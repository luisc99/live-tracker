package com.cylorun.instance.world;

import com.cylorun.Tracker;
import com.cylorun.instance.WorldFile;
import com.cylorun.utils.ExceptionUtil;
import com.cylorun.utils.JSONUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WorldCreationEventHandler extends Thread {
    public List<String> previousWorlds;
    private String lastPath = "";
    private final List<Consumer<WorldFile>> listeners;

    private final File LAST_WORlD_JSON = Paths.get(System.getProperty("user.home")).resolve("speedrunigt").resolve("latest_world.json").toFile();

    public WorldCreationEventHandler() {
        super("WorldCreationEventHandler");
        this.previousWorlds = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.start();
    }


    public void addListener(Consumer<WorldFile> listener) {
        listeners.add(listener);
    }

    private Optional<String> getLastWorldPath() {
        FileReader reader = null;
        try {
            reader = new FileReader(LAST_WORlD_JSON);
        } catch (FileNotFoundException e) {
            ExceptionUtil.showDialogAndExit("LAST WORLD JSON FILE NOT FOUND, MAKE SURE UR USING SPEEDRUN IGT 14.2+ (i think that's the version)");
            return Optional.of(this.lastPath);
        }

        JsonElement element = JsonParser.parseReader(reader);
        if (element == null || !element.isJsonObject()) {
            return Optional.empty();
        }

        return JSONUtil.getOptionalString(element.getAsJsonObject(), "world_path");
    }

    public void notifyListeners(WorldFile world) {
        for (Consumer<WorldFile> listener : listeners) {
            listener.accept(world);
        }
    }

    @Override
    public void run() {
        this.lastPath = this.getLastWorldPath().orElse(""); // so that it wont detect old worlds

        while (true) {
            if (this.getLastWorldPath().isEmpty()) {
                continue;
            }

            String newPath = this.getLastWorldPath().get();
            if (!Objects.equals(newPath, this.lastPath)) { // makes sure that a world wont be tracked multiple times
                this.lastPath = newPath;

                JsonObject recordJson = JSONUtil.parseFile(Paths.get(newPath).resolve("speedrunigt").resolve("record.json").toFile(), true);
                // that file will usually not exist unless the world has already been loaded before
                if (recordJson != null && recordJson.get("category").getAsString().equals("pratice_world")) { // yes i spelled it right
                    Tracker.log(Level.INFO, String.format("Practice world %s detected, will not track", new File(newPath).getName()));
                    continue;
                }

                this.notifyListeners(new WorldFile(this.lastPath));
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
