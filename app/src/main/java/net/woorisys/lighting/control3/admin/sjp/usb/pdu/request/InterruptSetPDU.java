package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

import android.util.Log;

/**
 * 인터럽트 설정 PDU
 * 
 * @author khk
 *
 */
public class InterruptSetPDU extends RequestPDUBase {
	
	private String count;
	
	public InterruptSetPDU(String dest, String transferType, String count) {
		super(dest, transferType, COMMAND_ID_INTER_COUNT);
		this.count = count;

		Log.d("SJP_DEFAULT_TAG",COMMAND_ID_INTER_COUNT);
	}

	@Override
	public String encode() {
		StringBuilder sb = new StringBuilder();
		sb.append(count);

		return encode(sb.toString());
	}
}
