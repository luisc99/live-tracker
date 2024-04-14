package me.cylorun.io.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.utils.ExceptionUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WorldFile extends File {
    public final String path;

    public WorldFile(String path) {
        super(path);
        this.path = path;
    }

    public Path getRecordPath() {
        return Paths.get(this.path, "speedrunigt", "record.json");
    }

    public void onCompletion() {
        FileReader reader = null;
        try {
            reader = new FileReader(this.getRecordPath().toFile());
        } catch (FileNotFoundException e) {
            ExceptionUtil.showError(e);
            throw new RuntimeException(e);
        }
        JsonObject o = JsonParser.parseReader(reader).getAsJsonObject();
        RecordFile record = new RecordFile(o);
    }
}
