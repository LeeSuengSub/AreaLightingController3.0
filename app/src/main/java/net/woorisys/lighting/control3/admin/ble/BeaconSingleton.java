package net.woorisys.lighting.control3.admin.ble;

import java.util.ArrayList;

public class BeaconSingleton {

    private ArrayList<BeaconDomain> beaconDomainList;

    public ArrayList<BeaconDomain> getBeaconDomainList() {
        return beaconDomainList;
    }

    public void setBeaconDomainList(ArrayList<BeaconDomain> beaconDomainList) {
        this.beaconDomainList = beaconDomainList;
    }

    public void resetBeaconDomainList() {
        beaconDomainList.clear();
    }

    private static final BeaconSingleton instance = new BeaconSingleton();

    public static BeaconSingleton getInstance() {
        return instance;
    }

    private BeaconSingleton() {
        beaconDomainList = new ArrayList<>();
    }
}