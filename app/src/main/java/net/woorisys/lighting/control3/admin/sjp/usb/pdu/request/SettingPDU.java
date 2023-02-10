package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

public class SettingPDU extends RequestPDUBase3 {


    String serial ="";
    String gateway="";

    public SettingPDU(String Gateway,String serial) {
        super(Gateway,"FFFF");
        System.out.println("Gateway => : "+Gateway);

        this.serial=serial;
    }

    @Override
    public String encode() {

        StringBuilder sb=new StringBuilder();
//        sb.append(gateway);
//        sb.append(SEPARATOR);
        sb.append(serial);
        sb.append(SEPARATOR);
        sb.append(CMD_GROUP_SEND);
        return encode(sb.toString());
    }
}
