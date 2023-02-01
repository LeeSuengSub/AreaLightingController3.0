package net.woorisys.lighting.control3.admin.sjp.usb.pdu;

/**
 * 요청 및 응답 PDU의 상위 클래스로 공통 상수들을 정의한다.
 * 
 * @author hslim
 *
 */
public class PDU {
	
	/**
	 * PDU의 긑을 가리키는 문자열
	 */
	protected static final String ETX = ";";
	
	/**
	 * 필드 구분자
	 */
	protected final static String SEPARATOR = ",";
	
	/**
	 * ON
	 */
	protected final static String ON = "1";
	
	/**
	 * OFF
	 */
	protected final static String OFF = "0";
}
