package me.cylorun;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.gui.TrackerFrame;
import me.cylorun.instance.RecordFile;
import me.cylorun.instance.Run;
import me.cylorun.instance.world.WorldCreationEventHandler;
import me.cylorun.instance.world.WorldFile;
import me.cylorun.io.TrackerOptions;
import me.cylorun.io.sheets.GoogleSheetsClient;
import me.cylorun.utils.ExceptionUtil;
import me.cylorun.utils.LogReceiver;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


public class Tracker {

    public static final String VERSION = Tracker.class.getPackage().getImplementationVersion() == null ? "DEV" : Tracker.class.getPackage().getImplementationVersion();
    private static final Logger logger = LogManager.getLogger(Tracker.class);
    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatDarculaLaf());

        List<WorldFile> worlds = new ArrayList<>();
        TrackerFrame.getInstance().open();
        GoogleSheetsClient.setup();
        Tracker.log(Level.INFO,"Running Live-Tracker-"+VERSION);

        WorldCreationEventHandler worldHandler = new WorldCreationEventHandler(); // only one WorldFile object is created per world path
        worldHandler.addListener(world -> {
            Tracker.log(Level.DEBUG, "New world detected: " + world);
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
                    Tracker.log(Level.INFO,"Run Tracked");
                } catch (IOException | GeneralSecurityException ex) {
                    ExceptionUtil.showError(ex);
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public static void log(Level level, Object o) {
        logger.log(level, o);
        LogReceiver.log(level, o);
    }

}