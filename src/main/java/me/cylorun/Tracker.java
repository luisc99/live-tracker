package me.cylorun;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.gui.TrackerFrame;
import me.cylorun.io.TrackerOptions;
import me.cylorun.io.minecraft.RecordFile;
import me.cylorun.io.minecraft.Run;
import me.cylorun.io.minecraft.world.WorldCreationEventHandler;
import me.cylorun.io.minecraft.world.WorldFile;
import me.cylorun.io.sheets.GoogleSheetsClient;
import me.cylorun.utils.ExceptionUtil;
import me.cylorun.utils.Logging;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class Tracker {

    public static final String VERSION = "v0.0.1-beta.1";

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatDarculaLaf());
        List<WorldFile> worlds = new ArrayList<>();
        AtomicBoolean shouldTrack = new AtomicBoolean(false);

        TrackerFrame.getInstance().open();
        TrackerOptions.setValidSettingsConsumer((b -> {
            if (b) GoogleSheetsClient.setup();
            shouldTrack.set(b);
        }));
        TrackerOptions.validateSettings();
        Logging.info("Running Live-Tracker-"+VERSION);

        WorldCreationEventHandler worldHandler = new WorldCreationEventHandler(); // only one WorldFile object is created per world path
        worldHandler.addListener(world -> {
            if(!shouldTrack.get()) {
                Logging.warn("Will not track "+world+" due to invalid settings");
                return;
            }
            Logging.debug("New world detected: " + world);
            if (!worlds.contains(world)) {
                worlds.add(world);
            }

            if (worlds.size() > 1) {
                WorldFile prev = worlds.get(worlds.size() - 2);
                worlds.remove(prev);
                prev.onCompletion(); // since a new world has been created this one can be abandoned
            }

            handleWorld(world);
        });
    }

    public static void handleWorld(WorldFile world) {
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