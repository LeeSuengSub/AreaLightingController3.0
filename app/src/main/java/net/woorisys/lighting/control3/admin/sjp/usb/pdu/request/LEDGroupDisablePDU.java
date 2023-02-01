package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

public class LEDGroupDisablePDU extends RequestPDUBase {

    /**
     * @param dest         목적지 주소, 브로드케스트인 경우 NULL 입력
     */
    public LEDGroupDisablePDU(String dest) {
        super(dest, TRANSFER_TYPE_BROADCAST, RequestPDUBase.COMMAND_ID_GROUP_DISABLE);
    }

    @Override
    public String encode() {
        return encode(null);
    }
}
