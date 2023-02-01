package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

/**
 * 그룹 설정 PDU
 * 
 * @author hslim
 *
 */
public class GroupPDU extends RequestPDUBase {
	
	/**
	 * 구역 ID
	 */
	private String sectionId;
	
	/**
	 * 그룹에 속한 장비 MAC 배열
	 */
	private String[] groupItems;
	
	/**
	 * GroupPDU 생성자
	 * 
	 * @param dest 목적지 주소
	 * @param transferType 전송 유형
	 * @param sectionId 구역 ID
	 * @param groupItems 그룹에 속한 장비 MAC 배열
	 */
	public GroupPDU(String dest, String transferType, String sectionId, String[] groupItems) {
		super(dest, transferType, COMMAND_ID_GROUP);
		this.groupItems = groupItems;
		this.sectionId = sectionId;
	}

	@Override
	public String encode() {
		StringBuilder sb = new StringBuilder();
		sb.append(sectionId).append(SEPARATOR);
		sb.append(groupItems.length);
		
		for (String item : groupItems)
			sb.append(SEPARATOR).append(item);
		
		return encode(sb.toString());
	}
}
