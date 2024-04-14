package me.cylorun.io.minecraft;

import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

public class RecordFile {
    private JsonObject jsonObject;

    public RecordFile() {
        this.jsonObject = new JsonObject();
    }

    public RecordFile(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    // You can add other methods to manipulate the JSON object as needed
}
