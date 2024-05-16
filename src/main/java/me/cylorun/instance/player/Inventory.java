package me.cylorun.instance.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.Tracker;
import me.cylorun.instance.NBTReader;
import me.cylorun.instance.world.WorldFile;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;

public class Inventory extends ArrayList<InventoryItem> {

    private final WorldFile file;

    public Inventory(WorldFile file) {
        this.file = file;
    }

    public Inventory read() {
        NBTReader reader = new NBTReader(this.file.getLevelDatPath());
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
            InventoryItem invItem = new InventoryItem(name, count);

            this.add(invItem);
        }


        return this;
    }

}
