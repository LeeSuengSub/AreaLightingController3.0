package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

import android.util.Log;

public class LEDGroupEnablePDU extends RequestPDUBase {

    int enable=0;

    /**
     * @param dest         목적지 주소, 브로드케스트인 경우 NULL 입력
     */
    public LEDGroupEnablePDU(String dest,int enable) {
        super(dest, TRANSFER_TYPE_BROADCAST,COMMAND_ID_GROUP_ENABLE);

        this.enable=enable;
    }

    @Override
    public String encode() {

        Log.d("TEST","EMABLE : "+enable);
        StringBuilder sb=new StringBuilder();
        sb.append(enable);

        return encode(sb.toString());
    }
}
