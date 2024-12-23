package com.cylorun.utils;

import com.cylorun.Tracker;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ResourceUtil {


    public static List<Object> getHeaderLabels() {
        List<Object> list = new ArrayList<>();
        URL url = ResourceUtil.class.getClassLoader().getResource("tracked.json");
        if (url == null) {
            Tracker.log(Level.ERROR,"Resource not found: tracked.json");
            throw new RuntimeException("Resource not found: tracked.json");
        }


        String[] keys = {"GENERAL_HEADERS", "TRACKED_BARTERS", "TRACKED_FOODS", "TRACKED_MOBS", "TRAVEL_METHODS"};
        JsonObject jsonData = loadJsonResource(url);

        for (String k : keys) {
            JsonArray a = (JsonArray) jsonData.get(k);
            for (JsonElement o : a) {
                String insertStr = o.getAsString();

                if (k.equals("TRACKED_BARTERS") || k.equals("TRACKED_FOODS") || k.equals("TRACKED_MOBS")) {
                    insertStr = o.getAsString().split(":")[1];
                } else if (k.equals("TRAVEL_METHODS")) {
                    insertStr = o.getAsString().split(":")[1].replace("_one_cm", "");
                }

                list.add(insertStr);
            }
        }

        list.add("seed");
        return list;
    }

    public static BufferedImage loadImageResource(String path) throws IOException {
        ClassLoader classLoader = ResourceUtil.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            if (inputStream == null) {
                Tracker.log(Level.ERROR, "Resource not found: " + path);
                throw new IOException("Resource not found: " + path);
            }
            return ImageIO.read(inputStream);
        }
    }


    public static JsonObject loadJsonResource(URL url) {
        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream inputStream = url.openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return JsonParser.parseString(stringBuilder.toString()).getAsJsonObject();
    }
}

