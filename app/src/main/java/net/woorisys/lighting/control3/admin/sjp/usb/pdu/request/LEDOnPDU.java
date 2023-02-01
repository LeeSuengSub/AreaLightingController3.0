package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

/**
 * LED 제어 ON PDU
 * 
 * @author hslim
 * 
 */
public class LEDOnPDU extends RequestPDUBase {
	
	public LEDOnPDU(String dest, String transferType) {
		super(dest, transferType, RequestPDUBase.COMMAND_ID_ON);
	}

	@Override
	public String encode() {
		return encode(null);
	}
}
