package me.cylorun.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.Tracker;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ResourceUtil {


    public static List<Object> getHeaderLabels() {
        List<Object> list = new ArrayList<>();
        URL url = Tracker.class.getClassLoader().getResource("events/tracked.json");
        if (url == null) {
            ExceptionUtil.showError(new IOException("Resource not found: events/tracked.json"));
            throw new RuntimeException("Resource not found: events/tracked.json");
        }

        String trackedEventsData = loadFile(url);

        String[] keys = {"GENERAL_HEADERS", "TRACKED_BARTERS", "TRACKED_FOODS", "TRACKED_MOBS", "TRAVEL_METHODS"};
        JsonObject jsonData = JsonParser.parseString(trackedEventsData).getAsJsonObject();

        try {
            for (String k : keys) {
                JsonArray a = (JsonArray) jsonData.get(k);
                for (JsonElement o : a) {
                    String insertStr;

                    if (k.equals("TRACKED_BARTERS") || k.equals("TRACKED_FOODS") || k.equals("TRACKED_MOBS")) {
                        insertStr = o.getAsString().split(":")[1];
                    } else if(k.equals("TRAVEL_METHODS")) {
                        insertStr = o.getAsString().split(":")[1].replace("_one_cm","");
                    } else {
                        insertStr = o.getAsString();
                    }

                    list.add(insertStr);
                }
            }
            list.add("Seed");
        } catch (Exception e) {
            ExceptionUtil.showError(e);
            throw new RuntimeException();
        }
        return list;
    }


    private static String loadFile(URL url) {
        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream inputStream = url.openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }
}

