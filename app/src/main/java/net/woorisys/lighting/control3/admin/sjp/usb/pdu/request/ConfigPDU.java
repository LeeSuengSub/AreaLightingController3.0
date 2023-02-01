package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

/**
 * LED 설정(config) PDU
 * 
 * @author hslim
 */
public class ConfigPDU extends RequestPDUBase {
	
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
	
	/** Broadcast 사용 여부 */
	private boolean useBroadcastGroupFilter;
	
	/**
	 * UNICAST용 설정 PDU 생성
	 * 
	 * @param dest
	 * @param maxBrightness
	 * @param standByBrightness
	 * @param autoOffDelayTime
	 * @param onDimmingTime
	 * @param offDimmingTime
	 * @param sensitivity
	 * @param useBroadcastGroupFilter
	 */
	public ConfigPDU(String dest, int maxBrightness,
                     int standByBrightness, int autoOffDelayTime, int onDimmingTime, int offDimmingTime, int sensitivity,
                     boolean useBroadcastGroupFilter) {
		super(dest, TRANSFER_TYPE_UNICAST, COMMAND_ID_CONFIG);
		this.maxBrightness = maxBrightness;
		this.standByBrightness = standByBrightness;
		this.autoOffDelayTime = autoOffDelayTime;
		this.onDimmingTime = onDimmingTime;
		this.offDimmingTime = offDimmingTime;
		this.sensitivity = sensitivity;
		this.useBroadcastGroupFilter = useBroadcastGroupFilter;
	}
	
	/**
	 * BROADCAST용 설정 PDU 생성
	 * 
	 * @param dest 타깃 장비 MAC(브로드케스트일 경우 FFFF)
	 * @param sectionID 브로드케스트일 경우 특정 구역에만 브로드 케스팅 되도록 할 때 사용함
	 * @param maxBrightness
	 * @param standByBrightness
	 * @param autoOffDelayTime
	 * @param onDimmingTime
	 * @param offDimmingTime
	 * @param sensitivity
	 */
	public ConfigPDU(String dest, String sectionID, int maxBrightness,
                     int standByBrightness, int autoOffDelayTime, int onDimmingTime, int offDimmingTime, int sensitivity,
                     boolean useBroadcastGroupFilter) {
		super(dest, TRANSFER_TYPE_BROADCAST, COMMAND_ID_CONFIG);
		this.sectionID = sectionID;
		this.maxBrightness = maxBrightness;
		this.standByBrightness = standByBrightness;
		this.autoOffDelayTime = autoOffDelayTime;
		this.onDimmingTime = onDimmingTime;
		this.offDimmingTime = offDimmingTime;
		this.sensitivity = sensitivity;
		this.useBroadcastGroupFilter = useBroadcastGroupFilter;
	}

	@Override
	public String encode() {
		StringBuilder sb = new StringBuilder();

		if (TRANSFER_TYPE_BROADCAST == transferType)
			sb.append(sectionID).append(SEPARATOR);
		
		sb.append(maxBrightness).append(SEPARATOR);
		sb.append(standByBrightness).append(SEPARATOR);
		sb.append(autoOffDelayTime).append(SEPARATOR);
		sb.append(onDimmingTime).append(SEPARATOR);
		sb.append(offDimmingTime).append(SEPARATOR);
		sb.append(sensitivity).append(SEPARATOR);
		sb.append(useBroadcastGroupFilter ? "1":"0");
		
		return encode(sb.toString());
	}
}
