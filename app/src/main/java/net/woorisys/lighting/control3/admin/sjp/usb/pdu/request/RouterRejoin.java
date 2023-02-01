package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

public class RouterRejoin extends RequestPDUBase4 {


    public RouterRejoin(String deviceId) {
        super(FV, SV, deviceId, TV_RouterRejoin);
    }

    @Override
    public String encode() {
        return encode("2");
    }
}
