package me.cylorun.io.minecraft.live;

import me.cylorun.io.TrackerOptions;
import me.cylorun.io.minecraft.LogEvent;
import me.cylorun.io.minecraft.SpeedrunEvent;
import me.cylorun.io.minecraft.logs.LogEventListener;
import me.cylorun.io.minecraft.player.InventoryItem;
import me.cylorun.io.minecraft.world.WorldEventListener;
import me.cylorun.io.minecraft.world.WorldFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HungerResetHandler implements WorldEventListener, LogEventListener {
    public Map<String, Integer> itemDiffs;
    public int respawnPointsSet;
    private WorldFile world;
    private List<InventoryItem> tmpInv;
    private long lastRespawnSet = 0;

    public HungerResetHandler(WorldFile world) {
        this.world = world;
        this.respawnPointsSet = 0;

        this.tmpInv = new ArrayList<>();
        this.itemDiffs = new HashMap<>(); //ammount of fake / duped items
        this.world.eventHandler.addListener(this);
        this.world.logHandler.addListener(this);
    }


    private void updateDiff() {
        System.out.println(this.tmpInv);
        for (InventoryItem item : this.tmpInv) {
            int prev = 0;
            if (this.itemDiffs.containsKey(item.name)) {
                prev = this.itemDiffs.get(item.name);
            }

            this.itemDiffs.put(item.name, prev + item.count);
        }

        for (Map.Entry<String, Integer> entry : this.itemDiffs.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();
    }

    @Override
    public void onLogEvent(LogEvent e) {
        if (!this.world.finished && this.world.track) {
            System.out.println(e.type);
            if (System.currentTimeMillis() - this.lastRespawnSet > (TrackerOptions.getInstance().max_respawn_to_hr_time * 1000L)) {
                tmpInv.clear();
            }

            switch (e.type) {
                case HUNGER_RESET -> this.updateDiff();

                case RESPAWN_SET -> {
                    this.respawnPointsSet++;
                    this.lastRespawnSet = System.currentTimeMillis();
                    this.world.inv.read();
                    try {
                        Thread.sleep(TrackerOptions.getInstance().game_save_interval * 1000L);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
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
