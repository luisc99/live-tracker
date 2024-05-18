package me.cylorun.gui;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import me.cylorun.Tracker;
import me.cylorun.gui.components.RunRecordEntry;
import me.cylorun.utils.APIUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.Level;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.List;

public class RunEditorPanel extends JPanel {

    private JPanel runRecordPanel;

    public RunEditorPanel() {
        this.runRecordPanel = new JPanel();
        this.runRecordPanel.setLayout(new BoxLayout(runRecordPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(runRecordPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        this.add(scrollPane);
        this.runRecordPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<RunRecord> runs = this.fetchData();
        if (runs == null) {
            this.add(new JLabel("Something went wrong"));
            return;
        }

        for (RunRecord run : runs) {
            RunRecordEntry entry = new RunRecordEntry(run);
            JPanel entryPanel = new JPanel(new BorderLayout());

            entryPanel.add(entry, BorderLayout.NORTH);
            entryPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            this.runRecordPanel.add(entryPanel);
        }
    }

    private List<RunRecord> fetchData() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(APIUtil.API_URL + "/runs/recent")
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                Tracker.log(Level.WARN, "Failed to fetch run data, status code: "+response.code());
                return null;
            }

            Gson gson = new Gson();
            Type runListType = new TypeToken<List<RunRecord>>() {}.getType();
            String jsonString = response.body().string();

            return gson.fromJson(jsonString, runListType);
        } catch (Exception e) {
            Tracker.log(Level.WARN, "Failed to fetch run data\n"+e);
            return null;
        }
    }
    public static class RunRecord {
        @SerializedName("run_id")
        private int runId;

        @SerializedName("date_played_est")
        private long datePlayedEst;

        @SerializedName("world_name")
        private String worldName;

        public int getRunId() {
            return runId;
        }
        public long getDatePlayedEst() {
            return datePlayedEst;
        }
        public String getWorldName() {
            return worldName;
        }

        @Override
        public String toString() {
            return "Run{" +
                    "runId=" + runId +
                    ", datePlayedEst=" + datePlayedEst +
                    ", worldName='" + worldName + '\'' +
                    '}';
        }
    }
}
