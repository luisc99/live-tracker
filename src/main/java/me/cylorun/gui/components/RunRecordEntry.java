package me.cylorun.gui.components;

import me.cylorun.gui.RunEditorPanel;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RunRecordEntry extends JPanel {
    public RunRecordEntry(RunEditorPanel.RunRecord record) {
        this.setLayout(new BorderLayout());
        Date date = new Date(record.getDatePlayedEst());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

        JLabel runIdLabel = new JLabel(String.format("<html>Run Id: <b>%s<b> </html>", record.getRunId(), record.getWorldName()));
        runIdLabel.setToolTipText(String.format("Date Played: %s\n World Name: %s", dateFormat.format(date), record.getWorldName()));
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.add(runIdLabel, BorderLayout.WEST);
        contentPanel.add(buttonPanel, BorderLayout.EAST);

        add(contentPanel, BorderLayout.CENTER);
    }
}
