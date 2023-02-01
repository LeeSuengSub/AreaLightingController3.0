package net.woorisys.lighting.control3.admin.sjp.usb.pdu.response;

/**
 * 일반적으로 SUCCESS/FAIL 정보만 전달하는 PDU 처리
 * 
 * @author hslim
 * 
 */
public class NormalResponsePDU extends ResponsePDUBase {
	
	public NormalResponsePDU() {
	}

	@Override
	public boolean decode(String message) {
		return decodeInternal(message);
	}
}
