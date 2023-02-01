package net.woorisys.lighting.control3.admin.sjp.usb.util;

public class NumberUtil {
	
	/**
	 * 인자로 주어진 10진수 문자열을 16진수로 변경한다.
	 * 
	 * @param from
	 * @return
	 */
	public static final String convert2Hex(String from) {
		int destInteger = Integer.parseInt(from);
		return String.format("%08x", destInteger);
	}

	/**
	 * 인자로 주어진 16진수 문자열을 10진수로 변경한다.
	 * 
	 * @param from
	 * @return
	 */
	public static final String convert2Decimal(String from) {
		int destInteger = Integer.parseInt(from, 16);
		return String.format("%04d", destInteger);
	}
}
