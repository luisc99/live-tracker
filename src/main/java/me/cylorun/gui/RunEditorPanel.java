package me.cylorun.gui;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import me.cylorun.Tracker;
import me.cylorun.gui.components.ActionButton;
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

    private final JPanel runRecordPanel;
    private boolean isFetching = false;

    public RunEditorPanel() {
        this.runRecordPanel = new JPanel();
        this.runRecordPanel.setLayout(new BoxLayout(runRecordPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(this.runRecordPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        this.add(scrollPane);
        this.runRecordPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        fetchData();
    }

    private void fetchData() {
        if (this.isFetching) {
            return;
        }

        this.isFetching = true;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(APIUtil.API_URL + "/runs/recent")
                .build();

        new SwingWorker<List<RunRecord>, Void>() {
            @Override
            protected List<RunRecord> doInBackground() {
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        Tracker.log(Level.WARN, "Failed to fetch run data, status code: " + response.code());
                        return null;
                    }

                    Gson gson = new Gson();
                    Type runListType = new TypeToken<List<RunRecord>>() {}.getType();
                    String jsonString = response.body().string();

                    return gson.fromJson(jsonString, runListType);
                } catch (Exception e) {
                    Tracker.log(Level.WARN, "Failed to fetch run data: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void done() {
                isFetching = false;
                try {
                    List<RunRecord> runs = get();
                    runRecordPanel.removeAll();
                    if (runs == null) {
                        runRecordPanel.add(new JLabel("Something went wrong"));
                        runRecordPanel.add(new ActionButton("Try again", e -> fetchData()));
                    } else {
                        for (RunRecord run : runs) {
                            RunRecordEntry entry = new RunRecordEntry(run);
                            JPanel entryPanel = new JPanel(new BorderLayout());
                            entryPanel.add(entry, BorderLayout.NORTH);
                            entryPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                            runRecordPanel.add(entryPanel);
                        }
                    }
                    runRecordPanel.revalidate();
                    runRecordPanel.repaint();
                } catch (Exception e) {
                    Tracker.log(Level.WARN, "Failed to process fetched data: " + e.getMessage());
                }
            }
        }.execute();
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
