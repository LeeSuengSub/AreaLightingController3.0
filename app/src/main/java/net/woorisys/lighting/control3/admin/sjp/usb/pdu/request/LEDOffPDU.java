package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

/**
 * LED 제어 OFF PDU
 * 
 * @author hslim
 */
public class LEDOffPDU extends RequestPDUBase {
	
	public LEDOffPDU(String dest, String transferType) {
		super(dest, transferType, RequestPDUBase.COMMAND_ID_OFF);
	}

	@Override
	public String encode() {
		return encode(null);
	}
}
