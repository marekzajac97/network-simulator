package com.gui;

import javax.swing.*;
import java.awt.Font;

public class PcOutput {
    private JPanel pcOutputPanel;
    private JTextArea taOutput;

    public JTextArea getTextArea(){
        return taOutput;
    }
    public JPanel getPanel(){
        return pcOutputPanel;
    }
    PcOutput(){
        taOutput.setEditable(false);
        taOutput.setFont(new Font("Courier New", Font.PLAIN, 12));
    }
}

