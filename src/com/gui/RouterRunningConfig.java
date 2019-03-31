package com.gui;

import com.networkComponents.connectors.Port;
import com.networkComponents.devices.router.*;
import java.awt.Font;

import javax.swing.*;

public class RouterRunningConfig {
    private JTextArea taRunningConfig;
    private JPanel RunningConfig;
    private Router router;
    public JPanel getPanel() {
        return RunningConfig;
    }
    public void refresh(){
        taRunningConfig.setText(null);
        taRunningConfig.setEditable(false);
        taRunningConfig.setFont(new Font("Courier New", Font.PLAIN, 12));
        taRunningConfig.append("\nINTERFACES\n");
        taRunningConfig.append("Interface" + "\tIP Address" + "\tSubnet Mask     " + "\tStatus" + "\n");
        for(int i=0;i<router.getEthSize();i++){
            Port port = router.getEth(i);
            taRunningConfig.append("Ethernet" + port.getIndex() + "\t" + (port.getAddress()==null?"not set":port.getAddress()) + "\t" + (port.getSubnetMask()==null?"not set":port.getSubnetMask()) + "\t" + (port.isShutdown()?"down":"up") + "\n");
        }
        taRunningConfig.append("\nROUTING TABLE\n");
        for(int i=0;i<router.getRoutingTableSize();i++){
            RoutingTableEntry entry = router.getRoutingTableEntry(i);
            taRunningConfig.append((entry.getHopCount() == 0)?((entry.isDirectlyConnected())?"C":"S"):"R");
            taRunningConfig.append("  " + entry.getNetworkAddress() + "  " + entry.getSubnetMask());
            taRunningConfig.append("  " + (entry.isDirectlyConnected()?(" is directly connected, eth" + entry.getPortIndex()):(" via " + entry.getNextHopAddress() + ", eth" + entry.getPortIndex())) + "\n");
        }
    }
    RouterRunningConfig(Router router) {
        this.router = router;
    }
}
