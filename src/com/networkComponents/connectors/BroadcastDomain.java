package com.networkComponents.connectors;// this class (if the topology is configured properly) is a container of all ports which are connected to same network segment (belong to the same IP network).
import java.util.ArrayList;
import com.packets.Packet;

public class BroadcastDomain {
    private String name;
    private ArrayList<Port> connectedPorts = new ArrayList<>();

    public String getName() {
        return name;
    }

    public boolean injectPacket(Packet packet, String address){ //returns true or false whether the packet was successfully transmitted or not
        if(address.equals("broadcast")){
            for(int i = 0; i< connectedPorts.size(); i++) {
                if(!connectedPorts.get(i).isShutdown())
                    if(!packet.getSourceAddress().equals(connectedPorts.get(i).getAddress()))
                        connectedPorts.get(i).writeBuffer(packet);
            }
            return true;
        }
        for(int i = 0; i< connectedPorts.size(); i++){
            if(!connectedPorts.get(i).isShutdown())
                if(connectedPorts.get(i).getAddress().equals(address)) {
                    connectedPorts.get(i).writeBuffer(packet);
                    return true;
                }
        }
        return false;
    }
    public void addPort(Port port){
        connectedPorts.add(port);
    }
    public void removePort(Port port){
            connectedPorts.remove(port);
    }
    public BroadcastDomain(String name) {
        this.name = name;
    }
}
