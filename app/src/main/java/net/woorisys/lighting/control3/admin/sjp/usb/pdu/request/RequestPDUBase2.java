package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

import android.util.Log;

import net.woorisys.lighting.control3.admin.sjp.usb.pdu.PDU;

import lombok.Getter;

public abstract class RequestPDUBase2 extends PDU {

    /** 채널 변경 전송 형태 **/
    public static final String F="E000";
    public static final String Z="0000";
    public static final String DONGLE_CHANNEL="DONGLE_CHANNEL";

    private String channel;
    private String First;
    private String ZeroF;
    private String ZeroS;

    @Getter
    private String DongleCHANNEL;

    public RequestPDUBase2(String First,String ZeroF,String ZeroS,String DongleCHANNEL)
    {
        super();
        this.First=First;
        this.ZeroF=ZeroF;
        this.ZeroS=ZeroS;
        this.DongleCHANNEL=DongleCHANNEL;
    }


    protected String encode(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(First);
        sb.append(SEPARATOR);
        sb.append(ZeroF);
        sb.append(SEPARATOR);
        sb.append(ZeroS);
        sb.append(SEPARATOR);
        sb.append(DongleCHANNEL);

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
