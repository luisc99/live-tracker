package me.cylorun.io.minecraft.world;

import me.cylorun.io.minecraft.SpeedrunEvent;

public interface WorldEventListener {
    void onNewEvent(SpeedrunEvent e);
}
