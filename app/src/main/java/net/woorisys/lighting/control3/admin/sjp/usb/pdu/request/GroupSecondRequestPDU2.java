package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

/**
 * 그룹 정보 요청 PDU
 *
 * @author hslim
 *
 */


public class GroupSecondRequestPDU2 extends RequestPDUBase3 {

    String gateway = "";
    String serial ="";

    public GroupSecondRequestPDU2 (String Gateway,String serial) {
        super(Gateway,F);
//		this.gateway = Gateway; //20230207
        this.serial=serial;
    }

    @Override
    public String encode() {

        StringBuilder sb=new StringBuilder();
//		sb.append(gateway);
//		sb.append(SEPARATOR);
//		sb.append(F);
//		sb.append(SEPARATOR); //20230207
        sb.append(serial);
        sb.append(SEPARATOR);
        sb.append(GET_R_BLE_2_GROUP);

        return encode(sb.toString());
    }
}
