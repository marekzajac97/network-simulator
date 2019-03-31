package com.gui;

import com.addressOperations.AddressOperations;
import com.networkComponents.connectors.Port;
import com.networkComponents.devices.router.Router;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class RouterConfiguration implements LinkObserver, DiodeObserver {
    private JPanel RouterConfig;
    private JComboBox<String> cbPort;
    private JButton bStartup;
    private JButton bShutdown;
    private JButton bAddInt;
    private JButton bAddEntry;
    private JButton bConfRIP;
    private JButton bRemoveEntry;
    private JButton bShowRun;
    private JButton diode;
    private LinkObserver o;
    private Router router;
    private int portCount = 0;
    private JFrame runningConfigFrame;
    private RouterRunningConfig runningConfigObject;
    private ArrayList<JFrame> portFrames = new ArrayList<>();
    private void setSubscription(PortConfiguration portConfig){
        portConfig.subscribe(this);
    }
    public void inform(Port port, String broadcastDomainName){
        o.inform(port, broadcastDomainName);
    }
    public void subscribe(LinkObserver o){
        this.o = o;
    }
    public JPanel getPanel() {
        return RouterConfig;
    }
    private int findPortFrame(String frameName){
        for(int i=0;i<portFrames.size();i++)
            if (portFrames.get(i).getTitle().equals(frameName))
                return i;
        return -1;
    }
    public void enableDiode(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(diode.getBackground() != Color.GREEN) {
                    diode.setBackground(Color.GREEN);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.getStackTrace();
                    }
                    diode.setBackground(Color.WHITE);
                }
            }
        });
        thread.start();
    }

    RouterConfiguration(Router router) {
        this.router = router;
        cbPort.addItem(null);
        runningConfigObject = new RouterRunningConfig(router);
        diode.setBackground(Color.WHITE);

        cbPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                String index = (String)cb.getSelectedItem();
                if(index != null)
                    portFrames.get(findPortFrame(index + " Configuration")).setVisible(true);
            }
        });
        bAddInt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Port newPort = new Port(portCount);
                newPort.subscribe(router);
                router.addNewPort(newPort);
                cbPort.addItem("eth" + portCount);
                JFrame frame = new JFrame("eth" + portCount + " Configuration");
                portFrames.add(frame);
                PortConfiguration newPortGUI = new PortConfiguration(newPort);
                setSubscription(newPortGUI);
                frame.setContentPane(newPortGUI.getPanel());
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(false);
                portCount += 1;
            }
        });
        bShutdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                router.shutdown();
                bStartup.setBackground(null);
            }
        });
        bStartup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(router.isRunning())
                    JOptionPane.showMessageDialog(null, "Router is already running");
                else {
                    Thread routerThread = new Thread(router);
                    routerThread.start();
                    bStartup.setBackground(Color.GREEN);
                }
            }
        });
        bAddEntry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String networkAddress;
                String subnetMask;
                String nextHop;
                String port;
                boolean isCorrect = false;
                networkAddress = JOptionPane.showInputDialog(null, "Enter network address");
                while(!isCorrect) {
                    if (networkAddress != null) {
                        if (!AddressOperations.validateIP(networkAddress)) {
                            JOptionPane.showMessageDialog(null, "This is not a correct IP address. Please enter it again");
                            networkAddress = JOptionPane.showInputDialog(null, "Enter network address");
                            isCorrect = false;
                        } else
                            isCorrect = true;
                    } else
                        return;
                }
                isCorrect = false;
                subnetMask = JOptionPane.showInputDialog(null, "Enter subnet mask");
                while(!isCorrect) {
                    if (subnetMask != null) {
                        if (!AddressOperations.validateSubnetMask(subnetMask)) {
                            JOptionPane.showMessageDialog(null, "This is not a correct subnet mask. Please enter it again");
                            subnetMask = JOptionPane.showInputDialog(null, "Enter subnet mask");
                            isCorrect = false;
                        } else
                            isCorrect = true;
                    } else
                        return;
                }
                if(!AddressOperations.validateNetwork(networkAddress, subnetMask)) {
                    JOptionPane.showMessageDialog(null, "This is not a correct network address for this mask");
                    return;
                }
                isCorrect = false;
                nextHop = JOptionPane.showInputDialog(null, "Enter next hop address");
                while(!isCorrect) {
                    if (nextHop != null) {
                        if (!AddressOperations.validateIP(nextHop)) {
                            JOptionPane.showMessageDialog(null, "This is not a correct IP address. Please enter it again");
                            nextHop = JOptionPane.showInputDialog(null, "Enter next hop address");
                            isCorrect = false;
                        } else
                            isCorrect = true;
                    } else
                        return;
                }
                port = JOptionPane.showInputDialog(null, "Enter port index");
                if(port != null){
                    int portIndex;
                    try {
                        portIndex = Integer.parseInt(port);
                    } catch (NumberFormatException exce){
                        JOptionPane.showMessageDialog(null, "Could not add the entry! Entered value is not a port index");
                        return;
                    }
                    if(router.addStaticRoute(networkAddress, subnetMask, nextHop, portIndex) == -2)
                        JOptionPane.showMessageDialog(null, "Could not add the entry! Another entry with this network already exists");
                    else if(router.addStaticRoute(networkAddress, subnetMask, nextHop, portIndex) == -1)
                        JOptionPane.showMessageDialog(null, "Could not add the entry! No such interface");
                    else
                        JOptionPane.showMessageDialog(null, "Entry added successfully");
                }
            }
        });
        bRemoveEntry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String networkAddress;
                String subnetMask;
                boolean isCorrect = false;
                networkAddress = JOptionPane.showInputDialog(null, "Enter network address");
                while(!isCorrect) {
                    if (networkAddress != null) {
                        if (!AddressOperations.validateIP(networkAddress)) {
                            JOptionPane.showMessageDialog(null, "This is not a correct IP address. Please enter it again");
                            networkAddress = JOptionPane.showInputDialog(null, "Enter network address");
                            isCorrect = false;
                        } else
                            isCorrect = true;
                    } else
                        return;
                }
                isCorrect = false;
                subnetMask = JOptionPane.showInputDialog(null, "Enter subnet mask");
                while(!isCorrect) {
                    if (subnetMask != null) {
                        if (!AddressOperations.validateSubnetMask(subnetMask)) {
                            JOptionPane.showMessageDialog(null, "This is not a correct subnet mask. Please enter it again");
                            subnetMask = JOptionPane.showInputDialog(null, "Enter subnet mask");
                            isCorrect = false;
                        } else
                            isCorrect = true;
                    } else
                        return;
                }
                if(!AddressOperations.validateNetwork(networkAddress, subnetMask)) {
                    JOptionPane.showMessageDialog(null, "Network address does not match the subnet mask!");
                    return;
                }
                if(!router.removeStaticRoute(networkAddress, subnetMask))
                    JOptionPane.showMessageDialog(null, "Could not remove the entry! No such entry");
                else
                    JOptionPane.showMessageDialog(null, "Entry removed successfully");
            }
        });
        bConfRIP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!router.isRipEnabled()) {
                    router.enableRip();
                    bConfRIP.setBackground(Color.GREEN);
                } else{
                    router.disableRip();
                    bConfRIP.setBackground(null);
                }
            }
        });
        bShowRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(runningConfigFrame == null) {
                    runningConfigFrame = new JFrame(router.getHostname() + " Running Configuration");
                    runningConfigFrame.setContentPane(runningConfigObject.getPanel());
                    runningConfigFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    runningConfigObject.refresh();
                    runningConfigFrame.pack();
                    runningConfigFrame.setLocationRelativeTo(null);
                    runningConfigFrame.setVisible(true);
                }
                else{
                    runningConfigObject.refresh();
                    runningConfigFrame.pack();
                    runningConfigFrame.setVisible(true);
                }
            }
        });
    }
}
