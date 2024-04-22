package me.cylorun;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.io.minecraft.RecordFile;
import me.cylorun.io.minecraft.Run;
import me.cylorun.io.minecraft.world.WorldCreationEventHandler;
import me.cylorun.io.minecraft.world.WorldFile;
import me.cylorun.io.sheets.GoogleSheetsClient;
import me.cylorun.utils.Assert;
import me.cylorun.utils.ExceptionUtil;
import me.cylorun.utils.Logging;
import me.cylorun.utils.ResourceUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


public class Tracker {

    public static final String VERSION = "0.0.0";

    public static void main(String[] args) {
        GoogleSheetsClient.setup();
        Logging.info("Tracking");
        List<WorldFile> worlds = new ArrayList<>();
        WorldCreationEventHandler worldHandler = new WorldCreationEventHandler(); // only one WorldFile object is created per world path
        worldHandler.addListener(world -> {
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

    public static List<String> getFinalBarters(Run run, WorldFile world) {
        List<String> res = new ArrayList<>();
        URL url = Tracker.class.getClassLoader().getResource("events/tracked.json");
        Assert.isNotNull(url, "Resource not found: events/tracked.json");

        JsonObject o = ResourceUtil.loadJsonResource(url);
        JsonArray barters = o.get("TRACKED_BARTERS").getAsJsonArray();
        JsonObject pickedUp;
        if (run.stats.has("minecraft:picked_up")) {
            pickedUp = run.stats.get("minecraft:picked_up").getAsJsonObject();
        } else {
            for (JsonElement e : barters) {
                res.add("0");
            }
            return res;
        }

        for (JsonElement barterItem : barters) {
            if (pickedUp.has(barterItem.getAsString())) {
                int diff = 0;
                if (world.hungerResetHandler.itemDiffs.containsKey(barterItem.getAsString())) {
                    diff = world.hungerResetHandler.itemDiffs.get(barterItem.getAsString());
                }
                res.add(String.valueOf(pickedUp.get(barterItem.getAsString()).getAsInt() - diff));
            } else {
                res.add("0");
            }
        }

        return res;
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
            for (int i = 0; i < 13; i++) {
                runData.add("Nil");
            }
            runData.addAll(getFinalBarters(thisRun, world));
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