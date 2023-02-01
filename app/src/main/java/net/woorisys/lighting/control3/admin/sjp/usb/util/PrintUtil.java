package net.woorisys.lighting.control3.admin.sjp.usb.util;


/**
 * 바이트 배열을 16진수 및 2진수로 출력한다.
 * 
 * @author hslim
 *
 */
public class PrintUtil {
	
	/**
	 * int 타입과 byte 타입간의 비트 개수 차이
	 */
	static int DIFF = Integer.SIZE - Byte.SIZE;
	
	/**
	 * 16진수 문자열 출력 시 바이트 단위 구분 문자열
	 */
	static String HEX_PRINT_SEPARATOR = " ";
	
	/**
	 * 바이너리 문자열 출력 시 바이트 단위 구분 문자열
	 */
	static String BIN_PRINT_SEPARATOR = " ";
	
	/**
	 * 인자로 주어진 바이트 배열을 16진수 표현 문자열로 변환한다.
	 * 
	 * @param buffer 변환 대상 바이트 배열
	 * @return 16진수 표현 문자열
	 */
	public static String bytesToHEXString(byte[] buffer) {
		StringBuilder str = new StringBuilder();
		
		for (byte b: buffer) 
			str.append(String.format("%02X", b)).append(HEX_PRINT_SEPARATOR);

		return str.toString();
	}
	
	/**
	 * 인자로 주어진 바이트 배열을 2진수 표현 문자열로 변환한다.
	 * 
	 * @param buffer 변환 대상 바이트 배열
	 * @return 2진수 표현 문자열
	 */
	public static String bytesToBINString(byte[] buffer) {
		StringBuilder str = new StringBuilder();
		
		for (byte b : buffer) {
			int bValue = b & 0x000000FF;
			
			// toBinaryString은 앞에 0을 출력하지않으므로 O의 개수를 구하여 추가해준다.
			// 자바에서 바이트는 signed 이므로int로 바로 케스팅 하면 부호 값이 적용되어
			// -128 ~ 127사이의 값으로 변환된다.
			// 다음과 같이 비트 연산을 한 후에 값을 변환하면, ubsigned byte 값을 얻을 수 있다.
			for (int i = 0; i < Integer.numberOfLeadingZeros(bValue & 0x000000FF)-DIFF; i++)
				str.append("0");
			
			// 10진수를 2진수로 변환, 원래 값이 바이트 임
			str.append(Integer.toBinaryString(bValue)).append(BIN_PRINT_SEPARATOR);
		}

		return str.toString();
	}
	
	public static void main(String... args) {
		int a = (int)((byte)255 & 0x000000FF);
		int b = (byte)128 & 0x000000FF;
		int c = (byte)127;
		int d = (byte)64;
		
		byte ba = (byte)255;
		byte bb = (byte)128;
		byte bc = (byte)127;
		byte bd = (byte)64;
		
		System.out.println("a = " + Integer.toString(a));
		System.out.println("b = " + Integer.toString(b));
		System.out.println("c = " + Integer.toString(c));
		System.out.println("d = " + Integer.toString(d));
		
		System.out.println("ba = " + PrintUtil.bytesToBINString(new byte[]{ba}));
		System.out.println("bb = " + PrintUtil.bytesToBINString(new byte[]{bb}));
		System.out.println("bc = " + PrintUtil.bytesToBINString(new byte[]{bc}));
		System.out.println("bd = " + PrintUtil.bytesToBINString(new byte[]{bd}));
	}
}
