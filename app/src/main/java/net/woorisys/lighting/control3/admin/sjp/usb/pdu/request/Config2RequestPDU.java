package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

/**
 * LED 설정(config)요청 PDU
 * 
 * @author hslim
 * 
 */
public class Config2RequestPDU extends RequestPDUBase {
	
	public Config2RequestPDU(String dest) {
		super(dest, TRANSFER_TYPE_UNICAST, COMMAND_ID_CONFIG2_REQ);
	}

	@Override
	public String encode() {
		return encode(null);
	}
}
