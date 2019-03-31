package com.addressOperations;

public class AddressOperations {
    public static String getBroadcastAddress(String ipAddress, String subnetMask) {
        String ipNetwork = getNetworkAddress(ipAddress, subnetMask);
        String[] mask     = subnetMask.split("\\.");
        String[] network = ipNetwork.split("\\.");
        StringBuffer ipBroadcast  = new StringBuffer();
        for(int i=0; i<4; i++)
            try{
                if(ipBroadcast.length()>0)
                    ipBroadcast.append('.');
                ipBroadcast.append(Integer.parseInt(network[i]) ^ (~(Integer.parseInt(mask[i])) & 0xff));
            }catch(Exception x){
                System.out.println(x.getMessage());
            }
        return ipBroadcast.toString();
    }
    public static String getNetworkAddress(String ipAddress, String subnetMask) {
        String[] mask = subnetMask.split("\\.");
        String[] address = ipAddress.split("\\.");
        StringBuffer ipNetwork  = new StringBuffer();
        for(int i=0; i<4; i++)
            try{
                if(ipNetwork.length()>0)
                    ipNetwork.append('.');
                ipNetwork.append(Integer.parseInt(address[i]) & Integer.parseInt(mask[i]));
            }catch(Exception x){
                System.out.println(x.getMessage());
            }
        return ipNetwork.toString();
    }
    public static String getSubnetMask(int mask){
        if(mask == 0) return "0.0.0.0";
        long bits = 0;
        bits = 0xffffffff ^ (1 << 32 - mask) - 1;
        return String.format("%d.%d.%d.%d", (bits & 0x0000000000ff000000L) >> 24, (bits & 0x0000000000ff0000) >> 16, (bits & 0x0000000000ff00) >> 8, bits & 0xff);
    }
    public static boolean validateSubnetMask(String subnetMask){
        if(validateIP(subnetMask)) {
            for (int i = 0; i < 32; i++) {
                if (subnetMask.equals(getSubnetMask(i)))
                    return true;
            }
            return false;
        }
        else return false;
    }
    public static boolean validateIP(String address){
        if(address.endsWith("."))
            return false;
        String[] ip = address.split("\\.");
        if(ip.length != 4)
            return false;
        else{
            for (int i=0;i<4;i++)
                try {
                    if (Integer.parseInt(ip[i]) > 255 || Integer.parseInt(ip[i]) < 0)
                        return false;
                }
                catch (Exception e){
                return false;
                }
        }
        return true;
    }
    public static boolean validateNetwork(String address, String subnetMask){
        if(!validateIP(address))
            return false;
        else if(!validateSubnetMask(subnetMask))
            return false;
        else if(address.equals(getNetworkAddress(address, subnetMask)))
            return true;
        else
            return false;
    }
}
