package me.cylorun;

import me.cylorun.gui.TrackerFrame;
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

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Tracker {

    public static final String VERSION = Tracker.class.getPackage().getImplementationVersion() == null ? "DEV" : Tracker.class.getPackage().getImplementationVersion();
    private static final Logger LOGGER = LogManager.getLogger(Tracker.class);

    public static void run() {
        Tracker.log(Level.INFO, "Running Live-Tracker-" + VERSION);

        java.util.logging.Logger.getLogger(OkHttpClient.class.getName()).setLevel(java.util.logging.Level.FINE);
        List<WorldFile> worlds = new ArrayList<>();
        TrackerFrame.getInstance().open();
//        GoogleSheetsClient.setup();

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
        TrackerOptions options = TrackerOptions.getInstance();
        world.setCompletionHandler(() -> {
            Run run = new Run(world);
            run.gatherAll();

            if (run.shouldPush()) {
                if (options.always_save_locally) {
                    run.save(TrackerOptions.getTrackerDir().resolve("local"));
                }

                APIUtil.tryUploadRun(run);
            }

            if (options.generate_chunkmap) {
                new Thread(()->{
                    ChunkMap cm = new ChunkMap(world.getSeed(), 500, kaptainwutax.mcutils.state.Dimension.OVERWORLD, world);
                    cm.generate();
                    cm.setDimension(kaptainwutax.mcutils.state.Dimension.NETHER).generate();
                }, "ChunkMapGen").start();
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