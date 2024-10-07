package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

public class SettingSecondPDU extends RequestPDUBase3 {


    String serial ="";
    String gateway="";

    public SettingSecondPDU (String Gateway,String serial) {
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
        sb.append(CMD_GROUP_2_SEND);
        return encode(sb.toString());
    }
}
