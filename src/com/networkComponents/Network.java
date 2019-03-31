package com.networkComponents;

import com.networkComponents.connectors.BroadcastDomain;
import com.networkComponents.connectors.Port;
import com.networkComponents.devices.Device;
import com.networkComponents.devices.Pc;
import com.networkComponents.devices.router.Router;

import java.util.ArrayList;

public class Network {
    private ArrayList<Router> routers = new ArrayList<>();
    private ArrayList<Pc> pcs = new ArrayList<>();
    private ArrayList<BroadcastDomain> broadcastDomains = new ArrayList<>();

    public void addNewRouter(Router router){
        routers.add(router);
    }
    public void addNewPC(Pc pc){
        pcs.add(pc);
    }
    public void addNewBroadcastDomain(BroadcastDomain broadcastDomain){
        broadcastDomains.add(broadcastDomain);
    }
    public void addToBroadcastDomain(Port port, String broadcastDomainName){
        port.removeFromBroadcastDomain();
        boolean domain_exists = false;
        for(int i = 0; i< broadcastDomains.size(); i++)
            if(broadcastDomains.get(i).getName().equals(broadcastDomainName)) {
                domain_exists = true;
                broadcastDomains.get(i).addPort(port);
                port.setBroadcastDomain(broadcastDomains.get(i));
            }
        if(!domain_exists){
            BroadcastDomain new_domain = new BroadcastDomain(broadcastDomainName);
            new_domain.addPort(port);
            port.setBroadcastDomain(new_domain);
            addNewBroadcastDomain(new_domain);
        }
    }
    public Device findDevice(String hostname){
        for (int i=0;i<routers.size();i++){
            if(routers.get(i).getHostname().equals(hostname))
                return routers.get(i);
        }
        for (int i=0;i<pcs.size();i++){
            if(pcs.get(i).getHostname().equals(hostname))
                return pcs.get(i);
        }
        return null;
    }
    public void removeRouter(String hostname){
        for(int i=0;i<routers.size();i++) {
            if(routers.get(i).getHostname().equals(hostname)) {
                routers.get(i).removeAllPorts();
                routers.remove(i);
            }
        }
    }
    public void removePC(String hostname){
        for(int i=0;i<pcs.size();i++) {
            if(pcs.get(i).getHostname().equals(hostname)) {
                pcs.get(i).removePort();
                pcs.remove(i);
            }
        }
    }
}
