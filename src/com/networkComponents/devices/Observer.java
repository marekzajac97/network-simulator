package com.networkComponents.devices;

import com.networkComponents.connectors.Port;
import com.networkComponents.devices.router.RoutingTableEntry;

public interface Observer {
    void inform(Port port, boolean flushEntry); //to inform router that a port changed its ip address
    void inform(RoutingTableEntry entry); // to inform router that an entry needs to be removed due to the flush time reaching zero
}
