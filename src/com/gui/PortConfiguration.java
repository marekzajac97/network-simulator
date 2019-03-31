package com.gui;

import com.addressOperations.AddressOperations;
import com.networkComponents.connectors.Port;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PortConfiguration {
    private JTextField tfSubnet;
    private JTextField tfAddress;
    private JTextField tfBroadcastDomain;
    private JButton bSetBroadcastDomain;
    private JButton bSetAddress;
    private JPanel PortConfig;
    private JButton bShutdown;
    private JButton bNoShutdown;
    private Port port;
    private LinkObserver o;
    public JPanel getPanel(){
        return PortConfig;
    }
    public void subscribe(LinkObserver o){
        this.o = o;
    }

    PortConfiguration(Port port) {
        this.port = port;
        bSetBroadcastDomain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( tfBroadcastDomain.getText() != null){
                    o.inform(port, tfBroadcastDomain.getText());
                }
            }
        });
        bSetAddress.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( tfAddress.getText() == null)
                    JOptionPane.showMessageDialog(null, "Address field is empty!");
                else if (tfSubnet.getText() == null)
                    JOptionPane.showMessageDialog(null, "Subnet mask field is empty!");
                else if (!AddressOperations.validateIP(tfAddress.getText()))
                    JOptionPane.showMessageDialog(null, "IP Address is incorrect!");
                else if (!AddressOperations.validateSubnetMask(tfSubnet.getText()))
                    JOptionPane.showMessageDialog(null, "Subnet mask is incorrect!");
                else if (AddressOperations.validateNetwork(tfAddress.getText(), tfSubnet.getText()))
                        JOptionPane.showMessageDialog(null, "This is a network IP address!");
                else
                    port.setAddress(tfAddress.getText(), tfSubnet.getText());
            }
        });
        bShutdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!port.isShutdown()) {
                    port.shutdown();
                    bNoShutdown.setBackground(null);
                }
                else
                    JOptionPane.showMessageDialog(null, "Port is already down");
            }
        });
        bNoShutdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(port.isShutdown()) {
                    if (port.validate()) {
                        port.noShutdown();
                        bNoShutdown.setBackground(Color.GREEN);
                    } else
                        JOptionPane.showMessageDialog(null, "Configuration incomplete! Please check the fields and don't forget to click on 'Set'");
                }
            }
        });
    }
}
