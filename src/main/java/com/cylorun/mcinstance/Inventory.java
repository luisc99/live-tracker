package com.cylorun.mcinstance;

import com.cylorun.Tracker;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;

public class Inventory extends ArrayList<Inventory.Item> {

    private final WorldFile file;

    public Inventory(WorldFile file) {
        this.file = file;
    }

    public Inventory read() {
        NBTReader reader = NBTReader.from(this.file);
        String invString = reader.get(NBTReader.INVENTORY_PATH);
        if (invString == null) {
            Tracker.log(Level.WARN,"Failed to retrieve inventory data");
            return this;
        }

        JsonArray inventory = JsonParser.parseString(invString).getAsJsonArray();
        this.clear();

        for (JsonElement e : inventory) {
            JsonObject item = e.getAsJsonObject();
            String name = item.get("id").getAsJsonObject().get("value").getAsString();
            int count = item.get("Count").getAsJsonObject().get("value").getAsInt();
            Inventory.Item invItem = new Inventory.Item(name, count);

            this.add(invItem);
        }


        return this;
    }

    public static class Item {

        public String name;
        public int count;
        public Item(String name, int count) {
            this.name = name;
            this.count = count;
        }

        @Override
        public String toString() {
            return String.format("{\"name\": %s, \"count\": %s}", this.name, this.count);
        }
    }

}
