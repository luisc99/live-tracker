package me.cylorun.instance.live;

import me.cylorun.enums.SpeedrunEventType;
import me.cylorun.io.TrackerOptions;
import me.cylorun.instance.NBTReader;
import me.cylorun.instance.SpeedrunEvent;
import me.cylorun.instance.world.WorldEventListener;
import me.cylorun.instance.world.WorldFile;
import me.cylorun.utils.Vec2i;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class DistanceTracker implements WorldEventListener {
    private final NBTReader reader;
    private final SpeedrunEventType startEvent;
    private final SpeedrunEventType endEvent;
    private final WorldFile file;
    public Vec2i startPoint;
    public Vec2i endPoint;

    public DistanceTracker(WorldFile file, SpeedrunEventType startEvent, SpeedrunEventType endEvent) {
        this.endEvent = endEvent;
        this.startEvent = startEvent;
        this.file = file;

        this.file.eventHandler.addListener(this);
        this.reader = new NBTReader(file.getLevelDatPath());
    }

    public String getFinalData() {
        if (this.startPoint == null || this.endPoint == null) {
            return "";
        }

        return String.valueOf(this.startPoint.distanceTo(this.endPoint));
    }


    @Override
    public void onSpeedrunEvent(SpeedrunEvent e) {
        if(e.type.equals(this.endEvent)){
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(()-> {
                this.endPoint = reader.getPlayerLocation();
            }, TrackerOptions.getInstance().game_save_interval, TimeUnit.SECONDS);
        }

        if(e.type.equals(this.startEvent)){
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(()-> {
                this.startPoint = reader.getPlayerLocation();
            }, TrackerOptions.getInstance().game_save_interval, TimeUnit.SECONDS);
        }
    }
}
