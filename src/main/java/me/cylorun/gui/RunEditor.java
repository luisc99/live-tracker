package me.cylorun.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.Tracker;
import me.cylorun.gui.components.MultiChoiceOptionField;
import me.cylorun.utils.APIUtil;
import me.cylorun.utils.ResourceUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.Level;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RunEditor extends JPanel {
    private final JsonObject record;
    private final JPanel configPanel;
    private final MultiChoiceOptionField columnField;
    private JsonObject runData;
    private boolean isFetching = false;
    private String[] columnValues = {};

    public RunEditor(JsonObject runRecord) {
        this.record = runRecord;

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

        this.columnField = new MultiChoiceOptionField(this.columnValues, "run_id", "Column", (val) -> {

        });

        this.configPanel.add(columnField);
        this.fetchData();
        TrackerFrame.getInstance().setView(this);
    }

    public List<String> getAllKeys() {
        List<String> keys = new ArrayList<>();
        extractKeys(this.runData, keys);
        return keys;
    }

    private void extractKeys(JsonObject jsonObject, List<String> keys) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (entry.getValue().isJsonPrimitive()) {
                keys.add(entry.getKey());
            }
            if (entry.getValue().isJsonObject()) {
                extractKeys(entry.getValue().getAsJsonObject(), keys);
            }
        }
    }

    private void fetchData() {
        if (this.isFetching) {
            return;
        }
        this.isFetching = true;

        new SwingWorker<JsonObject, Void>() {
            @Override
            protected JsonObject doInBackground() {
                OkHttpClient client = new OkHttpClient();
                Request req = new Request.Builder()
                        .url(APIUtil.API_URL + "/runs?id=" + record.get("run_id").getAsString())
                        .get()
                        .build();

                try (Response res = client.newCall(req).execute()) {
                    String jsonData = res.body().string();
                    return JsonParser.parseString(jsonData).getAsJsonObject();
                } catch (Exception e) {
                    Tracker.log(Level.ERROR, "Failed to fetch data: " + e);
                }
                return null;
            }

            @Override
            protected void done() {
                isFetching = false;
                try {
                    runData = this.get();
                    String[] values = getAllKeys().toArray(new String[0]);
                    columnField.setOptions(values);

                } catch (InterruptedException | ExecutionException e) {
                    Tracker.log(Level.ERROR, "Failed to process run data: " + e);
                }
            }
        }.execute();
    }
}
