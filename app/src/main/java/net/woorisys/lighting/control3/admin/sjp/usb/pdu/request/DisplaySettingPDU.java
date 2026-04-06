package net.woorisys.lighting.control3.admin.sjp.usb.pdu.request;

public class DisplaySettingPDU extends RequestPDUBase{

    private String gateway;
    private String firstDisplay;
    private String secondDisplay;
    private String thirdDisplay;
    private String fourthDisplay;
    private String fifthDisplay;
    private String sixthDisplay;
    private String seventhDisplay;
    private String eighthDisplay;
    private String ninthDisplay;
    private String tenthDisplay;
    private int length;

    public DisplaySettingPDU(String gateway, int length, String firstDisplay, String secondDisplay, String thirdDisplay, String fourthDisplay, String fifthDisplay, String sixthDisplay, String seventhDisplay, String eighthDisplay, String ninthDisplay, String tenthDisplay) {
        super(gateway, ZERO, gateway, SET_G_GROUP);
        this.gateway = gateway;
        this.length = length;
        this.firstDisplay = firstDisplay;
        this.secondDisplay = secondDisplay;
        this.thirdDisplay = thirdDisplay;
        this.fourthDisplay = fourthDisplay;
        this.fifthDisplay = fifthDisplay;
        this.sixthDisplay = sixthDisplay;
        this.seventhDisplay = seventhDisplay;
        this.eighthDisplay = eighthDisplay;
        this.ninthDisplay = ninthDisplay;
        this.tenthDisplay = tenthDisplay;
    }

    public String encode() {
        StringBuilder sb = new StringBuilder();

//        sb.append(SET_G_GROUP).append(SEPARATOR);
        sb.append(length).append(SEPARATOR);
        if(!firstDisplay.isEmpty()) sb.append("1").append(SEPARATOR);
        if(!firstDisplay.isEmpty()) sb.append(firstDisplay);
        if(!secondDisplay.isEmpty()) sb.append(SEPARATOR).append("2").append(SEPARATOR);
        if(!secondDisplay.isEmpty()) sb.append(secondDisplay);
        if(!thirdDisplay.isEmpty()) sb.append(SEPARATOR).append("3").append(SEPARATOR);
        if(!thirdDisplay.isEmpty()) sb.append(thirdDisplay);
        if(!fourthDisplay.isEmpty()) sb.append(SEPARATOR).append("4").append(SEPARATOR);
        if(!fourthDisplay.isEmpty()) sb.append(fourthDisplay);
        if(!fifthDisplay.isEmpty()) sb.append(SEPARATOR).append("5").append(SEPARATOR);
        if(!fifthDisplay.isEmpty()) sb.append(fifthDisplay);
        if(!sixthDisplay.isEmpty()) sb.append(SEPARATOR).append("6").append(SEPARATOR);
        if(!sixthDisplay.isEmpty()) sb.append(sixthDisplay);
        if(!seventhDisplay.isEmpty()) sb.append(SEPARATOR).append("7").append(SEPARATOR);
        if(!seventhDisplay.isEmpty()) sb.append(seventhDisplay);
        if(!eighthDisplay.isEmpty()) sb.append(SEPARATOR).append("8").append(SEPARATOR);
        if(!eighthDisplay.isEmpty()) sb.append(eighthDisplay);
        if(!ninthDisplay.isEmpty()) sb.append(SEPARATOR).append("9").append(SEPARATOR);
        if(!ninthDisplay.isEmpty()) sb.append(ninthDisplay);
        if(!tenthDisplay.isEmpty()) sb.append(SEPARATOR).append("10").append(SEPARATOR);
        if(!tenthDisplay.isEmpty()) sb.append(tenthDisplay);
        return encode(sb.toString());
    }


}
