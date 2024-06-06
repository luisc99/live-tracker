package me.cylorun.gui;

import com.google.gson.JsonObject;
import me.cylorun.utils.ResourceUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RunEditor extends JPanel {
    private final JsonObject record;
    private final JPanel configPanel;

    public RunEditor(JsonObject record) {
        this.record = record;

        this.setLayout(new BorderLayout());
        JPanel topBar = new JPanel();
        topBar.setLayout(new BoxLayout(topBar, BoxLayout.X_AXIS));
        JButton backButton = new JButton("Back");
        try {
            backButton.setIcon(new ImageIcon(ResourceUtil.loadImageResource("icons/back.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        topBar.add(backButton);
        topBar.add(Box.createRigidArea(new Dimension(10, 0)));
        topBar.add(new JLabel(String.format("Run %s", this.record.get("run_id").getAsInt())));

        backButton.addActionListener((e) -> TrackerFrame.getInstance().resetToInitialView());

        this.add(topBar, BorderLayout.NORTH);
        this.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);

        this.configPanel = new JPanel();
        this.configPanel.setLayout(new BoxLayout(this.configPanel, BoxLayout.Y_AXIS));
        this.add(this.configPanel, BorderLayout.SOUTH);

        TrackerFrame.getInstance().setView(this);
    }
}
