package me.cylorun.io;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.cylorun.io.callbacks.WorldCreationListener;
import me.cylorun.minecraft.WorldFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldCreationEventHandler extends Thread {
    private String lastPath = "";
    Gson gson = new Gson();

    public WorldCreationEventHandler() {
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
        File lwjson = Paths.get(System.getProperty("user.home"), "speedrunigt", "latest_world.json").toFile();

        while (true) {
            FileReader reader = null;
            try {
                reader = new FileReader(lwjson);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            JsonElement element = JsonParser.parseReader(reader);
            String currPath = element.getAsJsonObject().get("world_path").toString();
            if (!Objects.equals(currPath, this.lastPath)) {
                this.lastPath = currPath;
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
