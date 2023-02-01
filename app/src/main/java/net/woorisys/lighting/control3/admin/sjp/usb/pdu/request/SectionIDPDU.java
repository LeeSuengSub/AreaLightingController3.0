package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

/**
 * 구역 설정 PDU
 * 
 * @author hslim
 */
public class SectionIDPDU extends RequestPDUBase {
	
	/** 구역 ID */
	private String sectionId;

	public SectionIDPDU(String dest, String transferType, String sectionId) {
		super(dest, transferType, COMMAND_ID_SECTION_ID);

		this.sectionId = sectionId;
	}

	@Override
	public String encode() {
		return encode(sectionId);
	}
}
