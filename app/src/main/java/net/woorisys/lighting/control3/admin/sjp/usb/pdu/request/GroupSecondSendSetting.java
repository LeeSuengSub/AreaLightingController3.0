package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

public class GroupSecondSendSetting extends RequestPDUBase3 {

    String gateway;
    String serial ="";
    String[] members;
    String[] type;

    public GroupSecondSendSetting (String Gateway,String serial,String[] members,String[] type) {
        super(Gateway,F);
        this.serial=serial;
        this.members=members;
        this.type=type;
    }

    @Override
    public String encode() {
        StringBuilder sb=new StringBuilder();
        //sb.append(SEPARATOR);
        sb.append(serial);
        sb.append(SEPARATOR);
        sb.append(SET_R_BLE_2_GROUP);
//        sb.append(SEPARATOR);
//        sb.append(members.length);
        for (int i=0;i<members.length;i++) {
            sb.append(SEPARATOR).append(members[i]);
            sb.append(SEPARATOR).append(type[i]);
        }
//        sb.append(mambers);
        return encode(sb.toString());
    }
}
