package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

public class GroupDeletePDU extends RequestPDUBase {

	public GroupDeletePDU(String dest, String transferType) {
		super(dest, transferType, RequestPDUBase.COMMAND_ID_GROUP_DELETE);
	}

	@Override
	public String encode() {
		return encode(null);
	}
}
