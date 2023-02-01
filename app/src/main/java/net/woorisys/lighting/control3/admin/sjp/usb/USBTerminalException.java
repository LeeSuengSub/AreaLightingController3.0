package net.woorisys.lighting.control3.admin.sjp.usb;

/**
 * 내부 예외 처리 클래스
 * 
 * @author hslim
 *
 */
public class USBTerminalException extends Exception {
	
	/**
	 * 직렬화를 위한 serial version id
	 */
	private static final long	serialVersionUID	= -3327015735930423302L;

	public USBTerminalException() {
		super();
	}

	public USBTerminalException(String message) {
		super(message);
	}
}
