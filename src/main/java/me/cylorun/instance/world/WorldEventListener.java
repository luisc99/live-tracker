package me.cylorun.instance.world;

import me.cylorun.instance.SpeedrunEvent;

public interface WorldEventListener {
    void onSpeedrunEvent(SpeedrunEvent e);
}
