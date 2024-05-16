package me.cylorun.gui.components;

import javax.swing.*;

public class ProgressBar extends JFrame {
    private final JProgressBar progressBar;
    private final JLabel label;

    public ProgressBar(int maxVal, int minVal, String labelText) {
        super("Progress");
        setSize(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        label = new JLabel(labelText, SwingConstants.CENTER);

        progressBar = new JProgressBar(minVal, maxVal);
        progressBar.setStringPainted(true);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(label);
        getContentPane().add(progressBar);

        setVisible(true);
    }

    public void setValue(int value) {
        progressBar.setValue(value);
    }
    public int getValue() {
        return progressBar.getValue();
    }
}
