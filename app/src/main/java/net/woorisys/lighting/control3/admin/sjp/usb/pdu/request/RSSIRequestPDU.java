package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

/**
 * 가장 가까운 노드 요청 PDU
 * 
 * @author hslim
 * 
 */
public class RSSIRequestPDU extends RequestPDUBase {

	public RSSIRequestPDU() {
		super(RequestPDUBase.ADDRESS_BCAST, TRANSFER_TYPE_BROADCAST,
				COMMAND_ID_RSSI_REQ);
	}

	@Override
	public String encode() {
		return encode(null);
	}
}
