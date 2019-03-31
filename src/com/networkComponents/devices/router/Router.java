package com.networkComponents.devices.router;

import com.gui.DiodeObserver;
import com.networkComponents.devices.Device;
import com.networkComponents.devices.Observer;
import com.networkComponents.connectors.Port;
import com.packets.*;
import com.packets.ripPacket.*;
import com.addressOperations.AddressOperations;
import java.util.logging.Logger;

import java.util.ArrayList;

public class Router extends Device implements Observer, Runnable{
    private ArrayList<Port> eth = new ArrayList<>();
    private ArrayList<RoutingTableEntry> routingTable = new ArrayList<>();
    private boolean ripEnabled = false;
    private DiodeObserver observer;
    private static Logger log = Logger.getLogger("Logger");

    public void subscribe(DiodeObserver observer){
        this.observer = observer;
    }
    public boolean isRipEnabled() {
        return ripEnabled;
    }
    public int getEthSize() {
        return eth.size();
    }
    public Port getEth(int i) {
        return eth.get(i);
    }
    public int getRoutingTableSize() {
        return routingTable.size();
    }
    public RoutingTableEntry getRoutingTableEntry(int i) {
        return routingTable.get(i);
    }
    private void servePacket(Packet packet, int portIndex){
        observer.enableDiode();
        int isItMine = isForMe(packet.getDestinationAddress());
        if(isItMine != -1) {
            if(packet instanceof IcmpPacket) {
                if ( ((IcmpPacket) packet).getIcmpType() == 8) {
                    Packet icmpEchoReplayPacket = PacketFactory.getPacket("ICMP_echo_reply", packet.getSourceAddress(), eth.get(isItMine).getAddress(), 32);
                    forward(icmpEchoReplayPacket);
                }
            }
            else if(packet instanceof RipPacket) {
                if(ripEnabled)
                    updateRoutingTable( ((RipPacket) packet), portIndex);
            }
        }
        else
            forward(packet);
    }
    public void inform(Port port, boolean flushEntry){
        if(flushEntry) {
            removeDirectlyConnected(port);
            if(ripEnabled)
                propagateNetworkUnavailable(AddressOperations.getNetworkAddress(port.getAddress(), port.getSubnetMask()), port.getSubnetMask(), -1);
        }
        else
            addDirectlyConnected(port);
    }
    public void inform(RoutingTableEntry entry){
        try { Thread.sleep(1); } catch (InterruptedException e) {e.getStackTrace();}
        RoutingTableEntry entryToRemove = entry;
        routingTable.remove(entry);
        propagateNetworkUnavailable(entryToRemove.getNetworkAddress(), entryToRemove.getSubnetMask(), -1);
    }
    private void forward(Packet packet){
        packet.decreaseTTL();
        if(packet.getTTL() == 0){
            Packet icmpTimeExceededPacket = PacketFactory.getPacket("ICMP_time_exceeded", packet.getSourceAddress(), "unknown", 32);
            forward(icmpTimeExceededPacket);
        }
        int[] entriesFound = {-1,-1};    // first one is static route or directly connected and the second is rip route
        for(int k = 0; k < 32; k++ ) {   // longest prefix match algorithm
            for(int i = 0; i< routingTable.size(); i++)
                if (AddressOperations.getNetworkAddress(packet.getDestinationAddress(), AddressOperations.getSubnetMask(k)).equals(routingTable.get(i).getNetworkAddress()) && routingTable.get(i).getSubnetMask().equals(AddressOperations.getSubnetMask(k))){
                    if(routingTable.get(i).getHopCount() == 0){
                        entriesFound[0] = i;
                    } else
                        entriesFound[1] = i;
                }
        }
        if (entriesFound[0] == -1 && entriesFound[1] == -1) {
            Packet icmpDestinationUnreachablePacket = PacketFactory.getPacket("ICMP_destination_network_unreachable", packet.getSourceAddress(), "unknown", 32);
            forward(icmpDestinationUnreachablePacket);
            return;
        }
        if(entriesFound[0] != -1) {
            int portIndex = routingTable.get(entriesFound[0]).getPortIndex();                 // extract port index from routing table entry
            if (packet.getSourceAddress().equals("unknown"))                              // if packet src address was 'unknown' set a proper src address according to interface address
                packet.setSourceAddress(eth.get(portIndex).getAddress());
            if (routingTable.get(entriesFound[0]).isDirectlyConnected()) {
                if (!eth.get(portIndex).getBroadcastDomain().injectPacket(packet, packet.getDestinationAddress())) { //if the network is directly connected - inject packet into broadcast domain (to packet destination)
                    Packet icmpDestinationUnreachablePacket = PacketFactory.getPacket("ICMP_destination_host_unreachable", packet.getSourceAddress(), "unknown", 32);
                    forward(icmpDestinationUnreachablePacket);
                }
            } else {
                if (!eth.get(portIndex).getBroadcastDomain().injectPacket(packet, routingTable.get(entriesFound[0]).getNextHopAddress())) { //if not inject packet into broadcast domain (to nexthop)
                    Packet icmpDestinationUnreachablePacket = PacketFactory.getPacket("ICMP_destination_host_unreachable", packet.getSourceAddress(), "unknown", 32);
                    forward(icmpDestinationUnreachablePacket);
                }
            }
        } else{
            int portIndex = routingTable.get(entriesFound[1]).getPortIndex();
            if (packet.getSourceAddress().equals("unknown"))
                packet.setSourceAddress(eth.get(portIndex).getAddress());
            if (!eth.get(portIndex).getBroadcastDomain().injectPacket(packet, routingTable.get(entriesFound[1]).getNextHopAddress())) { //if not inject packet into broadcast domain (to nexthop)
                Packet icmpDestinationUnreachablePacket = PacketFactory.getPacket("ICMP_destination_host_unreachable", packet.getSourceAddress(), "unknown", 32);
                forward(icmpDestinationUnreachablePacket);
            }
        }
    }
    private int findRipEntry(String networkAddress, String subnet){
        for(int i = 0; i< routingTable.size(); i++)
            if(routingTable.get(i).getNetworkAddress().equals(networkAddress) && routingTable.get(i).getSubnetMask().equals(subnet) && routingTable.get(i).getHopCount() > 0)
                return i;
        return -1;

    }
    private int findDirectlyConnected(String networkAddress, String subnet){
        for(int i = 0; i< routingTable.size(); i++)
            if(routingTable.get(i).getNetworkAddress().equals(networkAddress) && routingTable.get(i).getSubnetMask().equals(subnet) && routingTable.get(i).isDirectlyConnected())
                return i;
        return -1;

    }
    private int findStaticEntry(String networkAddress, String subnet){
        for(int i = 0; i< routingTable.size(); i++)
            if(routingTable.get(i).getNetworkAddress().equals(networkAddress) && routingTable.get(i).getSubnetMask().equals(subnet) && routingTable.get(i).getHopCount() == 0 && !routingTable.get(i).isDirectlyConnected())
                return i;
        return -1;

    }
    private void addRoutingTableEntry(String networkAddress, String subnet, String nextHopAddress, int hopCount, int portIndex){
        log.info(hostname + " adding RIP entry: address " + networkAddress + " subnet " + subnet + " next hop " + nextHopAddress + " hop count " + hopCount);
        RoutingTableEntry entry = new RoutingTableEntry(networkAddress, subnet, nextHopAddress, hopCount, false, portIndex, this);
        routingTable.add(entry);
        entry.start();
    }
    public int addStaticRoute(String networkAddress, String subnet, String nextHopAddress, int portIndex){
        if(eth.size() -1 < portIndex)
            return -1;
        else {
            if (findStaticEntry(networkAddress, subnet) == -1) {
                RoutingTableEntry entry = new RoutingTableEntry(networkAddress, subnet, nextHopAddress, 0, false, portIndex, this);
                routingTable.add(entry);
                return 1;
            } else
                return -2;
        }
    }
    public boolean removeStaticRoute(String networkAddress, String subnet){
        int entry = findStaticEntry(networkAddress, subnet);
            if(entry == -1)
                    return false;
                else{
                    if(routingTable.get(entry).isDirectlyConnected())
                        return false;
                    else {
                        routingTable.remove(entry);
                        return true;
                    }
                }
    }
    private void addDirectlyConnected(Port port){
        String networkAddress;
        networkAddress = AddressOperations.getNetworkAddress(port.getAddress(), port.getSubnetMask());
        RoutingTableEntry entry = new RoutingTableEntry(networkAddress, port.getSubnetMask(), true, eth.indexOf(port), this);
        routingTable.add(entry);
    }
    private void removeDirectlyConnected(Port port){
        for(int i = 0; i< routingTable.size(); i++){
            if(routingTable.get(i).getPortIndex() == eth.indexOf(port) && routingTable.get(i).isDirectlyConnected())
                routingTable.remove(i);
        }
    }
    public void addNewPort(Port port){
        eth.add(port);
    }
    public void removeAllPorts(){
        for (Port port : eth)
            port.removeFromBroadcastDomain();
        eth.clear();
    }
    private int isForMe(String address){
        int portIndex = -1;
        for(int i=0;i<eth.size();i++) {
            if (!eth.get(i).isShutdown()) {
                if (eth.get(i).getAddress().equals(address))
                    portIndex = i;
                else if (AddressOperations.getBroadcastAddress(eth.get(i).getAddress(), eth.get(i).getSubnetMask()).equals(address))
                    portIndex = i;
            }
        }
        return portIndex;
    }
    public void enableRip(){
        ripEnabled = true;
    }
    public void disableRip(){
        ripEnabled = false;
        for(int i = 0; i< routingTable.size(); i++)
            if(routingTable.get(i).getHopCount() > 0) {
                routingTable.remove(i);
                i--;
            }
    }
    private void propagateRipInfo(){
        ArrayList<RipInformation> ripInfo = new ArrayList<>(); //build a new routing information based on internal routing table
        for(int i = 0; i< routingTable.size(); i++) {
            if(routingTable.get(i).isDirectlyConnected() || routingTable.get(i).getHopCount() > 0)
                ripInfo.add(new RipInformation(routingTable.get(i).getNetworkAddress(), routingTable.get(i).getSubnetMask(), routingTable.get(i).getHopCount() + 1));
        }
        for(int i=0;i<eth.size();i++) { //generate RIP packets and send it on broadcast addresses of all ports
            if(!eth.get(i).isShutdown()) {
                Packet ripPacket = PacketFactory.getPacket("RIP", AddressOperations.getBroadcastAddress(eth.get(i).getAddress(), eth.get(i).getSubnetMask()), eth.get(i).getAddress(), 32, ripInfo);
                eth.get(i).getBroadcastDomain().injectPacket(ripPacket, "broadcast");
            }
        }
    }
    private void propagateNetworkUnavailable(String networkAddress, String subnet, int portIndex){
        ArrayList<RipInformation> ripInfo = new ArrayList<>();
        ripInfo.add(new RipInformation(networkAddress, subnet, 16));
        for(int i=0;i<eth.size();i++) { //generate RIP packets and send it on broadcast (all ports except the one you received rip packet from)
            if (!eth.get(i).isShutdown() && i != portIndex) {
                Packet ripPacket = PacketFactory.getPacket("RIP", AddressOperations.getBroadcastAddress(eth.get(i).getAddress(), eth.get(i).getSubnetMask()), eth.get(i).getAddress(), 32, ripInfo);
                eth.get(i).getBroadcastDomain().injectPacket(ripPacket, "broadcast");
            }
        }
    }
    private void updateRoutingTable(RipPacket packet, int portIndex){
        for(int i=0;i<packet.getRipInfoSize();i++){
            int entryIndex = findRipEntry(packet.getRipInfo(i).getNetworkAddress(), packet.getRipInfo(i).getSubnetMask());
            if(entryIndex != -1){
                if (routingTable.get(entryIndex).getHopCount() > packet.getRipInfo(i).getHopCount()) {
                    routingTable.remove(entryIndex);
                    addRoutingTableEntry(packet.getRipInfo(i).getNetworkAddress(), packet.getRipInfo(i).getSubnetMask(), packet.getSourceAddress(), packet.getRipInfo(i).getHopCount(),  portIndex);
                }
                else {
                        if(routingTable.get(entryIndex).getNextHopAddress().equals(packet.getSourceAddress())) {
                            routingTable.get(entryIndex).resetFlushTime();
                            if (packet.getRipInfo(i).getHopCount() == 16) {
                                propagateNetworkUnavailable(routingTable.get(entryIndex).getNetworkAddress(), routingTable.get(entryIndex).getSubnetMask(), routingTable.get(entryIndex).getPortIndex());
                                routingTable.remove(entryIndex);
                            }
                        }
                    }
            }
            else {
                if(findDirectlyConnected(packet.getRipInfo(i).getNetworkAddress(), packet.getRipInfo(i).getSubnetMask()) == -1 && packet.getRipInfo(i).getHopCount() != 16)
                    addRoutingTableEntry(packet.getRipInfo(i).getNetworkAddress(), packet.getRipInfo(i).getSubnetMask(), packet.getSourceAddress(), packet.getRipInfo(i).getHopCount(), portIndex);
            }
        }
    }
    public void run(){
        running = true;
        int milliseconds = 0;
        while(running)
        {
            try {
                Thread.sleep(1); //buffer reading speed
            } catch (InterruptedException e) {e.getStackTrace();}
            milliseconds += 1;
            for(int i=0; i<eth.size(); i++) {
                if (!eth.get(i).isBufferEmpty() && !eth.get(i).isShutdown())
                    servePacket(eth.get(i).readBuffer(), i);
            }
            if(milliseconds == 5000) { //how frequent rip propagation messages should be sent
                if(ripEnabled)
                    propagateRipInfo();
                milliseconds = 0;
            }
        }
    }

    public Router(String hostname) {
        super(hostname);
    }
}