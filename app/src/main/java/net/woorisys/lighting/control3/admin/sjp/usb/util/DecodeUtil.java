package net.woorisys.lighting.control3.admin.sjp.usb.util;

public class DecodeUtil {
	
	public static long decodeInt4(byte b1, byte b2, byte b3, byte b4) {
		long ret = 0;
		
		ret |= (b4 << 24) & 0x00000000FF000000;
		ret |= (b3 << 16) & 0x0000000000FF0000;
		ret |= (b2 << 8)  & 0x000000000000FF00;
		ret |= b1         & 0x00000000000000FF;
		
		return ret;
	}
	
	/**
	 * @param b1
	 * @param b2
	 * @return
	 */
	public static int decodeInt2(byte b1, byte b2) {
		int ret = 0;
		
		ret |= (b2 << 8) & 0x000000000000FF00;
		ret |= b1        & 0x00000000000000FF;
		
		return ret;
	}
	
	public static String decodeString(byte[] bytes, int start, int length) {
		int strLength = 0;
		
		for (int i = start; i < length; i++) {
			if (0 == bytes[i]) {
				strLength = i - start + 1;
				break;
			}
		}

		return new String(bytes, start, strLength);
	}
	
	/**
	 * 
	 * 
	 * @param value
	 * @param bitIndex
	 * @return
	 */
	public static byte decodeBit(byte value, int bitIndex) {
		short mask = (short) (1 << bitIndex);
		System.out.format("decodeBit bitIndex = %d, Mask = %x", bitIndex, mask);
		return (byte) ((value | mask) >> bitIndex);
	}
}
