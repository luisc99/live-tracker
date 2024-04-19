package me.cylorun.io.minecraft.world;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.enums.SpeedrunEventType;
import me.cylorun.io.minecraft.RecordFile;
import me.cylorun.io.minecraft.Run;
import me.cylorun.io.minecraft.SpeedrunEvent;
import me.cylorun.utils.ExceptionUtil;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WorldFile extends File implements WorldEventListener {
    private final WorldEventHandler eventHandler;
    public boolean track = true;
    public boolean finished = false;

    public WorldFile(String path) {
        super(path);
        this.eventHandler = new WorldEventHandler(this);
        this.eventHandler.addListener(this);
    }

    public Path getRecordPath() {
        return Paths.get(this.getAbsolutePath()).resolve("speedrunigt").resolve("record.json");
    }

    public Path getEventLog() {
        return Paths.get(this.getAbsolutePath()).resolve("speedrunigt").resolve("events.log");
    }

    public Path getLogPath() {
        return Paths.get(this.getAbsolutePath()).getParent().getParent().resolve("logs").resolve("latest.log");
    }

    public String getUsername() throws IOException {
        // [11:30:07] [Render thread/INFO]: Setting user: cylorun
        String regex = "^\\[\\d{2}:\\d{2}:\\d{2}\\] \\[Render thread\\/INFO\\]: Setting user: .*$";
        Pattern pattern = Pattern.compile(regex);
        BufferedReader reader = new BufferedReader(new FileReader(this.getLogPath().toFile()));

        String line;
        String username = "";
        while ((line = reader.readLine()) != null) {
            if (pattern.matcher(line).find()) {
                String[] split = line.split(":");
                username = split[split.length-1].trim();
            }
        }
        return username;
    }


    public List<Object> getEverything(RecordFile record) {
        List<Object> res = new ArrayList<>();


        return res;
    }

    public void onCompletion(SpeedrunEvent event) {
        this.finished = true;
        FileReader reader = null;
        try {
            reader = new FileReader(this.getRecordPath().toFile());
        } catch (FileNotFoundException e) {
            ExceptionUtil.showError(e);
            throw new RuntimeException(e);
        }
        JsonObject o = JsonParser.parseReader(reader).getAsJsonObject();
        RecordFile record = new RecordFile(o);
        Run thisRun = new Run(this, record);

    }

    @Override
    public void onNewEvent(SpeedrunEvent e) {
        if (!this.finished) {
            if (e.type.equals(SpeedrunEventType.REJOIN_WORLD)) {
                this.track = true;
            }

            if (this.track) {
                if (e.type.equals(SpeedrunEventType.LEAVE_WORLD)) {
                    this.track = false;
                }

                if (e.type.equals(SpeedrunEventType.CREDITS)) {
                    this.onCompletion(e);
                }

                System.out.printf("Name: %s, Event: %s\n", this.getName(), e);
            }
        }
    }
}
