package com.packets.ripPacket;

public class RipInformation {
    private String networkAddress;
    private String subnetMask;
    private int hopCount;

    public String getNetworkAddress() {
        return networkAddress;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public int getHopCount() {
        return hopCount;
    }

    public RipInformation(String networkAddress, String subnet, int hopCount) {
        this.networkAddress = networkAddress;
        this.subnetMask = subnet;
        this.hopCount = hopCount;
    }
}
