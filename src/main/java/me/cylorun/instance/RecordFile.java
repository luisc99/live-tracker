package me.cylorun.instance;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.Tracker;
import org.apache.logging.log4j.Level;

import javax.sound.midi.Track;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LoggingPermission;

public class RecordFile {
    private JsonObject jsonObject;

    public RecordFile(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }
    public RecordFile(File file) {
        FileReader reader;

        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException ex) {
            Tracker.log(Level.ERROR ,"Record file non existent\n"+ file.getAbsolutePath());
            throw new RuntimeException();
        }
        System.out.println(file.getAbsolutePath());
        this.jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
    }

    public JsonObject getJson() {
        return jsonObject;
    }
}
