package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

public class ChannelChangePDU extends RequestPDUBase2 {


    String channel ="";

    public ChannelChangePDU(String channel) {
        super(F,Z,Z,DONGLE_CHANNEL);

        this.channel=channel;
    }

    @Override
    public String encode() {

        StringBuilder sb=new StringBuilder();
        sb.append(channel);

        return encode(sb.toString());
    }
}
