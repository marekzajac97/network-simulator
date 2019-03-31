package com.gui;

import com.networkComponents.Network;
import com.networkComponents.connectors.Port;
import com.networkComponents.devices.Device;
import com.networkComponents.devices.Pc;
import com.networkComponents.devices.router.Router;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class NetworkConfiguration implements LinkObserver {
    private JComboBox<String> cbDevice;
    private JButton bAddRouter;
    private JButton bDeleteDevice;
    private JButton bAddPC;
    private JPanel NetConfigPanel;
    private Network net;
    private ArrayList<JFrame> routerFrames = new ArrayList<>();
    private ArrayList<JFrame> pcFrames = new ArrayList<>();
    private int findRouterFrame(String frameName){
        for(int i=0;i<routerFrames.size();i++)
            if (routerFrames.get(i).getTitle().equals(frameName))
                return i;
        return -1;
    }
    private int findPcFrame(String frameName){
        for(int i=0;i<pcFrames.size();i++)
            if (pcFrames.get(i).getTitle().equals(frameName))
                return i;
        return -1;
    }
    private void setSubscription(RouterConfiguration routerConfig){
        routerConfig.subscribe(this);
    }
    private void setSubscription(PcConfiguration pcConfig){
        pcConfig.subscribe(this);
    }
    public void inform(Port port, String broadcastDomainName){
        net.addToBroadcastDomain(port, broadcastDomainName);
    }
    private NetworkConfiguration(Network net) {
        this.net = net;
        cbDevice.addItem(null);

        bAddRouter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hostname = JOptionPane.showInputDialog(null, "Enter router's hostname");
                if(hostname != null) {
                    if(findRouterFrame(hostname + " Configuration") == -1 && findPcFrame(hostname + " Configuration") == -1) {
                        Router newRouter = new Router(hostname);
                        net.addNewRouter(newRouter);
                        cbDevice.addItem(hostname);
                        JFrame frame = new JFrame(hostname + " Configuration");
                        routerFrames.add(frame);
                        RouterConfiguration newRouterGUI = new RouterConfiguration(newRouter);
                        newRouter.subscribe(newRouterGUI);
                        setSubscription(newRouterGUI);
                        frame.setContentPane(newRouterGUI.getPanel());
                        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                        frame.pack();
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(false);
                    }
                    else
                        JOptionPane.showMessageDialog(null, "Device already exists!");
                }
            }
        });
        bAddPC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hostname = JOptionPane.showInputDialog(null, "Enter PC's hostname");
                if(hostname != null) {
                    if(findPcFrame(hostname + " Configuration") == -1 && findRouterFrame(hostname + " Configuration") == -1) {
                        Pc newPc = new Pc(hostname);
                        net.addNewPC(newPc);
                        cbDevice.addItem(hostname);
                        JFrame frame = new JFrame(hostname + " Configuration");
                        pcFrames.add(frame);
                        PcConfiguration newPcGUI = new PcConfiguration(newPc);
                        setSubscription(newPcGUI);
                        frame.setContentPane(newPcGUI.getPanel());
                        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                        frame.pack();
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(false);
                    }
                    else
                        JOptionPane.showMessageDialog(null, "Device already exists!");
                }
            }
        });
        cbDevice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                String hostname = (String)cb.getSelectedItem();
                if(hostname != null) {
                    Device deviceToConfigure = net.findDevice(hostname);
                    if (deviceToConfigure instanceof Router) {
                        int index = findRouterFrame(deviceToConfigure.getHostname() + " Configuration");
                        routerFrames.get(index).setVisible(true);

                    }
                    if(deviceToConfigure instanceof Pc) {
                        int index = findPcFrame(deviceToConfigure.getHostname() + " Configuration");
                        pcFrames.get(index).setVisible(true);
                }
                }
            }
        });
        bDeleteDevice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hostname = JOptionPane.showInputDialog(null, "Enter device's hostname");
                if(hostname != null){
                    Device deviceToDelete = net.findDevice(hostname);
                    if (deviceToDelete == null)
                        JOptionPane.showMessageDialog(null, "No such device");
                    else {
                        if (deviceToDelete instanceof Router) {
                            routerFrames.remove(findRouterFrame(hostname + " Configuration")).dispose();
                            net.removeRouter(hostname);
                            cbDevice.removeItem(hostname);
                        }
                        if (deviceToDelete instanceof Pc) {
                            pcFrames.remove(findPcFrame(hostname + " Configuration")).dispose();
                            net.removePC(hostname);
                            cbDevice.removeItem(hostname);
                        }
                    }
                }

            }
        });
    }

    public static void main(String[] args) {

        Network net = new Network();
        JFrame frame = new JFrame("Network Configuration");
        frame.setContentPane(new NetworkConfiguration(net).NetConfigPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
