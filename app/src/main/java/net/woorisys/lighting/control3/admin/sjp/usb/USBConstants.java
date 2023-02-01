package net.woorisys.lighting.control3.admin.sjp.usb;

public class USBConstants {
	
	/** CP210X USB SET Request Type */
    static final int SET_REQUEST_TYPE = 0X41;
    
    /** CP210X USB GET Request Type */
    static final int GET_REQUEST_TYPE = 0XC1;
    
    /**
     * 3종 센서 통신 Baudrate
     */
    static final int BAUDRATE = 38400;
    
    /**
     * Baudrate GET 
     */
    static final int GET_BAUDRATE = 0X1D;
    
    /**
     * Baudrate SET
     */
    static final int SET_BAUDRATE = 0X1E;
    
    /**
     * Interface Enable
     */
    static final int SET_IFC_ENABLE = 0X00;
    
    /**
     * Reset
     */
    static final int RESET = 0X11;
    
	/**
	 * 핸들러에게 장비가 연결 해지 되었음을 알리기 위한 상수 값
	 */
    public static final int WHAT_DEVICE_DISCONNECTED = 1;
	
	/**
	 * 핸들러에게 장비가 연결 되지 않았음을 알리기 위한 상수 값
	 */
    public static final int WHAT_DEVICE_NOT_CONNECTED = 2;
	
	/**
	 * 핸들러에게 장비로 부터 응답 시간이 지연 되었음을 알리기 위한 상수 값
	 */
    public static final int WHAT_DEVICE_TIME_OUT = 3;
	
	/**
	 * 센싱 값을 화면에 표시하고자 할 때 사용하는 메시지 코드
	 */
	public static final int WHAT_SENSING_VALUES = 4;
	
	/**
	 * 센서노드 상태 모니터링 정보를 화면에 표시하고자 할 때 사용하는 메시지 코드
	 */
	public static final int WHAT_STATUS = 5;
}
