package me.cylorun.io.minecraft.live;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.cylorun.Tracker;
import me.cylorun.enums.LogEventType;
import me.cylorun.io.minecraft.LogEvent;
import me.cylorun.io.minecraft.SpeedrunEvent;
import me.cylorun.io.minecraft.player.InventoryItem;
import me.cylorun.io.minecraft.world.WorldFile;
import me.cylorun.utils.ResourceUtil;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BarterHandler {
    private WorldFile world;
    private Map<String, Integer> tradesDiff;
    private JsonObject barterData;

    public BarterHandler(WorldFile world) {
        this.world = world;
        this.tradesDiff = new HashMap<>(); // diff in trades
        this.barterData = new JsonObject();

    }

    public JsonObject getFinalData() {
        return this.barterData;
    }

    public void onLogEvent(LogEvent e) {
        if (e.type.equals(LogEventType.RESPAWN_SET)) {
            this.world.inv.read();
            this.updateTrades();
        }

        if (e.type.equals(LogEventType.HUNGER_RESET)) {
            JsonObject prevTrades = this.barterData.deepCopy();
            this.world.inv.read();
            this.updateTrades();

        }

    }

    public void onSpeedrunEvent(SpeedrunEvent e) {

    }

    private void updateTrades() {
        JsonObject trades = this.barterData.deepCopy();
        URL url = Tracker.class.getResource("events/tracked.json");
        JsonArray trackedTrades = ResourceUtil.loadJsonResource(url).get("TRACKED_BARTERS").getAsJsonArray();

        for (InventoryItem item : this.world.inv) {
            for (JsonElement e : trackedTrades) {
                if (e.getAsString().equals(item.name)) {
                    trades.addProperty(item.name, item.count);
                }
            }
        }
    }
}
