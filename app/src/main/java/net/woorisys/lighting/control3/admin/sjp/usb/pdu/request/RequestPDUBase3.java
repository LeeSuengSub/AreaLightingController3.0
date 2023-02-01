package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

import android.util.Log;

import net.woorisys.lighting.control3.admin.sjp.usb.pdu.PDU;

import lombok.Getter;

public abstract class RequestPDUBase3 extends PDU {

    /** 채널 변경 전송 형태 **/
    public static final String ID="5004";

    public static final String F="FFFF";

    public static final String SET_R_BLE_1_GROUP="SET_R_BLE_1_GROUP";
    public static final String GET_R_BLE_1_GROUP="GET_R_BLE_1_GROUP";
    public static final String CMD_GROUP_SEND="CMD_GROUP_1_SEND";

    private String id;
    private String f;

    @Getter
    private String DongleCHANNEL;

    public RequestPDUBase3(String id,String f)
    {
        super();

        this.id=id;
        this.f=f;
    }


    protected String encode(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        sb.append(SEPARATOR);
        sb.append(f);


        if (value != null) {
            sb.append(SEPARATOR);
            sb.append(value);
        }

        sb.append(ETX);

        Log.d("LEDControlSetActivity", sb.toString());
        return sb.toString();
    }

    abstract public String encode();

}
