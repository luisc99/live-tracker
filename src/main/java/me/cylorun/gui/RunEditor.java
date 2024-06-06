package me.cylorun.gui;

import me.cylorun.utils.ResourceUtil;

import javax.swing.*;
import java.io.IOException;

public class RunEditor extends JPanel {
    private final RunPanel.RunRecord record;
    public RunEditor(RunPanel.RunRecord record) {
        this.record = record;
        this.add(new JLabel(this.record.toString()));


        JButton backButton = new JButton("Back");
        try {
            backButton.setIcon(new ImageIcon(ResourceUtil.loadImageResource("icons/back.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.add(backButton);
        backButton.addActionListener((e)->{
            this.back();
        });


        TrackerFrame.getInstance().setView(this);
        TrackerFrame.getInstance().repaint();
        TrackerFrame.getInstance().revalidate();
    }

    private void back() {
        TrackerFrame.getInstance().resetToInitialView();
    }
}
