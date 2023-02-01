package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

/**
 * 구역 설정 PDU
 * 
 * @author hslim
 */
public class DongCannelPDU extends RequestPDUBase {
	
	/** 구역 ID */
	private String cannel;

	public DongCannelPDU(String cannel) {
		super("xxxx", TRANSFER_TYPE_UNICAST, COMMAND_ID_DONG_CHANNEL);
		this.cannel = cannel;
	}

	@Override
	public String encode() {
		return encode(cannel);
	}
}
