package com.networkComponents.connectors;

import java.util.LinkedList;
import java.util.Queue;
import com.networkComponents.devices.Observer;
import com.packets.Packet;

public class Port {
    private int index;
    private boolean shutdown = true;
    private Observer o;
    private BroadcastDomain broadcastDomain;
    private Queue<Packet> buffer = new LinkedList<>();
    private String address;
    private String subnetMask;
    public void subscribe(Observer o){
        this.o = o;
    }
    public void unsubscribe(Observer o){
        this.o = null;
    }
    public void writeBuffer(Packet packet){
        buffer.add(packet);
    }
    public Packet readBuffer(){
        return buffer.remove();
    }
    public void setBroadcastDomain(BroadcastDomain broadcastDomain){
        this.broadcastDomain = broadcastDomain;
    }
    public void removeFromBroadcastDomain(){
        if(broadcastDomain != null) {
            broadcastDomain.removePort(this);
            this.broadcastDomain = null;
        }
    }

    public String getAddress() {
        return address;
    }
    public String getSubnetMask() {
        return subnetMask;
    }
    public boolean isShutdown() {
        return shutdown;
    }
    public BroadcastDomain getBroadcastDomain() {
        return broadcastDomain;
    }
    public boolean isBufferEmpty(){
        return buffer.isEmpty();
    }
    public int getIndex() {
        return index;
    }

    public void setAddress(String address, String subnetMask){
            if(!shutdown) { //if is up
                if(o != null)
                    o.inform(this, true); //delete previous entry
                this.address = address;
                this.subnetMask = subnetMask;
                if(o != null)
                    o.inform(this, false); //set new entry
            }
            else{
                this.address = address;
                this.subnetMask = subnetMask;
            }
    }
    public void shutdown(){
        shutdown = true;
        buffer.clear();
        if(o != null)
            o.inform(this, true);
    }
    public void noShutdown(){
        shutdown = false;
        if(o != null)
            o.inform(this, false);
    }
    public boolean validate(){
        if(broadcastDomain == null || address == null || subnetMask == null)
            return false;
        else return true;
    }

    public Port(int index) {
        this.index = index;
    }
}
