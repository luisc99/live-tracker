package com.cylorun.utils;

import com.cylorun.Tracker;
import com.google.gson.JsonObject;
import com.cylorun.io.TrackerOptions;
import org.apache.logging.log4j.Level;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class I18n {

    private static JsonObject langJson;

    public static JsonObject getALlDeathMessages() {
        if (langJson == null) {
            langJson = getLangJson();
        }
        return langJson.get("deaths").getAsJsonObject();
    }
    public static List<String> getSupported() {
        List<String> allowedLangs = new ArrayList<>();
        allowedLangs.add("en_us");
        return allowedLangs;
    }

    public static boolean isValidLanguage(String lang) {
        return getSupported().contains(lang.trim());
    }

    public static String get(String key) {
        if (langJson == null) {
            langJson = getLangJson();
        }
        return langJson.get(key).getAsString();
    }

    private static JsonObject getLangJson() {
        String lang = TrackerOptions.getInstance().lang;
        URL url = Tracker.class.getClassLoader().getResource("translations/" + lang + ".json");
        if (url == null) {
            Tracker.log(Level.ERROR,  "Resource not found: translations/" + lang + ".json");
            return new JsonObject();
        }

        return ResourceUtil.loadJsonResource(url);
    }
}
