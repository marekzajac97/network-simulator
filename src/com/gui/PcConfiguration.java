package com.gui;

import com.addressOperations.AddressOperations;
import com.networkComponents.connectors.Port;
import com.networkComponents.devices.IPAddressIncorrectException;
import com.networkComponents.devices.Pc;

import java.io.PrintStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PcConfiguration implements LinkObserver {
    private JPanel PcConf;
    private JTextField tfAddress;
    private JTextField tfSubnet;
    private JButton bTraceroute;
    private JButton bPing;
    private JTextField tfGateway;
    private JButton bSetAddress;
    private JButton bBroadcastDomain;
    private JButton bStartup;
    private JButton bShutdown;
    private JTextField tfBroadcastDomain;
    private JButton bSetDGateway;
    private JFrame pcOutputFrame;
    private PcOutput newPcOutputObject;
    private LinkObserver o;
    private Pc pc;
    public JPanel getPanel(){
        return PcConf;
    }
    public void inform(Port port, String broadcastDomainName){
        o.inform(port, broadcastDomainName);
    }
    public void subscribe(LinkObserver o){
        this.o = o;
    }

    PcConfiguration(Pc pc) {
        this.pc = pc;

        bShutdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pc.shutdown();
                bStartup.setBackground(null);
            }
        });
        bStartup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pc.isRunning())
                    JOptionPane.showMessageDialog(null, "PC is already running");
                else {
                    if(pc.getEth0().validate()) {
                        Thread pcThread = new Thread(pc);
                        pcThread.start();
                        bStartup.setBackground(Color.GREEN);
                    }
                    else
                        JOptionPane.showMessageDialog(null, "Configuration incomplete! Please check the fields and don't forget to click on 'Set'");
                }
            }
        });
        bPing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!pc.isRunning()){
                    JOptionPane.showMessageDialog(null, "PC must be running!");
                    return;
                }
                String address = JOptionPane.showInputDialog(null, "Enter address");
                if(address != null) {
                    if(pcOutputFrame == null){
                        pcOutputFrame = new JFrame(pc.getHostname() + " Output");
                        newPcOutputObject = new PcOutput();
                        pcOutputFrame.setContentPane(newPcOutputObject.getPanel());
                        pcOutputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        //pcOutputFrame.pack();
                        pcOutputFrame.setSize(500,300);
                        pcOutputFrame.setLocationRelativeTo(null);
                        pcOutputFrame.setVisible(true);
                    }
                    else {
                        pcOutputFrame.setVisible(true);
                    }
                    try {
                        PrintStream myStream = new PrintStream(new CustomOutputStream(newPcOutputObject.getTextArea()));
                        //PrintStream standardOut = System.out;
                        System.setOut(myStream);
                        pc.ping(address);
                        //System.setOut(standardOut);
                    }
                    catch ( IPAddressIncorrectException exce){
                        JOptionPane.showMessageDialog(null, "IP Address is incorrect!");
                    }
                }
            }
        });
        bTraceroute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!pc.isRunning()){
                    JOptionPane.showMessageDialog(null, "PC must be running!");
                    return;
                }
                String address = JOptionPane.showInputDialog(null, "Enter address");
                if(address != null) {
                    if(pcOutputFrame == null){
                        pcOutputFrame = new JFrame(pc.getHostname() + " Output");
                        newPcOutputObject = new PcOutput();
                        pcOutputFrame.setContentPane(newPcOutputObject.getPanel());
                        pcOutputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        //pcOutputFrame.pack();
                        pcOutputFrame.setSize(500,300);
                        pcOutputFrame.setLocationRelativeTo(null);
                        pcOutputFrame.setVisible(true);
                    }
                    else {
                        pcOutputFrame.setVisible(true);
                    }
                    try {
                        PrintStream myStream = new PrintStream(new CustomOutputStream(newPcOutputObject.getTextArea()));
                        //PrintStream standardOut = System.out;
                        System.setOut(myStream);
                        pc.traceroute(address);
                        //System.setOut(standardOut);
                    }
                    catch ( IPAddressIncorrectException exce){
                        JOptionPane.showMessageDialog(null, "IP Address is incorrect!");
                    }
                }
            }
        });
        bBroadcastDomain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( tfBroadcastDomain.getText() != null){
                    o.inform(pc.getEth0(), tfBroadcastDomain.getText());
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
                    pc.getEth0().setAddress(tfAddress.getText(), tfSubnet.getText());
            }
        });
        bSetDGateway.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tfGateway.getText() == null)
                    JOptionPane.showMessageDialog(null, "Default gateway field is empty!");
                else if (!AddressOperations.validateIP(tfGateway.getText()))
                    JOptionPane.showMessageDialog(null, "Default gateway is incorrect!");
                else
                    pc.setDefaultGateway(tfGateway.getText());
            }
        });
    }
}
