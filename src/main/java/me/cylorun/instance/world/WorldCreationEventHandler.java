package me.cylorun.instance.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.cylorun.instance.RecordFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldCreationEventHandler extends Thread {
    public List<String> previousWorlds;
    private String lastPath = "";
    private File lwjson = Paths.get(System.getProperty("user.home")).resolve("speedrunigt").resolve("latest_world.json").toFile();

    public WorldCreationEventHandler() {
        this.previousWorlds = new ArrayList<>();
        this.start();
    }

    private List<WorldCreationListener> listeners = new ArrayList<>();

    public void addListener(WorldCreationListener listener) {
        listeners.add(listener);
    }

    private String getLastWorldPath() {
        FileReader reader = null;
        try {
            reader = new FileReader(lwjson);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        JsonElement element = JsonParser.parseReader(reader);
        return element.getAsJsonObject().get("world_path").getAsString();
    }

    public void notifyListeners(WorldFile world) {
        for (WorldCreationListener listener : listeners) {
            listener.onNewWorld(world);
        }
    }

    @Override
    public void run() {
        this.lastPath = getLastWorldPath(); // so that it wont detect old worlds

        while (true) {
            String newPath = getLastWorldPath();
            if (!Objects.equals(newPath, this.lastPath) && !this.previousWorlds.contains(newPath)) { // makes sure that a world wont be tracked multiple times
                this.lastPath = newPath;

                RecordFile rf = new RecordFile(Paths.get(newPath).resolve("speedrunigt").resolve("record.json").toFile());
                if (rf.getJson().get("category").getAsString().equals("pratice_world")) { // yes i spelled it right
                    continue;
                }

                this.previousWorlds.add(newPath);
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
