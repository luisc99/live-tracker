package me.cylorun.io.minecraft.world;

import me.cylorun.enums.SpeedrunEventType;
import me.cylorun.io.minecraft.SpeedrunEvent;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class WorldFile extends File implements WorldEventListener {
    private final WorldEventHandler eventHandler;
    private CompletionHandler completionHandler;
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


    public void setCompletionHandler(CompletionHandler completionHandler){
        this.completionHandler = completionHandler;
    }

    private void onCompletion(SpeedrunEvent e){
        if (this.completionHandler !=null){
            this.completionHandler.handleCompletion(e);
        }
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
