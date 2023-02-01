package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

/**
 * 채널 변경 설정 PDU
 * 
 * @author khk
 *
 */
public class ChannelSetPDU extends RequestPDUBase {
	
	private String channel;
	
	public ChannelSetPDU(String dest, String transferType, String channel) {
		super(dest, transferType, COMMAND_ID_CHANNEL);
		this.channel = channel;
	}

	@Override
	public String encode() {
		return encode(channel);
	}
}
