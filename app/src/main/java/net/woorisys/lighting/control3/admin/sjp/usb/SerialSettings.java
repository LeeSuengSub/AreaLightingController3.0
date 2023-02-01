package net.woorisys.lighting.control3.admin.sjp.usb;

import android.content.SharedPreferences;

/**
 * 시리얼 통신을 위하여 필요한 세부 설정정보 관리 클래스
 * Preference 및 Intent로 주고 받는 값들을 로딩하는 메소들을 제공한다.
 * 
 * frame structure:
 * - start bit: 1 bit (always low)
 * - data bit: 0 ~ 8 bit
 * - parity bit: odd, even parity
 * - stop bit: 1 ~ 2 bit (always high)
 *
 * @author hslim
 */
public class SerialSettings {
	/**
	 * 설정 내용 등을 저장하는 Preference 이름
	 */
	public static final String SETTING_PREFERENCE_NAME = "settings";
	
	
	/**
	 * 메인 Activity 이동 시 전달하는 Baudrate 값의 키
	 */
	public static final String BAUDRATE = "Baudrate";
	
	/**
	 * 메인 Activity 이동 시 전달하는 Baudrate 값이 사용자 정의 값인지를 가리키는 키
	 */
	public static final String IS_CUSTOM_BAUDRATE = "IsCustomBaudrate";
	
	/**
	 * 메인 Activity 이동 시 전달하는 Data Bits 값의 키
	 */
	public static final String DATABITS = "DataBits";
	
	/**
	 * 메인 Activity 이동 시 전달하는 Parity 값의 키
	 */
	public static final String PARITY = "Parity";
	
	/**
	 * 메인 Activity 이동 시 전달하는 Stop Bits 값의 키
	 */
	public static final String STOPBITS = "StopBits";
	
	/**
	 * 메인 Activity 이동 시 전달하는 Handshaking 값의 키
	 */
	public static final String HANDSHAKING = "HandShaking";
	
	/**
	 * 메인 Activity 이동 시 전달하는 Byte Order 값의 키
	 */
	public static final String BYTEORDER = "ByteOrder";
	
	/**
	 * USB 장비로 부터 읽은 데이터의 파일 로깅 여부를 가리키는 값 
	 */
	public static final String ENABLE_FILE_LOGGING = "enableFileLogging";
	
	/**
	 * Little-Endian Byte order를 가리킴
	 */
	public static final int BYTEORDER_LITTLE_ENDIAN = 1;
	
	/**
	 * Big-Endian Byte order를 가리킴
	 */
	public static final int BYTEORDER_BIG_ENDIAN = 2;
	
	/**
	 * Baudrate 디폴트 값
	 */
	public static final int DEFAULT_SETTING_BAUDRATE = 38400;
	
	/**
	 * Data Bits 디폴트 값
	 */
	public static final int DEFAULT_SETTING_DATABITS = 8;
	
	/**
	 * Parity 디폴트 값
	 */
	public static final String DEFAULT_SETTING_PARITY = "None";
	
	/**
	 * Stop Bits 디폴트 값
	 */
	public static final int DEFAULT_SETTING_STOPBITS = 1;
	
	/**
	 * Handshaking 디폴트 값
	 */
	public static final String DEFAULT_SETTING_HANDSHAKING = "None";
	
	/**
	 * Byte Order 디폴트 값
	 */
	public static final int DEFAULT_SETTING_BYTEORDER = 1;
	
	/**
	 * Baudrate 값이 사용자 정의 값인 경우 true
	 */
	public boolean isCustomBaudrate = false;
	
	/**
	 * Baudrate 설정 값
	 */
	public int baudRate = DEFAULT_SETTING_BAUDRATE;
	
	/**
	 * Data Bits 설정 값
	 */
	public int dataBits = DEFAULT_SETTING_DATABITS;
	
	/**
	 * Parity 설정 값
	 */
	public String parity = DEFAULT_SETTING_PARITY;
	
	/**
	 * Stop Bits 설정 값
	 */
	public float stopBits = DEFAULT_SETTING_STOPBITS;
	
	/**
	 * Handshaking 설정 값
	 */
	public String handShaking = DEFAULT_SETTING_HANDSHAKING;
	
	public boolean enableFileLogging = false;
	
	/**
	 * 메인 Activity인 CP210XTermActivity의 Private Preference로 부터 설정 값을 읽어온다.
	 * 값들이 없는 경우 기존 값을 유지한다.
	 */
	public void loadPreferences(SharedPreferences sharedPreferences) {
		isCustomBaudrate = sharedPreferences.getBoolean(IS_CUSTOM_BAUDRATE, false);
		System.out.println(IS_CUSTOM_BAUDRATE + ":" + isCustomBaudrate);
	
		baudRate = sharedPreferences.getInt(BAUDRATE, SerialSettings.DEFAULT_SETTING_BAUDRATE);
		System.out.println(BAUDRATE + ":" + baudRate);
    
    	dataBits = sharedPreferences.getInt(DATABITS, SerialSettings.DEFAULT_SETTING_DATABITS);
    	System.out.println(DATABITS + ":" + dataBits);

    	parity = sharedPreferences.getString(PARITY, SerialSettings.DEFAULT_SETTING_PARITY);
    	System.out.println(PARITY + ":" + parity);

    	stopBits = sharedPreferences.getFloat(STOPBITS, SerialSettings.DEFAULT_SETTING_STOPBITS);
    	System.out.println(STOPBITS + ":" + stopBits);

    	handShaking = sharedPreferences.getString(HANDSHAKING, SerialSettings.DEFAULT_SETTING_HANDSHAKING);
    	System.out.println(HANDSHAKING + ":" + handShaking);
    	
    	enableFileLogging = sharedPreferences.getBoolean(ENABLE_FILE_LOGGING, false);  
    	System.out.println(ENABLE_FILE_LOGGING + ":" + enableFileLogging);
	}
	
	/**
	 * 설정 값들을 Preference 파일에 저장한다.
	 * @param sharedPreferences
	 */
	public void savePreference(SharedPreferences sharedPreferences) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		
		editor.putBoolean(SerialSettings.IS_CUSTOM_BAUDRATE, isCustomBaudrate);
		editor.putInt(SerialSettings.BAUDRATE, baudRate);
		editor.putInt(SerialSettings.DATABITS, dataBits);
		editor.putString(SerialSettings.PARITY, parity);
		editor.putFloat(SerialSettings.STOPBITS, stopBits);
		editor.putString(SerialSettings.HANDSHAKING, handShaking);
		editor.putBoolean(SerialSettings.ENABLE_FILE_LOGGING, enableFileLogging);
		
		editor.commit();
	}

	@Override
	public String toString() {
		return "SerialSettings [isCustomBaudrate=" + isCustomBaudrate + ", baudRate=" + baudRate + ", dataBits="
				+ dataBits + ", parity=" + parity + ", stopBits=" + stopBits + ", handShaking=" + handShaking
				+ ", enableFileLogging=" + enableFileLogging + "]";
	}
}
