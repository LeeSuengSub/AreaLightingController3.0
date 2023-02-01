package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

/**
 * 그룹 정보 요청 PDU
 * 
 * @author hslim
 * 
 */
public class GroupRequestPDU extends RequestPDUBase {
	
	public GroupRequestPDU(String dest) {
		super(dest, TRANSFER_TYPE_UNICAST, COMMAND_ID_GROUP_REQ);
	}

	@Override
	public String encode() {
		return encode(null);
	}
}
