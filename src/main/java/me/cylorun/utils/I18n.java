package me.cylorun.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.cylorun.Tracker;
import me.cylorun.io.TrackerOptions;

import java.net.URL;

public class I18n {

    private static JsonObject langJson;

    public static JsonObject getAllDeaths() {
        if (langJson == null) {
            langJson = getLangJson();
        }
        return langJson.get("deaths").getAsJsonObject();
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
