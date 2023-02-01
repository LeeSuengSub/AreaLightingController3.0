package net.woorisys.lighting.control3.admin.sjp.usb.pdu.response;


import net.woorisys.lighting.control3.admin.sjp.usb.util.NumberUtil;

/**
 * 그룹 정보 응답 PDU
 * 
 * @author hslim
 * 
 */
public class GroupResponsePDU extends ResponsePDUBase {
	
	String[] groupItems;

	public GroupResponsePDU() {
	}

	/**
	 * @return the groupItems
	 */
	public String[] getGroupItems() {
		return groupItems;
	}

	@Override
	public boolean decode(String message) {
		if (decodeInternal(message)) {
			// SUCCESS 인 경우만 내용이 있음
			if (result && null != rawContent) {
				String[] fields = rawContent.split(SEPARATOR);
				if (fields.length == 0) {
					System.out.println("Invalid number of fields.(number of fields = "
									+ fields.length + ")");
					return false;
				}

				int length = Integer.parseInt(fields[1]);

				if (length < 0) {
					System.out.println("Invalid length field.(length field = " + length + ")");
					return false;
				}

				if (length != (fields.length - 2)) {
					System.out.println("Invalid length field.(length field = "
							+ length + ", received group items = "
							+ (fields.length - 2) + ")");
					return false;
				}

				groupItems = new String[length];

				for (int i = 0; i < length; i++) {
					// 첫번째 필드는 전체 개수
					groupItems[i] = NumberUtil.convert2Decimal(fields[i + 2]);
				}
			}

			return true;
		}

		return false;
	}
}
