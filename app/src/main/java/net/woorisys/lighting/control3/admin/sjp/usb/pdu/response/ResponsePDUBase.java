package net.woorisys.lighting.control3.admin.sjp.usb.pdu.response;

import net.woorisys.lighting.control3.admin.sjp.usb.pdu.PDU;

/**
 * 응답 PDU 기본 구조 클래스
 * 
 * @author hslim
 * 
 */
public abstract class ResponsePDUBase extends PDU {

	/** 처리 성공 응답 해더 */
	public static final String RESULT_SUCCESS = "SUCCESS";

	/**
	 * 처리 실패 응답 해더
	 */
	public static final String RESULT_FAIL = "FAIL";

	/**
	 * LED 설정 요청 응답 메시지 ID
	 */
	public static final String MESSAGE_ID_CONFIG_REQ = "CONFIG_RES";

	/**
	 * 2.2.11 가장 가까운 노드 응답 메시지 ID
	 */
	public static final String MESSAGE_ID_RSSI_RES = "RSSI_RES";

	/** 처리 결과: SUCCESS / FAIL, SUCCESS 일경우 true로 설정된다. */
	protected boolean result;

	/** 응답 메시지 내용: 파싱되지않은 문자열 형태 */
	protected String rawContent;

	/**
	 * @return the result
	 */
	public boolean getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

	/**
	 * @return the rawContent
	 */
	public String getRawContent() {
		return rawContent;
	}

	/**
	 * @param rawContent the rawContent to set
	 */
	public void setRawContent(String rawContent) {
		this.rawContent = rawContent;
	}

	/**
	 * 인자로 주어진 메시지를 파싱한다. 결과 필드, 컨텐츠 부분로 시작하여 ETX가지 분리한다. 결과 필드외에
	 */
	public boolean decodeInternal(String message) {
		if (!message.endsWith(ETX))
			return false;

		String resultString = null;

		int indexOfFirstSeparator = message.indexOf(SEPARATOR);
		if (indexOfFirstSeparator != -1) {
			resultString = message.substring(0, indexOfFirstSeparator);
			rawContent = message.substring(indexOfFirstSeparator + 1, (message.length() - 1));
		} else {
			resultString = message.substring(0, message.length() - 1);
		}

		System.out.println("Result: " + resultString);

		if (!resultString.equals(RESULT_SUCCESS) && !resultString.equals(RESULT_FAIL))
			return false;

		result = resultString.equals(RESULT_SUCCESS);

		System.out.println(toDebugString());

		return true;
	}

	abstract public boolean decode(String message);

	public String toDebugString() {
		return "ResponsePDUBase [result=" + result + ", rawContent=" + rawContent + "]";
	}
}
