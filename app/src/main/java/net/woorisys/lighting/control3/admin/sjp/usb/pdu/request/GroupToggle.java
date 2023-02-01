package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

public class GroupToggle extends RequestPDUBase4 {

    public GroupToggle() {
        super(FV, SV, TV,TV_Toggle);
    }

    @Override
    public String encode() {
        return encode(null);
    }
}
