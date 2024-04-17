package me.cylorun.io.minecraft.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldCreationEventHandler extends Thread {
    private final int DELAY_MS = 5000;
    private List<String> previousWorlds;
    private String lastPath = "";

    public WorldCreationEventHandler() {
        this.previousWorlds = new ArrayList<>();
        this.start();
    }

    private List<WorldCreationListener> listeners = new ArrayList<>();

    public void addListener(WorldCreationListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(WorldFile world) {
        for (WorldCreationListener listener : listeners) {
            listener.onNewWorld(world);
        }
    }

    @Override
    public void run() {
        File lwjson = Paths.get(System.getProperty("user.home")).resolve("speedrunigt").resolve("latest_world.json").toFile();

        while (true) {
            FileReader reader = null;
            try {
                reader = new FileReader(lwjson);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            JsonElement element = JsonParser.parseReader(reader);
            String newPath = element.getAsJsonObject().get("world_path").getAsString();
            if (!Objects.equals(newPath, this.lastPath) && !this.previousWorlds.contains(newPath)) { // makes sure that a world wont be tracked multiple times
                this.previousWorlds.add(newPath);
                this.lastPath = newPath;
                this.notifyListeners(new WorldFile(this.lastPath));
            }

            try {
                Thread.sleep(this.DELAY_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
