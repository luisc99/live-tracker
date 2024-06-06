package me.cylorun.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.Tracker;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

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

    public static JsonObject flatten(JsonObject jsonObject) { // collapses all children objects
        JsonObject flatJsonObject = new JsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (entry.getValue().isJsonObject()) {
                JsonObject nestedObject = entry.getValue().getAsJsonObject();
                for (Map.Entry<String, JsonElement> nestedEntry : nestedObject.entrySet()) {
                    flatJsonObject.add(nestedEntry.getKey(), nestedEntry.getValue());
                }
            } else {
                flatJsonObject.add(entry.getKey(), entry.getValue());
            }
        }

        return flatJsonObject;
    }


}
