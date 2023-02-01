package net.woorisys.lighting.control3.admin.sjp.usb.util;

public class FirstListViewItem implements Comparable<FirstListViewItem> {

	/** Mac 주소 */
	private String mac = "";

	public FirstListViewItem(String mac) {
		this.mac = mac;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String device) {
		this.mac = device;
	}

	@Override
	public int compareTo(FirstListViewItem another) {
		return getMac().compareToIgnoreCase(another.getMac());
	}
}
