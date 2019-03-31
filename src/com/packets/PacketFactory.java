package com.packets;

import com.packets.ripPacket.RipInformation;
import com.packets.ripPacket.RipPacket;

import java.util.ArrayList;

public class PacketFactory {
    public static Packet getPacket(String type, String destinationAddress, String sourceAddress, int ttl) {
        if ("ICMP_echo_request".equalsIgnoreCase(type))
            return new IcmpPacket(destinationAddress, sourceAddress, ttl, 8, 0);
        else if ("ICMP_echo_reply".equalsIgnoreCase(type))
            return new IcmpPacket(destinationAddress, sourceAddress, ttl, 0, 0);
        else if ("ICMP_destination_network_unreachable".equalsIgnoreCase(type))
            return new IcmpPacket(destinationAddress, sourceAddress, ttl, 3, 0);
        else if ("ICMP_destination_host_unreachable".equalsIgnoreCase(type))
            return new IcmpPacket(destinationAddress, sourceAddress, ttl, 3, 1);
        else if ("ICMP_time_exceeded".equalsIgnoreCase(type))
            return new IcmpPacket(destinationAddress, sourceAddress, ttl, 11, 0);
        else
            return null;
    }
    public static Packet getPacket(String type, String destinationAddress, String sourceAddress, int ttl, ArrayList<RipInformation> ripInfo) {
        if ("RIP".equalsIgnoreCase(type))
            return new RipPacket(destinationAddress, sourceAddress, ttl, ripInfo);
        else
            return null;
    }
}
