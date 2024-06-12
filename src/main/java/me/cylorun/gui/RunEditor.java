package me.cylorun.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.Tracker;
import me.cylorun.gui.components.ColorPicker;
import me.cylorun.gui.components.MultiChoiceOptionField;
import me.cylorun.gui.components.TextEditor;
import me.cylorun.gui.components.TextOptionField;
import me.cylorun.io.TrackerOptions;
import me.cylorun.utils.APIUtil;
import me.cylorun.utils.JSONUtil;
import me.cylorun.utils.ResourceUtil;
import okhttp3.*;
import org.apache.logging.log4j.Level;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RunEditor extends JPanel {
    private final JsonObject record;
    private final JPanel configPanel;
    private final MultiChoiceOptionField columnField;
    private TextOptionField valueField;
    private TextEditor textEditorField;
    private ColorPicker colorChooser;
    private final JButton saveButton;
    private JsonObject runData;
    private boolean isFetching = false;
    private Color prevColor = Color.WHITE;

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
        this.add(new JScrollPane(this.configPanel), BorderLayout.CENTER);

        this.saveButton = new JButton("Save");
        this.colorChooser = new ColorPicker();

        this.valueField = new TextOptionField("Value", this.record.get("run_id").getAsString(), (val) -> this.saveButton.setEnabled(true));
        this.textEditorField = new TextEditor((val) -> this.saveButton.setEnabled(true));
        this.colorChooser.addConsumer((newCol -> this.saveButton.setEnabled(true)));

        this.textEditorField.setSize(new Dimension(200, 200));
        this.textEditorField.setVisible(false);

        this.columnField = new MultiChoiceOptionField(new String[]{}, "run_id", "Column", (val) -> {

            this.valueField.setVisible(false);
            this.textEditorField.setVisible(false);

            if (val.equals("notes")) {
                this.textEditorField.setValue(this.runData.get(val).getAsString());
                this.textEditorField.setVisible(true);
                this.valueField.setVisible(false);
            } else {
                this.valueField.setValue(this.runData.get(val).getAsString());
                this.valueField.setVisible(true);
                this.textEditorField.setVisible(false);
            }

            this.saveButton.setEnabled(false);
            this.configPanel.revalidate();
            this.configPanel.repaint();
        });

        this.configPanel.add(this.columnField);
        this.configPanel.add(this.valueField);
        this.configPanel.add(this.textEditorField);
        this.configPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        this.configPanel.add(new JLabel("Run Color"));
        this.configPanel.add(this.colorChooser);
        this.configPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        this.configPanel.add(Box.createVerticalStrut(10));
        this.configPanel.add(this.saveButton);
        this.configPanel.add(Box.createVerticalStrut(10));
        this.saveButton.setEnabled(false);
        this.saveButton.addActionListener((e -> {
            this.saveButton.setEnabled(false);
            String value = columnField.getValue().equals("notes") ? this.textEditorField.getHtmlText() : this.valueField.getValue();
            if (this.editRun(this.columnField.getValue(), value)) {
                Tracker.log(Level.INFO, "Successfully edited run " + this.record.get("run_id").getAsString());
            } else {
                Tracker.log(Level.ERROR, "Failed to edit run " + this.record.get("run_id").getAsString());
            }

            if (!this.prevColor.equals(this.colorChooser.getCurrentColor())) {
                this.prevColor = this.colorChooser.getCurrentColor();
                this.editRun("color", this.colorChooser.getColorString());
                Tracker.log(Level.INFO, "Successfully edited color for run " + this.record.get("run_id").getAsString());
            }
        }));

        this.fetchData();

        TrackerFrame.getInstance().setView(this);
    }

    public List<String> getAllValueKeys() {
        List<String> keys = new ArrayList<>();
        extractKeys(this.runData, keys);
        return keys;
    }

    private boolean editRun(String column, String value) {
        OkHttpClient client = new OkHttpClient();
        JsonObject o = new JsonObject();
        o.addProperty("column", column);
        o.addProperty("value", value);
        o.addProperty("id", this.record.get("run_id").getAsString());

        RequestBody body = RequestBody.create(o.toString(), MediaType.get("application/json; charset=utf-8"));
        Request req = new Request.Builder()
                .url(APIUtil.API_URL + "/edit")
                .post(body)
                .addHeader("authorization", TrackerOptions.getInstance().api_key)
                .build();

        try (Response res = client.newCall(req).execute()) {
            return res.code() == 200;
        } catch (Exception e) {
            return false;
        }
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
                    JsonObject r = this.get();
                    if (r == null) {
                        Tracker.log(Level.ERROR, "Failed to fetch data");
                        return;
                    }

                    runData = JSONUtil.flatten(r);
                    String[] values = getAllValueKeys().toArray(new String[0]);
                    columnField.setOptions(values);
                    Integer[] rgb = Arrays.stream(runData.get("color").getAsString().split(",")).map((e)->{
                        return Integer.parseInt(e.strip());
                    }).toArray(Integer[]::new);
                    Color color = new Color(rgb[0], rgb[1], rgb[2]);
                    colorChooser.setColor(color);
                } catch (InterruptedException | ExecutionException e) {
                    Tracker.log(Level.ERROR, "Failed to process run data: " + e);
                }
            }
        }.execute();
    }
}
