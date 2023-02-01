package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

/**
 * 보드 LED 설정 PDU
 * 
 * @author khk
 *
 */
public class BoardLEDOnOffPDU extends RequestPDUBase {
	
	private String use;
	
	public BoardLEDOnOffPDU(String dest, String transferType, String use) {
		super(dest, transferType, COMMAND_ID_BOARD_LED);
		this.use = use;
	}

	@Override
	public String encode() {
		return encode(use);
	}
}
