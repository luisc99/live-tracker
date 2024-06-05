package me.cylorun.gui.components;

import me.cylorun.gui.RunEditorPanel;
import me.cylorun.utils.APIUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RunRecordEntry extends JPanel {
    private final JButton deleteButton;
    private final JButton editButton;

    public RunRecordEntry(RunEditorPanel.RunRecord record) {
        this.setLayout(new BorderLayout());
        this.deleteButton = new JButton("Delete");
        this.editButton = new JButton("Edit");
        Date date = new Date(record.getDatePlayedEst());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

        JLabel runIdLabel = new JLabel(String.format("<html>Run Id: <b>%s<b> </html>", record.getRunId(), record.getWorldName()));
        runIdLabel.setToolTipText(String.format("Date Played: %s\n World Name: %s", dateFormat.format(date), record.getWorldName()));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(this.editButton);
        buttonPanel.add(this.deleteButton);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.add(runIdLabel, BorderLayout.WEST);
        contentPanel.add(buttonPanel, BorderLayout.EAST);

        this.add(contentPanel, BorderLayout.CENTER);
    }

    private boolean deleteRun() {
        OkHttpClient client = new OkHttpClient();

        Request req = new Request.Builder()
                .url(APIUtil.API_URL + "/delete")
                .build();
        return true;
    }
}
