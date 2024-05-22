package me.cylorun.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.Tracker;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class JSONUtil {
    public static JsonObject parseFile(File file) {
        FileReader reader;

        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            Tracker.log(Level.ERROR, "Json file non existent: " + file.getAbsolutePath());
            return null;
        }

        return JsonParser.parseReader(reader).getAsJsonObject();
    }

}
