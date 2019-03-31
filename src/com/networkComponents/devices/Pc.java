package com.networkComponents.devices;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import com.networkComponents.connectors.Port;
import com.packets.*;
import com.addressOperations.AddressOperations;


import javax.swing.*;

public class Pc extends Device implements Runnable{
    private Port eth0 = new Port(0);
    private boolean requestedFlag = false;
    private Queue<Packet> applicationBuffer = new LinkedList<>();
    private String defaultGateway;

    public Port getEth0() {
        return eth0;
    }

    private void servePacket(Packet packet){
        boolean isForMe = isForMe(packet.getDestinationAddress());
        if(isForMe) {
            if(packet instanceof IcmpPacket) {
                if ( ((IcmpPacket) packet).getIcmpType() == 8) {
                    Packet icmpEchoReplayPacket = PacketFactory.getPacket("ICMP_echo_reply", packet.getSourceAddress(), eth0.getAddress(), 32);
                    send(icmpEchoReplayPacket);
                } else {
                    if (requestedFlag) {
                        applicationBuffer.add(packet);
                        requestedFlag = false;
                    }
                }
            }
        }
    }
    public void ping(String address) throws IPAddressIncorrectException {
        if(!AddressOperations.validateIP(address))
            throw new IPAddressIncorrectException();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int packetsSend = 4;
                int packetsReceived = 0;
                long[] times = new long[4];
                long timesSum = 0;
                System.out.println("Pinging " + address + " with X bytes of data:" + '\n');
                for(int i=0; i < packetsSend; i++) {
                    try {
                        Thread.sleep(1000); //timeout
                    }
                    catch (InterruptedException e) {}
                    long output = pingCore(address);
                    if (output > 0) {
                        packetsReceived += 1;
                        times[i] = output;
                        timesSum += output;
                    }
                }
                float packetLoss = ((packetsSend-packetsReceived)/packetsSend)*100;
                Arrays.sort(times);
                System.out.print('\n');
                System.out.println("Ping statistics for " + address + ":");
                System.out.println("     Packets: Send = " + packetsSend + ", Received = " + packetsReceived + ", Lost = " + (packetsSend-packetsReceived) + " <" + packetLoss + "% loss>" );
                System.out.println("Approximate round trip times in milli-seconds:");
                System.out.println("     Minimum = " + times[0] + "ms, Maximum = " + times[3] + "ms, Average = " + (timesSum/4) + "ms" + "\n\n");
            }
        });
        thread.start();
    }
    private long pingCore(String address){
        Packet icmpEchoRequestPacket = PacketFactory.getPacket("ICMP_echo_request", address, eth0.getAddress(), 32);
        requestedFlag = true;
        send(icmpEchoRequestPacket);
        long start = System.currentTimeMillis();
        for(int i=0; i<1000; i++) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {}
            if(!requestedFlag)
                break;
        }
        if (requestedFlag) {
            System.out.println("Request timed out");
            requestedFlag = false;
            return -1;
        } else {
            long elapsedTimeMillis = System.currentTimeMillis()-start;
            if ( ((IcmpPacket) applicationBuffer.peek()).getIcmpType() == 0){
                System.out.println("Reply from: " + applicationBuffer.remove().getSourceAddress() + " time: " + elapsedTimeMillis + " ms");
                return elapsedTimeMillis;
            } else if ( ((IcmpPacket) applicationBuffer.peek()).getIcmpType() == 3) {
                if(((IcmpPacket) applicationBuffer.peek()).getIcmpCode() == 0)
                    System.out.println("Reply from: " + applicationBuffer.remove().getSourceAddress() + ": Destination network unreachable");
                else if(((IcmpPacket) applicationBuffer.peek()).getIcmpCode() == 1)
                    System.out.println("Reply from: " + applicationBuffer.remove().getSourceAddress() + ": Destination host unreachable");
                return -2;
            } else if ( ((IcmpPacket) applicationBuffer.peek()).getIcmpType() == 11) {
                System.out.println("Reply from: " + applicationBuffer.remove().getSourceAddress() + ": TTL expired in transit");
                return -3;
            }
            else return -4;
        }
    }
    public void traceroute(String address) throws IPAddressIncorrectException {
        if(!AddressOperations.validateIP(address))
            throw new IPAddressIncorrectException();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Tracing route to " + address + " over maximum of 30 hops");
                int counter = 0;
                int ttl = 1;
                while(counter < 30){
                    try { Thread.sleep(1000); } catch (InterruptedException e) {e.getStackTrace();}
                    counter += 1;
                    Packet icmpEchoRequestPacket = PacketFactory.getPacket("ICMP_echo_request", address, eth0.getAddress(), ttl);
                    requestedFlag = true;
                    send(icmpEchoRequestPacket);
                    long start = System.currentTimeMillis();
                    for(int i=0; i<1000; i++) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {e.getStackTrace();}
                        if(!requestedFlag)
                            break;
                    }
                    if (requestedFlag) {
                        System.out.println(counter +".    Request timed out");
                        requestedFlag = false;
                    } else {
                        long elapsedTimeMillis = System.currentTimeMillis()-start;
                        if ( ((IcmpPacket) applicationBuffer.peek()).getIcmpType() == 0){
                            System.out.println(counter + ".\t" + elapsedTimeMillis + " ms\t" + applicationBuffer.remove().getSourceAddress());
                            System.out.println("\nTrace Complete" + "\n\n");
                            break;
                        } else if ( ((IcmpPacket) applicationBuffer.peek()).getIcmpType() == 3) {
                            if(((IcmpPacket) applicationBuffer.peek()).getIcmpCode() == 0) {
                                System.out.println(counter + ".\tDestination network unreachable");
                                ttl += 1;
                            }
                            else if(((IcmpPacket) applicationBuffer.peek()).getIcmpCode() == 1) {
                                System.out.println(counter + ".\tDestination host unreachable");
                                ttl += 1;
                            }
                        } else if ( ((IcmpPacket) applicationBuffer.peek()).getIcmpType() == 11) {
                            System.out.println(counter + ".\t" + elapsedTimeMillis + " ms\t" + applicationBuffer.remove().getSourceAddress());
                            ttl += 1;
                        }
                    }
                }
            }
        });
        thread.start();
    }
    private void send(Packet packet){
        boolean isForNeighbor = false;
        if (AddressOperations.getNetworkAddress(packet.getDestinationAddress(), eth0.getSubnetMask()).equals(AddressOperations.getNetworkAddress(eth0.getAddress(), eth0.getSubnetMask())))
            isForNeighbor = true;
        if(!isForNeighbor && defaultGateway == null){
            Packet icmpDestinationUnreachablePacket = PacketFactory.getPacket("ICMP_destination_network_unreachable", packet.getSourceAddress(), eth0.getAddress(), 32);
            send(icmpDestinationUnreachablePacket);
            return;
        }
        if(isForNeighbor) {
            if (!eth0.getBroadcastDomain().injectPacket(packet, packet.getDestinationAddress())) {
                Packet icmpDestinationUnreachablePacket = PacketFactory.getPacket("ICMP_destination_host_unreachable", packet.getSourceAddress(), eth0.getAddress(), 32);
                send(icmpDestinationUnreachablePacket);
            }
        } else {
            if (!eth0.getBroadcastDomain().injectPacket(packet, defaultGateway)) {
                Packet icmpDestinationUnreachablePacket = PacketFactory.getPacket("ICMP_destination_host_unreachable", packet.getSourceAddress(), eth0.getAddress(), 32);
                send(icmpDestinationUnreachablePacket);
            }
        }
    }
    public void setDefaultGateway(String defaultGateway){
        this.defaultGateway = defaultGateway;
    }
    private boolean isForMe(String address){
        return eth0.getAddress().equals(address);
    }
    public void removePort(){
        eth0.removeFromBroadcastDomain();
        eth0 = null;
    }
    @Override
    public void shutdown(){
        running = false;
        eth0.shutdown();
    }
    public void run(){
        eth0.noShutdown();
        running = true;
        while(running)
        {
            try {
                Thread.sleep(1); //buffer reading speed
            } catch (InterruptedException e) {e.getStackTrace();}
                if (!eth0.isBufferEmpty())
                    servePacket(eth0.readBuffer());
        }
    }
    public Pc(String hostname) {
        super(hostname);
    }
}
