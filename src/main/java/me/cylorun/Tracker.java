package me.cylorun;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.gui.TrackerFrame;
import me.cylorun.instance.RecordFile;
import me.cylorun.instance.Run;
import me.cylorun.instance.world.WorldCreationEventHandler;
import me.cylorun.instance.world.WorldFile;
import me.cylorun.io.sheets.GoogleSheetsClient;
import me.cylorun.map.ChunkMap;
import me.cylorun.utils.ExceptionUtil;
import me.cylorun.utils.LogReceiver;
import me.cylorun.utils.UpdateUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Tracker {

    public static final String VERSION = Tracker.class.getPackage().getImplementationVersion() == null ? "DEV" : Tracker.class.getPackage().getImplementationVersion();
    private static final Logger LOGGER = LogManager.getLogger(Tracker.class);
    public static boolean hasFirstRun = false;
    public static String[] args;

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatDarculaLaf());
        FlatDarculaLaf.setup();
        ToolTipManager.sharedInstance().setInitialDelay(0);

        Tracker.args = args;
        UpdateUtil.checkForUpdates(VERSION);
        checkDeleteOldjar();
        Tracker.run();
    }

    private static void checkDeleteOldjar() {
        List<String> argList = Arrays.asList(args);
        if (!argList.contains("-deleteOldJar")) {
            return;
        }

        File toDelete = new File(argList.get(argList.indexOf("-deleteOldJar") + 1));

        log(Level.INFO, "Deleting old jar " + toDelete.getName());

        for (int i = 0; i < 200 && !toDelete.delete(); i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (toDelete.exists()) {
            log(Level.ERROR, "Failed to delete " + toDelete.getName());
        } else {
            log(Level.INFO, "Deleted " + toDelete.getName());
        }

    }

    public static void handleWorld(WorldFile world) {
        world.setCompletionHandler(() -> {

            RecordFile record = new RecordFile(world.getRecordPath().toFile());
            Run run = new Run(world, record);
            List<Object> runData = run.gatherAll();

            if (run.shouldPush()) {
                hasFirstRun = true;
                try {
                    GoogleSheetsClient.appendRowTop(runData);
                    Tracker.log(Level.INFO, "Run Tracked");
                } catch (IOException | GeneralSecurityException e) {
                    log(Level.ERROR, "Failed to upload run to google sheets\n"+e);
                }
            }
            ChunkMap cm = new ChunkMap(world.getSeed(), new Dimension(100,100), kaptainwutax.mcutils.state.Dimension.OVERWORLD, world);
            cm.generate();
            cm.setDimension(kaptainwutax.mcutils.state.Dimension.NETHER);
            cm.generate();
        });
    }

    private static void run() {

        List<WorldFile> worlds = new ArrayList<>();
        TrackerFrame.getInstance().open();

        GoogleSheetsClient.setup();
        Tracker.log(Level.INFO, "Running Live-Tracker-" + VERSION);

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

    public static Path getSourcePath() {
        try {
            return Paths.get(Tracker.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void log(Level level, Object o) {
        LOGGER.log(level, o);
        LogReceiver.log(level, o);
    }
}