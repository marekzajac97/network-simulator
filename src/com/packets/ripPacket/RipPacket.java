package com.packets.ripPacket;

import com.packets.Packet;

import java.util.ArrayList;

public class RipPacket extends Packet {
    private ArrayList<RipInformation> ripInfo;
    public RipInformation getRipInfo(int i){
        return ripInfo.get(i);
    }
    public int getRipInfoSize(){
        return ripInfo.size();
    }

    public RipPacket(String destinationAddress, String sourceAddress, int ttl, ArrayList<RipInformation> ripInfo) {
        super(destinationAddress, sourceAddress, ttl);
        this.ripInfo = ripInfo;
    }
}
