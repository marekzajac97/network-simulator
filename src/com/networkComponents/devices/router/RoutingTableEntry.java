package com.networkComponents.devices.router;

import com.networkComponents.devices.Observer;

public class RoutingTableEntry extends Thread{
    private String networkAddress;
    private String subnetMask;
    private String nextHopAddress;
    private int hopCount;
    private boolean directlyConnected; // if true && hopCount == 0 => directly connected network (C)
    private int portIndex;             // if false && hopCount == 0 => static route entry (S)
    private int flushTime = 30;      // if false && hopCount >0 => entry learned from rip (R)
    private Observer o;                // so static routes are always preferred

    public String getNetworkAddress() {
        return networkAddress;
    }
    public String getSubnetMask() {
        return subnetMask;
    }
    public int getPortIndex() {
        return portIndex;
    }
    public String getNextHopAddress() {
        return nextHopAddress;
    }
    public boolean isDirectlyConnected() {
        return directlyConnected;
    }
    public int getHopCount() {
        return hopCount;
    }

    public void resetFlushTime() {
        flushTime = 30;
    }

    public RoutingTableEntry(String networkAddress, String subnet, String nextHopAddress, int hopCount, boolean directlyConnected, int portIndex, Observer o) {
        this.networkAddress = networkAddress;
        this.nextHopAddress = nextHopAddress;
        this.portIndex = portIndex;
        this.subnetMask = subnet;
        this.o = o;
        this.hopCount = hopCount;
        this.directlyConnected = directlyConnected;
    }
    public RoutingTableEntry(String networkAddress, String subnet, boolean directlyConnected, int portIndex, Observer o) {
        this.networkAddress = networkAddress;
        this.directlyConnected = directlyConnected;
        this.portIndex = portIndex;
        this.subnetMask = subnet;
        this.o = o;
        hopCount = 0;
        nextHopAddress = null;
    }
    public void run(){
        if(hopCount == 0) // if directly connected or static don't decrease flushTime
            return;
        boolean state = true;
        while(state)
        {
            try { sleep(1000); } catch (InterruptedException e) {}
            flushTime -= 1; // decrease flush time by 1 every second
            if(flushTime == 0) { // if true inform router in oder to delete this entry
                state = false;
                o.inform(this);
            }
        }
    }
}
