package com.packets;

public class Packet {
    private String destinationAddress;
    private String sourceAddress;
    private int ttl;

    public String getDestinationAddress() {
        return destinationAddress;
    }
    public String getSourceAddress() {
        return sourceAddress;
    }
    public int getTTL() {
        return ttl;
    }
    public void decreaseTTL(){
        ttl -= 1;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public Packet(String destinationAddress, String sourceAddress, int ttl) {
        this.destinationAddress = destinationAddress;
        this.sourceAddress = sourceAddress;
        this.ttl = ttl;
    }
}
