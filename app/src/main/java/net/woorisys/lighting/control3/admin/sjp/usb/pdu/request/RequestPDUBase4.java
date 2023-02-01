package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

import android.util.Log;

import net.woorisys.lighting.control3.admin.sjp.usb.pdu.PDU;

public abstract class RequestPDUBase4 extends PDU {

    public static final String FV="FFFF";
    public static final String SV="FFFF";
    public static final String TV="B001";
    public static final String TV_Toggle="CMD_GROUP_1_SEND";
    public static final String TV_RouterRejoin="REJOIN";

    private String F;
    private String S;
    private String T;
    private String Ft;

    public RequestPDUBase4(String F,String S,String T,String Ft)
    {
        super();

        this.F=F;
        this.S=S;
        this.T=T;
        this.Ft=Ft;
    }


    protected String encode(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(F);
        sb.append(SEPARATOR);
        sb.append(S);
        sb.append(SEPARATOR);
        sb.append(T);
        sb.append(SEPARATOR);
        sb.append(Ft);


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
