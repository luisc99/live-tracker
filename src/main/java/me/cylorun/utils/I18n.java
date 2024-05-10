package me.cylorun.utils;

import com.google.gson.JsonObject;
import me.cylorun.Tracker;
import me.cylorun.io.TrackerOptions;

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
        Assert.isNotNull(url, "Resource not found: translations/" + lang + ".json");

        return ResourceUtil.loadJsonResource(url);
    }
}
