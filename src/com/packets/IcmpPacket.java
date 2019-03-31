package com.packets;

public class IcmpPacket extends Packet{
    private int icmpType; //google for ICMP Message Types
    private int icmpCode;

    public int getIcmpType() {
        return icmpType;
    }

    public int getIcmpCode() {
        return icmpCode;
    }

    public IcmpPacket(String destinationAddress, String sourceAddress, int ttl, int icmpType, int icmpCode) {
        super(destinationAddress, sourceAddress, ttl);
        this.icmpType = icmpType;
        this.icmpCode = icmpCode;
    }
}
