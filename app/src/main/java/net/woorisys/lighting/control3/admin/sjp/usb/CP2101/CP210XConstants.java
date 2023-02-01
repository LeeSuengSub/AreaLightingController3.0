package net.woorisys.lighting.control3.admin.sjp.usb.CP2101;

public class CP210XConstants {
	
	/** CP2102 Vendor ID */
	public static final int CP2102_VENDOR_ID = 4292;
	
	/** CP2102 Product ID */
	public static final int CP2102_PRODUCT_ID = 60000;
	
	
	/** CP210X USB SET Request Type */
    public static final int SET_REQUEST_TYPE = 0x41;
    
    /** CP210X USB GET Request Type */
    public static final int GET_REQUEST_TYPE = 0xC1;
    
    /**
     * Baudrate GET 
     */
    public static final int GET_BAUDRATE = 0x1D;
    
    /**
     * SET_MHS: Set Modem Handshaking states
     */
    public static final int SET_MHS = 0x07;
    
    /**
     * GET_MDMSTS: Get Modem Control Line Status 
     */
    public static final int GET_MDMSTS = 0x08;

    /**
     * GET PROPERTIES
     */
    public static final int GET_PROPS = 0x0F;
   
    /**
     * SET_FLOW
     */
    public static final int SET_FLOW = 0x13;
    
    /**
     * GET_FLOW
     */
    public static final int GET_FLOW = 0x14;
    
    /**
     * Baudrate SET
     */
    public static final int SET_BAUDRATE = 0x1E;
    
    /**
     * Line Control(Stop bits, Parity) SET
     */
    public static final int SET_LINE_CTL = 0x03;
    
    /**
     * Line Control(Stop bits, Parity) GET
     */
    public static final int GET_LINE_CTL = 0x04;
    
    /**
     * Interface Enable
     */
    public static final int SET_IFC_ENABLE = 0x00;
    
    /**
     * Reset
     */
    public static final int RESET = 0x11;
    
    /**
     * 3종 센서 통신 Baudrate
     */
    public static final int DEFAULT_BAUDRATE = 115200;
}
