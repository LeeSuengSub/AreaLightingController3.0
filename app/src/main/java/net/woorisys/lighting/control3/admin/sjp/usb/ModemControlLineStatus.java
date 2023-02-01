package net.woorisys.lighting.control3.admin.sjp.usb;

/**
 * 모뎀 제어 라인 상태 
 * 
 * @author hslim
 *
 */
public class ModemControlLineStatus {
	/**
	 * CTS(Clear To Send): 모뎀이 수신 할 수 있음을 알림
	 */
	private boolean ctsStatus;
	
	/**
	 * DSR(Data Set Ready): 모뎀 동작 가능 상태를 알림
	 */
	private boolean dsrStatus;
	
	/**
	 * CD(Carrier Detect): 모뎀이 전송한 캐리어를 검출 하였음을 알림
	 */
	private boolean cdStatus;
	
	/**
	 * RI(Ring Indicator): 모뎀이 회선에서 호출 신호 받을 시 알림
	 */
	private boolean riStatus;

	public ModemControlLineStatus() {
		ctsStatus = false;
		dsrStatus = false;
		cdStatus = false;
		riStatus = false;
	}
	
	public ModemControlLineStatus(boolean ctsStatus, boolean dsrStatus, boolean cdStatus, boolean riStatus) {
		super();
		this.ctsStatus = ctsStatus;
		this.dsrStatus = dsrStatus;
		this.cdStatus = cdStatus;
		this.riStatus = riStatus;
	}

	public boolean getCTSStatus() {
		return ctsStatus;
	}

	public void setCTSStatus(boolean ctsStatus) {
		this.ctsStatus = ctsStatus;
	}

	public boolean getDSRStatus() {
		return dsrStatus;
	}

	public void setDSRStatus(boolean dsrStatus) {
		this.dsrStatus = dsrStatus;
	}

	public boolean getCDStatus() {
		return cdStatus;
	}

	public void setCDStatus(boolean cdStatus) {
		this.cdStatus = cdStatus;
	}

	public boolean getRIStatus() {
		return riStatus;
	}

	public void setRIStatus(boolean riStatus) {
		this.riStatus = riStatus;
	}

	@Override
	public String toString() {
		return "ModemControlLineStatus [ctsStatus=" + ctsStatus + ", dsrStatus=" + dsrStatus + ", cdStatus=" + cdStatus
				+ ", riStatus=" + riStatus + "]";
	}
}
