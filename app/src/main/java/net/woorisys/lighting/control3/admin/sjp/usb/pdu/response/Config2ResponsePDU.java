package net.woorisys.lighting.control3.admin.sjp.usb.pdu.response;


/**
 * LED 2.0 설정(config) 응답(Response) PDU
 * 
 * @author khk
 *
 */
public class Config2ResponsePDU extends ResponsePDUBase {
	
	private String version;
	private boolean ledStatus;
	private String channel;
	private boolean SensorUsing;
	private String disableTime;
	private String interruptCount;
	private boolean interruptFlag;

	public Config2ResponsePDU() {
	}
	

	@Override
	public boolean decode(String message) {
		if (decodeInternal(message)) {
			if (result) { // SUCCESS 인 경우만 내용이 있음
				String[] fields = rawContent.split(SEPARATOR);
				
				if (fields.length != 8) {
					System.out.println("Invalid number of fields.(number of fields = " + fields.length + ")");
					return false;
				}
				
				version = fields[1];
				ledStatus = fields[2].equals(ON) ? true : false;
				channel = fields[3];
				SensorUsing = fields[4].equals(ON) ? true : false;
				disableTime = fields[5];
				interruptCount=fields[6];
				interruptFlag=fields[7].equals(ON)?true:false;


			}

			return true;
		}

		return false;
	}
	@Override
	public String toString() {
		return "버전 : " + version 
				+ "\n보드 LED 상태 : " + (ledStatus ? "사용" : "미사용")
				+ "\n채널 : " + channel 
				+ "\n센서 사용여부 : " + (SensorUsing ? "사용" : "미사용")
				+ "\n센서 Disable 시간 : " + disableTime
				+ "\n인터럽트 카운트 : " + interruptCount
				+ "\n인터럽트 플레그, : "+(interruptFlag?"Enable":"Disable");

	}
	
}
