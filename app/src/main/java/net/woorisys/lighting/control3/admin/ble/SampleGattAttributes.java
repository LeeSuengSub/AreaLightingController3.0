package net.woorisys.lighting.control3.admin.ble;

import java.util.HashMap;

public class SampleGattAttributes{

    private static HashMap<String, String> attributes = new HashMap<>();

    //    public static String WOORI_PARENT_UUID = "0000fff0-0000-1000-8000-00805f9b34fb"; // to be (WOORISYSTEM)
    public static String WOORI_PARENT_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"; // as is (DAS)
    public static String WOORI_NOTI_UUID = "0000fff1-0000-1000-8000-00805f9b34fb"; // to be (WOORISYSTEM)
//    public static String WOORI_NOTI_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"; // as is (DAS)

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}