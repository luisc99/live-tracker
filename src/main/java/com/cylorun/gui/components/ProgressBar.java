package com.cylorun.gui.components;

import javax.swing.*;

public class ProgressBar extends JFrame {
    private final JProgressBar progressBar;
    private final JLabel label;

    public ProgressBar(int maxVal, int minVal, String labelText) {
        super("Progress");
        setSize(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.label = new JLabel(labelText, SwingConstants.CENTER);

        this.progressBar = new JProgressBar(minVal, maxVal);
        this.progressBar.setStringPainted(true);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(this.label);
        getContentPane().add(this.progressBar);

        setVisible(true);
    }

    public void setValue(int value) {
        this.progressBar.setValue(value);
    }
    public int getValue() {
        return this.progressBar.getValue();
    }
}
