package net.woorisys.lighting.control3.admin.sjp.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;

public abstract class AbstractUSBDeviceManager {
	
	/** UsbDeviceConnection */
	protected UsbDeviceConnection connection = null;

	/** USB 인터페이스: 데이터 전송용 */
	protected UsbInterface intf = null;
	
	/** USB 수신 데이터 버퍼 */
	protected byte[] recvBuffer = new byte[1024];

	/** USB 송신 데이터 버퍼 */
	protected byte[] sendBuffer = new byte[1024];

	protected Handler handler = null;

	protected UsbDevice usbDevice = null;
	
	protected UsbManager usbManager = null;
	
	protected byte[] blankBytes = new byte[0];
	
	public Handler getUIHandler() {
		return handler;
	}

	/**
	 * UI Handler로 메시지를 전송한다.
	 * @param what
	 */
	protected void sendMessageToHandler(int what) {
		Message message = handler.obtainMessage();
		message.what = what;
		handler.sendMessage(message);
	}

	/**
	 * USB 장비 연결 상태를 리턴한다.
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return null != connection;
	}

	/**
	 * USB 연결을 종료한다.
	 */
	public abstract void close();
	
	/**
	 * 장비 초기화 및 수신 쓰레드 시작
	 * 
	 * @param device
	 * @param usbManager
	 * @return
	 * @throws USBTerminalException
	 */
	abstract public boolean initDevice(Context context, UsbManager usbManager, UsbDevice device, SerialSettings serialSettings) throws USBTerminalException;
	
	/**
	 * 모뎀 상태를 요청하여 응답 값을 리턴한다.
	 */
	abstract public ModemControlLineStatus getModemStatus();
	
	/**
	 * 보드레이트를 읽어 온다.
	 * 
	 * @return
	 */
	abstract public int getBaudrate();
	
	/**
	 * 보드레이트를 설정 한다.
	 * @param baudRateSettings
	 * @return
	 */
	abstract public boolean setBaudrate(int baudRateSettings);
	
	/**
	 * Line Control를 읽어 온다.
	 * 
	 * @return
	 */
	abstract public int getLineControl();
	
	/**
	 * Line Control를 설정한다.
	 * 
	 * @return
	 * @throws USBTerminalException 
	 */
	abstract public boolean setLineControl(float stopBits, String parity, int dataBits) throws USBTerminalException;
	
	/**
	 * Flow control 설정 값을 읽어 온다.
	 * @return
	 */
	abstract public int[] getFlowControl();
	
	/**
	 * Handshaking 모드를 설정한다.
	 * 
	 * @param mode
	 * @return
	 */
	abstract public boolean setFlowControl(String mode);
	
	/**
	 * 모뎀 상태를 설정한다.
	 * 
	 * @param dtrState
	 * @param rtsState
	 * @return
	 */
	abstract public boolean setModemControl(boolean dtrState, boolean rtsState);
	
	/**
	 * 인자로주어진 문자열을 전송한다.
	 * 
	 * @return
	 */
	abstract public void send(String message);
	
	/**
	 * 인자로주어진 바이트 배열을 전송한다.
	 * 
	 * @return
	 */
	abstract public void send(byte[] message);
	
	/**
	 * USB 장비로 부터 데이터를 수신한다.
	 * 
	 * @return 수신 에러 발생 시 null을 리턴한다.
	 */
	abstract public byte[] recv();
	
	abstract public int recv(byte[] buffer);
}
