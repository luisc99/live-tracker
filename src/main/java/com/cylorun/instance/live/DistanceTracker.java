package com.cylorun.instance.live;

import com.cylorun.instance.SpeedrunEvent;
import com.cylorun.instance.WorldFile;
import com.cylorun.instance.world.WorldEventListener;
import com.cylorun.io.TrackerOptions;
import com.cylorun.utils.Vec2i;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class DistanceTracker implements WorldEventListener {
    private final SpeedrunEvent.SpeedrunEventType startEvent;
    private final SpeedrunEvent.SpeedrunEventType endEvent;
    private final WorldFile world;
    public Vec2i startPoint;
    public Vec2i endPoint;

    public DistanceTracker(WorldFile world, SpeedrunEvent.SpeedrunEventType startEvent, SpeedrunEvent.SpeedrunEventType endEvent) {
        this.endEvent = endEvent;
        this.startEvent = startEvent;
        this.world = world;

        this.world.eventHandler.addListener(this);
    }

    public String getFinalData() {
        if (this.startPoint == null || this.endPoint == null) {
            return "";
        }

        return String.valueOf(this.startPoint.distanceTo(this.endPoint));
    }


    @Override
    public void onSpeedrunEvent(SpeedrunEvent e) {
        if (e.type.equals(this.endEvent)) {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                this.endPoint = this.world.getPlayerLocation();
            }, TrackerOptions.getInstance().game_save_interval + 2, TimeUnit.SECONDS);
        }

        if (e.type.equals(this.startEvent)) {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                this.startPoint = this.world.getPlayerLocation();
            }, TrackerOptions.getInstance().game_save_interval + 2, TimeUnit.SECONDS);
        }
    }
}
