package me.cylorun.io.minecraft.world;

import com.google.gson.JsonObject;
import me.cylorun.enums.SpeedrunEventType;
import me.cylorun.io.minecraft.LogEvent;
import me.cylorun.io.minecraft.SpeedrunEvent;
import me.cylorun.io.minecraft.live.BarterHandler;
import me.cylorun.io.minecraft.logs.LogEventListener;
import me.cylorun.io.minecraft.logs.LogHandler;
import me.cylorun.io.minecraft.player.Inventory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class WorldFile extends File implements WorldEventListener, LogEventListener {
    private CompletionHandler completionHandler;
    public final WorldEventHandler eventHandler;
    public BarterHandler barterHandler;
    public boolean track = true;
    public boolean finished = false;
    public JsonObject liveData;
    public Inventory inv;
    public LogHandler logHandler;

    public WorldFile(String path) {
        super(path);
        this.inv = new Inventory(this);
        this.eventHandler = new WorldEventHandler(this);
        this.barterHandler = new BarterHandler(this);
        this.logHandler = new LogHandler(this);

        this.logHandler.addListener(this);
        this.eventHandler.addListener(this);
        this.liveData = new JsonObject();
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

    public Path getLevelDatPath() {
        return Paths.get(this.getAbsolutePath()).resolve("level.dat");
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
                username = split[split.length - 1].trim();
            }
        }
        return username;
    }


    public void setCompletionHandler(CompletionHandler completionHandler) {
        this.completionHandler = completionHandler;
    }

    public void onCompletion() { // not necessarily on credits, just whenever the run is over
        if (this.completionHandler != null) {
            this.completionHandler.handleCompletion();
        }
    }


    @Override
    public void onSpeedrunEvent(SpeedrunEvent e) {
        if (!this.finished) {
//            System.out.printf("Name: %s, Event: %s\n", this.getName(), e);
            this.barterHandler.onSpeedrunEvent(e);
            if (e.type.equals(SpeedrunEventType.REJOIN_WORLD)) {
                this.track = true;
            }

            if (this.track) {
                if (e.type.equals(SpeedrunEventType.LEAVE_WORLD)) {
                    this.track = false;
                }

                if (e.type.equals(SpeedrunEventType.CREDITS)) {
                    this.finished = true;
                    this.onCompletion();
                }
            }
        }
    }

    @Override
    public void onLogEvent(LogEvent e) {
        if (!this.finished) {
            if (this.track) {
                this.barterHandler.onLogEvent(e);

            }
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
