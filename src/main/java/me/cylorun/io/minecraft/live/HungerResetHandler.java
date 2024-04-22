package me.cylorun.io.minecraft.live;

import me.cylorun.io.minecraft.LogEvent;
import me.cylorun.io.minecraft.SpeedrunEvent;
import me.cylorun.io.minecraft.logs.LogEventListener;
import me.cylorun.io.minecraft.player.InventoryItem;
import me.cylorun.io.minecraft.world.WorldEventListener;
import me.cylorun.io.minecraft.world.WorldFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HungerResetHandler implements WorldEventListener, LogEventListener {
    private Map<String, Integer> itemDiffs;
    private WorldFile world;
    private List<InventoryItem> tmpInv;

    public HungerResetHandler(WorldFile world) {
        this.world = world;

        this.itemDiffs = new HashMap<>(); //ammount of fake / duped items
        this.world.eventHandler.addListener(this);
        this.world.logHandler.addListener(this);
    }


    private void updateDiff() {
        for (InventoryItem item : this.tmpInv) {
            int prev = 0;
            if (this.itemDiffs.containsKey(item.name)) {
                prev = this.itemDiffs.get(item.name);
            }

            this.itemDiffs.put(item.name, prev + item.count);
        }
    }

    @Override
    public void onLogEvent(LogEvent e) {
        if (!this.world.finished && this.world.track) {
            switch (e.type) {
                case HUNGER_RESET -> {
                    this.updateDiff();
                }

                case RESPAWN_SET -> {
                    tmpInv.clear();
                    this.tmpInv.addAll(this.world.inv);
                }

            }
        }
    }

    @Override
    public void onSpeedrunEvent(SpeedrunEvent e) {

    }
}
