package me.cylorun.instance.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.instance.NBTReader;
import me.cylorun.instance.world.WorldFile;

import java.util.ArrayList;

public class Inventory extends ArrayList<InventoryItem> {

    private final WorldFile file;

    public Inventory(WorldFile file) {
        this.file = file;
    }

    public Inventory read() {
        NBTReader reader = new NBTReader(this.file.getLevelDatPath());
        this.clear();

        JsonArray inventory = JsonParser.parseString(reader.get(NBTReader.INVENTORY_PATH)).getAsJsonArray();

        for (JsonElement e : inventory) {
            JsonObject item = e.getAsJsonObject();
            String name = item.get("id").getAsJsonObject().get("value").getAsString();
            int count = item.get("Count").getAsJsonObject().get("value").getAsInt();
            InventoryItem invItem = new InventoryItem(name, count);

            this.add(invItem);
        }


        return this;
    }

}
