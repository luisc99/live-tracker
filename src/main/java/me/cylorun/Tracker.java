package me.cylorun;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.io.minecraft.RecordFile;
import me.cylorun.io.minecraft.Run;
import me.cylorun.io.minecraft.logs.LogHandler;
import me.cylorun.io.minecraft.world.WorldCreationEventHandler;
import me.cylorun.io.minecraft.world.WorldFile;
import me.cylorun.io.sheets.GoogleSheetsClient;
import me.cylorun.utils.ExceptionUtil;
import me.cylorun.utils.Logging;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


public class Tracker {
    public static void main(String[] args) {
        GoogleSheetsClient.setup();
        Logging.info("Tracking");
        List<WorldFile> worlds = new ArrayList<>();
        WorldCreationEventHandler worldHandler = new WorldCreationEventHandler(); // only one WorldFile object is created per world path
        worldHandler.addListener(world -> {
            Logging.debug("New world detected: "+world);
            if (!worlds.contains(world)) {
                worlds.add(world);
            }

            if (worlds.size() > 1) {
                WorldFile prev = worlds.get(worlds.size() - 2);
                prev.onCompletion(); // since a new world has been created this one can be abandoned
            }

            handleWorld(world);
        });

    }

    public static void handleWorld(WorldFile world) {
        LogHandler logH = new LogHandler(world);
        world.setCompletionHandler(() -> {
            world.finished = true;
            FileReader reader;

            try {
                reader = new FileReader(world.getRecordPath().toFile());
            } catch (FileNotFoundException ex) {
                ExceptionUtil.showError(ex);
                throw new RuntimeException(ex);
            }

            JsonObject o = JsonParser.parseReader(reader).getAsJsonObject();
            RecordFile record = new RecordFile(o);
            Run thisRun = new Run(world, record);

            List<Object> runData = thisRun.gatherAll();

            if (thisRun.shouldPush()) {
                try {
                    GoogleSheetsClient.appendRowTop(runData);
                    Logging.info("Run Tracked");
                } catch (IOException | GeneralSecurityException ex) {
                    ExceptionUtil.showError(ex);
                    throw new RuntimeException(ex);
                }
            }
        });
    }


}