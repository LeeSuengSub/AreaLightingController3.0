package net.woorisys.lighting.control3.admin.sjp.usb.CP2101;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import net.woorisys.lighting.control3.admin.sjp.usb.AbstractUSBDeviceManager;
import net.woorisys.lighting.control3.admin.sjp.usb.ModemControlLineStatus;
import net.woorisys.lighting.control3.admin.sjp.usb.SerialSettings;
import net.woorisys.lighting.control3.admin.sjp.usb.USBCommonConstants;
import net.woorisys.lighting.control3.admin.sjp.usb.USBConstants;
import net.woorisys.lighting.control3.admin.sjp.usb.USBTerminalException;
import net.woorisys.lighting.control3.admin.sjp.usb.util.DecodeUtil;
import net.woorisys.lighting.control3.admin.sjp.usb.util.PrintUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * USB 장비와 통신을 초기화 하고 데이터 수신 스레드를 생성하고 관리한다.
 * 
 * @author hslim
 */
public class CP210XUSBDeviceManager extends AbstractUSBDeviceManager {
	
	public CP210XUSBDeviceManager(Context context, Handler handler, UsbManager usbManager) {
		this.usbManager = usbManager;
		this.handler = handler;
	}
	
	/**
	 * 모뎀 상태를 요청하여 응답 값을 리턴한다.
	 */
	public ModemControlLineStatus getModemStatus() {
		if (!isConnected())
			return null;

		byte[] usbMessage = new byte[1];
		usbMessage[0] = 64;

		connection.controlTransfer(CP210XConstants.GET_REQUEST_TYPE, CP210XConstants.GET_MDMSTS,
				0x0, 0, usbMessage, usbMessage.length, 0);

//		int dtr = 1 & usbMessage[0];
//		int rts = (1 << 1) & usbMessage[0];
		int cts = (1 << 4) & usbMessage[0];
		int dsr = (1 << 5) & usbMessage[0];
		int ri = (1 << 6) & usbMessage[0];
		int cd = (1 << 7) & usbMessage[0];

		ModemControlLineStatus status = new ModemControlLineStatus(1 == cts, 1 == dsr, 1 == ri, 1 == cd);

		return status;
	}

	/**
	 * 장비 초기화 및 수신 쓰레드 시작
	 * 
	 * @param device
	 * @return
	 * @throws USBTerminalException
	 */
	public boolean initDevice(Context context, UsbManager usbManager, UsbDevice device, SerialSettings serialSettings) throws USBTerminalException {
		System.out.println("initDevice");

		usbDevice = device;

		if (null == device)
			return false;

		// print(device);
		intf = device.getInterface(0);

		if (null == intf) {
			sendMessageToHandler(USBConstants.WHAT_DEVICE_NOT_CONNECTED);
			return false;
		}

		return initDeviceInternal(device, serialSettings);
	}

	/**
	 * 장비 초기화 및 수신 쓰레드 시작 내부 함수, 오픈 실패 시 재귀 호출을 위하여 분리함
	 * 
	 * @param device
	 * @return
	 * @throws USBTerminalException
	 */
	public boolean initDeviceInternal(UsbDevice device, SerialSettings serialSettings) throws USBTerminalException {
		System.out.println("initDeviceInternal");

		connection = usbManager.openDevice(device);
		if (null == connection) {
			System.out.println("USB Connection is null.");

			sendMessageToHandler(USBConstants.WHAT_DEVICE_NOT_CONNECTED);
			return false;
		}
				
		// 보드레이트를 읽어 온다.
		getBaudrate();

		// Line Control를 읽어 온다.
		getLineControl();

		// 보드레이트를 설정한다.
		if(!setBaudrate(serialSettings.baudRate))
			return false;
		
		// Line Control 설정: SET_LINE_CTL
		setLineControl(serialSettings.stopBits, serialSettings.parity, serialSettings.dataBits);
		getLineControl();
		
		// Flow 설정
		setFlowControl(serialSettings.handShaking);
		getFlowControl();
		
		// 인터페이스 활성화
		connection.claimInterface(intf, true);
		int ret = connection.controlTransfer(CP210XConstants.SET_REQUEST_TYPE,
				CP210XConstants.SET_IFC_ENABLE, 0x0001, 0, null, 0, 0);
		// 인터페이스 활성화 오류
		if (ret < 0) {
			close();
			sendMessageToHandler(USBConstants.WHAT_DEVICE_DISCONNECTED);
			return false;
		}

		// reset
		ret = connection.controlTransfer(CP210XConstants.SET_REQUEST_TYPE, CP210XConstants.RESET,
				0, 0, null, 0, 0);

		System.out.println("Initialized");
		return true;
	}

	/**
	 * 보드레이트를 읽어 온다.
	 * 
	 * @return
	 */
	public int getBaudrate() {
		byte[] usbMessage = new byte[4];
		connection.controlTransfer(CP210XConstants.GET_REQUEST_TYPE, CP210XConstants.GET_BAUDRATE,
				0x0, 0, usbMessage, usbMessage.length, 0);

		ByteBuffer buff = ByteBuffer.wrap(usbMessage);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer intBuff = buff.asIntBuffer();
		
		int baudRate = intBuff.get();
		System.out.println("Current Boadrate : " + baudRate);
		
		return baudRate;
	}
	
	/**
	 * 보드레이트를 설정 한다.
	 * @param baudRateSettings
	 * @return
	 */
	public boolean setBaudrate(int baudRateSettings) {
		ByteBuffer buff = ByteBuffer.allocate(4);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer intBuff = buff.asIntBuffer();

		// BaudRate 설정: SET_BAUDRATE
		int baudrate = CP210XConstants.DEFAULT_BAUDRATE;
		if (baudRateSettings > 0)
			baudrate = baudRateSettings;

		intBuff.put(baudrate);

		int ret = connection.controlTransfer(CP210XConstants.SET_REQUEST_TYPE,
				CP210XConstants.SET_BAUDRATE, 0x0, 0, buff.array(), 4, 0);

		if (ret < 0) {
			sendMessageToHandler(USBConstants.WHAT_DEVICE_NOT_CONNECTED);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Line Control를 읽어 온다.
	 * 
	 * @return
	 */
	public int getLineControl() {
		byte[] usbMessage = new byte[2];

		connection.controlTransfer(CP210XConstants.GET_REQUEST_TYPE, CP210XConstants.GET_LINE_CTL,
				0x0, 0, usbMessage, usbMessage.length, 0);

		System.out.println("Current Line Control : " + PrintUtil.bytesToBINString(usbMessage));
		ByteBuffer buff = ByteBuffer.wrap(usbMessage);
		short lineControl = buff.asShortBuffer().get();
		
		System.out.println("Current Line Control : " + lineControl);
		
		return lineControl;
	}

	/**
	 * Line Control를 설정한다.
	 * 
	 * @return
	 * @throws USBTerminalException 
	 */
	public boolean setLineControl(float stopBits, String parity, int dataBits) throws USBTerminalException {
		int lineControl = makeLineControl(stopBits, parity, dataBits);
		System.out.println("SET LINE CONTRL : " + lineControl);
				
		int ret = connection.controlTransfer(CP210XConstants.SET_REQUEST_TYPE,
				CP210XConstants.SET_LINE_CTL, lineControl, 0, null, 0, 0);
		if (ret < 0) {
			sendMessageToHandler(USBConstants.WHAT_DEVICE_NOT_CONNECTED);
			System.out.println("WHAT_DEVICE_NOT_CONNECTED-2");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handshaking 모드를 설정한다.
	 * 
	 * @param mode
	 * @return
	 */
	public boolean setFlowControl(String mode) {
		ByteBuffer buff = ByteBuffer.allocate(16);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer intBuff = buff.asIntBuffer();

		int[] data = makeFlowControl(mode);
		intBuff.put(data);

		int ret = connection.controlTransfer(CP210XConstants.SET_REQUEST_TYPE,
				CP210XConstants.SET_FLOW, 0x0, 0, buff.array(), 16, 0);

		if (ret < 0) {
			sendMessageToHandler(USBConstants.WHAT_DEVICE_NOT_CONNECTED);
			return false;
		}
		
		return true;
	}
	
	/**
	 * 모뎀 Handshaking 상태를 설정한다.
	 * 
	 * @param dtrState
	 * @param rtsState
	 * @return
	 */
	public boolean setModemControl(boolean dtrState, boolean rtsState) {
		System.out.println("DTR:" + dtrState + ", RTS:" + rtsState);

		if (!isConnected())
			return false;

		int mhs = makeModemHandshakingStatus(dtrState, rtsState);

		int ret = connection.controlTransfer(CP210XConstants.SET_REQUEST_TYPE,
				CP210XConstants.SET_MHS, mhs, 0, null, 0, 0);
		if (ret < 0) {
			System.out.println("setMHS ERROR");
			return false;
		}

		return true;
	}
	
	/* CP2101_CONTROL */
	private static final int CONTROL_DTR	= 0x0001;
	private static final int CONTROL_RTS = 0x0002;

	private static final int CONTROL_WRITE_DTR = 0x0100;
	private static final int CONTROL_WRITE_RTS = 0x0200;
	
	/**
	 * 모뎀 Handshaking 상태 설정 바이트(LITTL_ENDIAN, LSB가 하위주소에 옮)을 생성한다.
	 * @param dtrState
	 * @param rtsState
	 * @return
	 */
	private static int makeModemHandshakingStatus(boolean dtrState, boolean rtsState) {
		int control = 0;

		if (rtsState) {
			control |= CONTROL_RTS;
			control |= CONTROL_WRITE_RTS;
		}
		
		if (dtrState) {
			control |= CONTROL_DTR;
			control |= CONTROL_WRITE_DTR;
		}
		
		if (!rtsState) {
			control &= ~CONTROL_RTS;
			control |= CONTROL_WRITE_RTS;
		}
		
		if (!dtrState) {
			control &= ~CONTROL_DTR;
			control |= CONTROL_WRITE_DTR;	
		}
		
		System.out.println("SET MHS : " + control);
		return control;
	}
	
	/**
	 * Line Control 제어 바이트들(LITTL_ENDIAN, LSB가 하위주소에 옮)을 생성한다.
	 * 
	 * @param stopBits Stop bits
	 * @param parity  parity
	 * @param dataBits data bits
	 * @return
	 * @throws USBTerminalException
	 */
	private int makeLineControl(float stopBits, String parity, int dataBits) throws USBTerminalException {
		/** 하위 바이트(LSB): bit 0 ~ 7 */
		byte lsb = 0;

		/** 상위 바이트(MSB): bit 8 ~ 15 */
		byte msb = 0;

		// bit 3 ~ 0
		if (1 == stopBits)
			lsb = 0;
		else if (1.5 == stopBits)
			lsb = 1;
		else if (2 == stopBits)
			lsb = 2;
		else
			throw new USBTerminalException("Invalid stop bits.");

		// bit 7 ~ 4
		if ("None".equals(parity)) // 0000
			lsb |= 0x00;
		else if ("Odd".equals(parity)) // 0001
			lsb |= 0x10;
		else if ("Even".equals(parity)) // 0010
			lsb |= 0x20;
		else if ("Mark".equals(parity)) // 0011
			lsb |= 0x30;
		else if ("Space".equals(parity)) // 0100
			lsb |= 0x40;
		else
			throw new USBTerminalException("Invalid parity.");

		if (dataBits < 5 || dataBits > 8)
			throw new USBTerminalException("Invalid data bits.");

		msb = (byte) dataBits;

		return DecodeUtil.decodeInt2(lsb, msb);
	}

	/**
	 * Flow control 설정 값을 읽어 온다.
	 * @return
	 */
	public int[] getFlowControl() {
		byte[] usbMessage = new byte[16];
		connection.controlTransfer(CP210XConstants.GET_REQUEST_TYPE, CP210XConstants.GET_FLOW, 0x0,
				0, usbMessage, usbMessage.length, 0);

		System.out.println("Get Flow = " + PrintUtil.bytesToBINString(usbMessage));

		ByteBuffer buff = ByteBuffer.wrap(usbMessage);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer intBuff = buff.asIntBuffer();

		int ulControlHandshake = intBuff.get();
		int ulFlowReplace = intBuff.get();
		int ulXonLimit = intBuff.get();
		int ulXoffLimit = intBuff.get();

		System.out.println("Get Flow2 = ulControlHandshake : " + ulControlHandshake + ", ulFlowReplace : " + ulFlowReplace);
		
		return new int[] { ulControlHandshake, ulFlowReplace, ulXonLimit, ulXoffLimit };
	}

	/**
	 * Flow Control 모드를 설정을 위한 데이터를 생성한다.
	 * @param mode
	 * @return
	 */
	private int[] makeFlowControl(String mode) {
		int[] flow = getFlowControl();

		int ulXonLimit = flow[2];
		int ulXoffLimit = flow[3];
		int ulControlHandshake = 0;
		int ulFlowReplace = 0;

		if ("None".equals(mode)) {
			ulControlHandshake |= 0x01; // 00000000 00000000 00000000 00000001 --> Big Endian 표시
			ulFlowReplace |= 0x40;      // 00000000 00000000 00000000 01000000 --> 전송 시 Little Endian으로 변경됨
		} else if ("RTS/CTS".equals(mode)) {
			ulControlHandshake |= 0x09; // 00000000 00000000 00000000 00001001
			ulFlowReplace |= 0x80;      // 00000000 00000000 00000000 10000000
		} else if ("XON/XOFF".equals(mode)) {
			ulFlowReplace |= 0x03;      // 00000000 00000000 00000000 00000011
		} else if ("RTS/CTS+XON/XOFF".equals(mode)) {
			ulControlHandshake |= 0x09; // 00000000 00000000 00000000 00001001
			ulFlowReplace |= 0x80;      // 00000000 00000000 00000000 10000011
			ulFlowReplace |= 0x03;
		}
		
		return new int[] { ulControlHandshake, ulFlowReplace, ulXonLimit, ulXoffLimit };
	}

	/**
	 * 인자로주어진 문자열을 전송한다.
	 * 
	 * @return
	 */
	public void send(String message) {
		if (null == connection)
			return;

		System.out.println("Send> " + message);

		byte[] send = message.getBytes();
		connection.bulkTransfer(intf.getEndpoint(1), send, send.length, 0);
	}

	/**
	 * 인자로주어진 바이트 배열을 전송한다.
	 * 
	 * @return
	 */
	public void send(byte[] message) {
		if (null == connection)
			return;

		System.out.println("Send> " + PrintUtil.bytesToHEXString(message));
		connection.bulkTransfer(intf.getEndpoint(1), message, message.length, 0);
	}

	/**
	 * USB 장비로 부터 데이터를 수신한다.
	 * 
	 * @return 수신 에러 발생 시 null을 리턴한다.
	 */
	public byte[] recv() {
		if (null == connection)
			return blankBytes;

		int readCnt = connection.bulkTransfer(intf.getEndpoint(0), recvBuffer, 1024,
				USBCommonConstants.USB_TIME_OUT);

		if (readCnt < 0) {
			System.out.println("sensor timeout: " + readCnt);
			return null;
		}

		System.out.println("Received bytes " + readCnt);

		byte[] ret = new byte[readCnt];
		System.arraycopy(recvBuffer, 0, ret, 0, readCnt);

		return ret;
	}
	
	public int recv(byte[] recvBuffer) {
		if (null == connection)
			return -1;

		int readCnt = connection.bulkTransfer(intf.getEndpoint(0), recvBuffer, recvBuffer.length,
				USBCommonConstants.USB_TIME_OUT);

		System.out.println("Received bytes " + readCnt);
		
		return readCnt;
	}
	
	/**
	 * USB 연결을 종료한다.
	 */
	@Override
	public void close() {
		System.out.println("Closing ....");
		setModemControl(false, false);

		if (null != intf)
			connection.releaseInterface(intf);

		connection.close();
		intf = null;
		usbDevice = null;
	}
}
