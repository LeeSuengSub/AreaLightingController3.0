package net.woorisys.lighting.control3.admin.sjp.usb.pdu.response;

import net.woorisys.lighting.control3.admin.sjp.usb.util.NumberUtil;

/**
 * 가장 가까운 노드 응답 PDU
 * 
 * @author hslim
 *
 */
public class RSSIResponsePDU extends ResponsePDUBase {
	
	/** 근접 노드 목록 */
	private String[] nodes;

	public RSSIResponsePDU() {
	}

	/**
	 * @return the nodes
	 */
	public String[] getNodes() {
		return nodes;
	}

	@Override
	public boolean decode(String message) {
		if (decodeInternal(message)) {
			// SUCCESS 인 경우만 내용이 있음
			if (result) {
				String[] fields = rawContent.split(SEPARATOR);

				if (fields.length == 0) {
					System.out.println("Invalid number of fields.(number of fields = " + fields.length + ")");
					return false;
				}

				int length = Integer.parseInt(fields[1]);
				if (length < 0) {
					System.out.println("Invalid length field.(length field = " + length + ")");
					return false;
				}

				if (length != (fields.length - 2)) {
					System.out.println("Invalid length field.(length field = " + length + ", received node items = "
							+ (fields.length - 2) + ")");
					return false;
				}

				nodes = new String[length];

				for (int i = 0; i < length; i++) {
					// 첫번째 필드는 전체 개수
					nodes[i] = NumberUtil.convert2Decimal(fields[i + 2]);
				}
			}

			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		System.err.println(" = > "+nodes);
		if (nodes != null) {
			for (String node : nodes)
				sb.append(node).append("\n");
		} else {
			System.err.println("Null");
			sb.append("수집된 정보가 없습니다.");
		}
		
		return sb.toString();
	}
}
