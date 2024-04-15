package me.cylorun.io.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.event.SpeedrunEvent;
import me.cylorun.event.WorldEventHandler;
import me.cylorun.event.SpeedrunEventType;
import me.cylorun.event.callbacks.WorldEventListener;
import me.cylorun.utils.ExceptionUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WorldFile extends File implements WorldEventListener {
    private final WorldEventHandler eventHandler;
    public boolean isActiveWorld = true;

    public WorldFile(String path) {
        super(path);
        this.eventHandler = new WorldEventHandler(this);
        this.eventHandler.addListener(this);
    }

    public Path getRecordPath() {
        return Paths.get(this.getAbsolutePath()).resolve("speedrunigt").resolve("record.json");
    }

    public Path getEventLog() {
        return Path.of(this.getAbsolutePath()).resolve("speedrunigt").resolve("events.log");
    }


    public void onCompletion() {
        FileReader reader = null;
        try {
            reader = new FileReader(this.getRecordPath().toFile());
        } catch (FileNotFoundException e) {
            ExceptionUtil.showError(e);
            throw new RuntimeException(e);
        }
        JsonObject o = JsonParser.parseReader(reader).getAsJsonObject();
        RecordFile record = new RecordFile(o);
    }

    @Override
    public void onNewEvent(SpeedrunEvent e) {
        if (e.type.equals(SpeedrunEventType.REJOIN_WORLD)) {
            this.isActiveWorld = true;
        }

        if (this.isActiveWorld) {
            if (e.type.equals(SpeedrunEventType.LEAVE_WORLD)) {
                this.isActiveWorld = false;
                System.out.printf("%s has been left\n", this.getName());
            }
            System.out.printf("Name: %s, Event: %s\n",this.getName(), e);
        }
    }
}
