package me.cylorun.io.minecraft;

import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class RecordFile {
    private JsonObject jsonObject;

    public RecordFile(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }
    public List<Object> getGeneralData() {  // timestamps, date , etc. GENERAL_HEADERS in tracked.json
        List<Object> list = new ArrayList<>();



        return list;
    }

    public JsonObject getJson() {
        return jsonObject;
    }
}
