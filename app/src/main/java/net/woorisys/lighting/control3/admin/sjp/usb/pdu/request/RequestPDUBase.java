package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

import android.util.Log;

import net.woorisys.lighting.control3.admin.sjp.usb.pdu.PDU;

/**
 * 요청 PDU 기본 구조 클래스
 */
public abstract class RequestPDUBase extends PDU {
	/**
	 * 전송 형태: UNICAST
	 */
	public static final String TRANSFER_TYPE_UNICAST = "U";

	/**
	 * 전송 형태: BROADCAST
	 */
	public static final String TRANSFER_TYPE_BROADCAST = "B";

	/**
	 * 브로드케스트 주소 
	 */
	public static final String ADDRESS_BCAST = "65535";
	
	/**
	 * LED ON Command ID
	 */
	public static final String COMMAND_ID_ON = "ON";

	/**
	 * LED OFF Command ID
	 */
	public static final String COMMAND_ID_OFF = "OFF";

	/**
	 * 구역 설정(그룹과 다른 의미임, 1 ~ 5이 값이 설정됨) Command ID
	 */
	public static final String COMMAND_ID_SECTION_ID = "SECTION_ID";

	/**
	 * LED 설정 Command ID
	 */
	public static final String COMMAND_ID_CONFIG = "CONFIG";

	/**
	 * LED 설정 요청 Command ID
	 */
	public static final String COMMAND_ID_CONFIG_REQ = "CONFIG_REQ";

	/**
	 * LED GROUP 설정 요청 Command ID
	 */
	public static final String COMMAND_ID_GROUP = "GROUP";

	/**
	 * LED GROUP 설정 정보 요청 Command ID
	 */
	public static final String COMMAND_ID_GROUP_REQ = "GROUP_REQ";
	
	/**
	 * LED GROUP 삭제 요청 Command ID
	 */
	public static final String COMMAND_ID_GROUP_DELETE = "GROUP_DELETE";
		
	/**
	 * 가장 가까운 노드 요청 Command ID
	 */
	public static final String COMMAND_ID_RSSI_REQ = "RSSI_REQ";
	
	/**
	 * 동글 채널 변경 요청 Command ID
	 */
	public static final String COMMAND_ID_DONG_CHANNEL = "DONG_CHANNEL";
	
	/**
	 * 보드 LED 설정 요청 Command ID
	 */
	public static final String COMMAND_ID_BOARD_LED = "BOARD_LED";
	
	/**
	 * 보드 채널 설정 요청 Command ID
	 */
	public static final String COMMAND_ID_CHANNEL = "CHANNEL";
	
	/**
	 * 인터럽트 설정 요청 Command ID
	 */
//	public static final String COMMAND_ID_INT_COUNT = "INT_COUNT";

	public static final String COMMAND_ID_INTER_COUNT="INT_COUNT";

	/**
	 * 버전 2.0 설정 요청 Command ID
	 */
	public static final String COMMAND_ID_CONFIG2_REQ = "CONFIG2_REQ";

	public static final String COMMAND_ID_GROUP_ENABLE="GROUP_ENABLE";

	public static final String COMMAND_ID_GROUP_DISABLE="GROUP_DISABLE";

	public static final String COMMAND_ID_GROUP_STATE_RES="GROUP_S_REQ";
	/**
	 * 대상 장비 4자리 MAC 주소
	 */
	private String dest;

	/**
	 * 전송 유형: UNICAST / BROADCAST
	 */
	protected String transferType;

	/**
	 * 명령 식별자(ON/OFF/SECTION_ID/CONFIG/....)
	 */
	private String commandID;



	/**
	 * @param dest 목적지 주소, 브로드케스트인 경우 NULL 입력
	 * @param transferType 전송형태
	 * @param commandID
	 */
	public RequestPDUBase(String dest, String transferType, String commandID) {
		super();
		this.dest = dest;
		this.transferType = transferType;
		this.commandID = commandID;

		Log.d("SJP_PDU","REQUEST PDU BASE");
		
		// 브로드케스트인 경우 주소는 FFFF로 설정되도록 한다.
		if(transferType.equals(TRANSFER_TYPE_BROADCAST)) dest = ADDRESS_BCAST;
	}

	/**
	 * @return the dest
	 */
	public String getDest() {
		return dest;
	}

	/**
	 * @param dest the dest to set
	 */
	public void setDest(String dest) {
		this.dest = dest;
	}

	/**
	 * @return the transferType
	 */
	public String getTransferType() {
		return transferType;
	}

	/**
	 * @param transferType the transferType to set
	 */
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	/**
	 * @return the commandID
	 */
	public String getCommandID() {
		return commandID;
	}

	/**
	 * @param commandID the commandID to set
	 */
	public void setCommandID(String commandID) {
		this.commandID = commandID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RequestPDUBase [dest=" + dest + ", transferType=" + transferType + ", commandID=" + commandID + "]";
	}

	/**
	 * Dest Device ID, 전송 형태, Command ID, 인자로 주어진 VALUE, ETX를 인코딩한다.
	 * 
	 * @return
	 */
	protected String encode(String value) {
		StringBuilder sb = new StringBuilder();
		sb.append(dest);
		sb.append(SEPARATOR);
		sb.append(transferType);
		sb.append(SEPARATOR);
		sb.append(commandID);

		if (value != null) {
			sb.append(SEPARATOR);
			sb.append(value);
		}

		sb.append(ETX);

		Log.d("LEDControlSetActivity", sb.toString());
		return sb.toString();
	}

	abstract public String encode();
}
