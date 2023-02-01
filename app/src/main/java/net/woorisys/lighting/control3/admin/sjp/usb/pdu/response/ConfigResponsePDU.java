package net.woorisys.lighting.control3.admin.sjp.usb.pdu.response;

/**
 * LED 설정(config)응답(Response) PDU
 * 
 * @author hslim
 *
 */
public class ConfigResponsePDU extends ResponsePDUBase {
	
	/** 구역 ID, 브로드 케스트일 경우만 사용 */
	private String sectionID;

	/** 최대 밝기 */
	private int maxBrightness;

	/** 대기밝기 */
	private int standByBrightness;

	/** 유지 시간 */
	private int autoOffDelayTime;

	/** ON 디밍시간 */
	private int onDimmingTime;

	/** OFF 디밍시간 */
	private int offDimmingTime;

	/** 감도 조절 값 */
	private int sensitivity;

	/** Broad cast 사용 여부 */
	private boolean useBroadcastGroupFilter;

	public ConfigResponsePDU() {
	}

	/**
	 * @return the sectionID
	 */
	public String getSectionID() {
		return sectionID;
	}

	/**
	 * @return the maxBrightness
	 */
	public int getMaxBrightness() {
		return maxBrightness;
	}

	/**
	 * @return the standByBrightness
	 */
	public int getStandByBrightness() {
		return standByBrightness;
	}

	/**
	 * @return the autoOffDelayTime
	 */
	public int getAutoOffDelayTime() {
		return autoOffDelayTime;
	}

	/**
	 * @return the onDimmingTime
	 */
	public int getOnDimmingTime() {
		return onDimmingTime;
	}

	/**
	 * @return the offDimmingTime
	 */
	public int getOffDimmingTime() {
		return offDimmingTime;
	}

	/**
	 * @return the sensitivity
	 */
	public int getSensitivity() {
		return sensitivity;
	}

	/**
	 * @return the useBroadcastSectionFilter
	 */
	public boolean isUseBroadcastGroupFilter() {
		return useBroadcastGroupFilter;
	}

	@Override
	public boolean decode(String message) {
		if (decodeInternal(message)) {
			// SUCCESS 인 경우만 내용이 있음
			if (result) {
				String[] fields = rawContent.split(SEPARATOR);

				if (fields.length != 9) {
					System.out.println("Invalid number of fields.(number of fields = " + fields.length + ")");
					return false;
				}

				maxBrightness = Integer.parseInt(fields[1]);
				standByBrightness = Integer.parseInt(fields[2]);
				autoOffDelayTime = Integer.parseInt(fields[3]);
				onDimmingTime = Integer.parseInt(fields[4]);
				offDimmingTime = Integer.parseInt(fields[5]);
				sensitivity = Integer.parseInt(fields[6]);
				useBroadcastGroupFilter = fields[7].equals(ON) ? true : false;
				sectionID = fields[8];
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
		return "구역 ID: " + sectionID + "\n최대 밝기: " + maxBrightness
				+ "\n대기 밝기: " + standByBrightness + "\n유지시간: " + autoOffDelayTime
				+ "\nON 디밍 시간(초): " + onDimmingTime + "\nOFF 디밍 시간(초): " + offDimmingTime + "\n감도수준: "
				+ sensitivity + "\nBroadcast Group Filter: " + (useBroadcastGroupFilter?"사용":"미사용");
	}
	
}
