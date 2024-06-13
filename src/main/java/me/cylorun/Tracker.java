package me.cylorun;

import me.cylorun.gui.TrackerFrame;
import me.cylorun.instance.RecordFile;
import me.cylorun.instance.Run;
import me.cylorun.instance.world.WorldCreationEventHandler;
import me.cylorun.instance.WorldFile;
import me.cylorun.io.TrackerOptions;
import me.cylorun.io.sheets.GoogleSheetsClient;
import me.cylorun.map.ChunkMap;
import me.cylorun.utils.APIUtil;
import me.cylorun.utils.LogReceiver;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


public class Tracker {

    public static final String VERSION = Tracker.class.getPackage().getImplementationVersion() == null ? "DEV" : Tracker.class.getPackage().getImplementationVersion();
    private static final Logger LOGGER = LogManager.getLogger(Tracker.class);

    public static void run() {
        java.util.logging.Logger.getLogger(OkHttpClient.class.getName()).setLevel(java.util.logging.Level.FINE);
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

    public static void handleWorld(WorldFile world) {
        world.setCompletionHandler(() -> {

            RecordFile record = new RecordFile(world.getRecordPath().toFile());
            Run run = new Run(world, record);
            run.gatherAll();

            List<Object> runData = new ArrayList<>(run.values());

            if (run.shouldPush()) {
                try {
                    GoogleSheetsClient.appendRowTop(runData);
                    Tracker.log(Level.INFO, "Run Tracked");
                } catch (IOException | GeneralSecurityException e) {
                    log(Level.ERROR, "Failed to upload run to google sheets\n" + e);
                }
                APIUtil.tryUploadRun(run);
            }

            if (TrackerOptions.getInstance().generate_chunkmap) {
                ChunkMap cm = new ChunkMap(world.getSeed(), 500, kaptainwutax.mcutils.state.Dimension.OVERWORLD, world);
                cm.generate();
                cm.setDimension(kaptainwutax.mcutils.state.Dimension.NETHER);
                cm.generate();
            }
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