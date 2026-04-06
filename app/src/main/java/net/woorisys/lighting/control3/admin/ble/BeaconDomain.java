package net.woorisys.lighting.control3.admin.ble;

public class BeaconDomain {

    private String macAddress;
    private int serialNumber;
    private String uuid;  // UUID 필드 추가

    public BeaconDomain(String macAddress, int serialNumber, String uuid) {
        this.macAddress = macAddress;
        this.serialNumber = serialNumber;
        this.uuid = uuid;
    }

    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public int getSerialNumber() { return serialNumber; }
    public void setSerialNumber(int serialNumber) { this.serialNumber = serialNumber; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }
}