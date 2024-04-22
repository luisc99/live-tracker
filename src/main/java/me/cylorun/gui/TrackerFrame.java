package me.cylorun.gui;

import com.formdev.flatlaf.FlatDarculaLaf;
import me.cylorun.Tracker;
import org.lwjgl.system.CallbackI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TrackerFrame extends JFrame {

    private final String NAME = "Live-Tracker"+ Tracker.VERSION;
    private Point windowDragStartCoords;
    public TrackerFrame() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatDarculaLaf());
        this.setSize(400, 300);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton settingsButton = new JButton("Settings");

        settingsButton.addActionListener((e)->{

        });
        this.add(settingsButton);
    }


}
