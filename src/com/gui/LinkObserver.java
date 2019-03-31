package com.gui;

import com.networkComponents.connectors.Port;

public interface LinkObserver {
    void inform(Port port, String broadcastDomainName);
}
