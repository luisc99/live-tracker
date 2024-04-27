package me.cylorun.io.minecraft.live;

import me.cylorun.enums.SpeedrunEventType;
import me.cylorun.io.TrackerOptions;
import me.cylorun.io.minecraft.NBTReader;
import me.cylorun.io.minecraft.SpeedrunEvent;
import me.cylorun.io.minecraft.world.WorldEventListener;
import me.cylorun.io.minecraft.world.WorldFile;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.system.CallbackI;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StrongholdTracker implements WorldEventListener {
    private WorldFile file;
    public Vec3i exitLoc = Vec3i.ZERO;
    public Vec3i strongholdLoc = Vec3i.ZERO;
    private final NBTReader reader;

    public StrongholdTracker(WorldFile file){
        this.reader = new NBTReader(file.getLevelDatPath());
        this.file = file;
        this.file.eventHandler.addListener(this);

    }
    @Override
    public void onSpeedrunEvent(SpeedrunEvent e) {
        if(e.type.equals(SpeedrunEventType.ENTER_STRONGHOLD)){
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(()-> {
                this.exitLoc = reader.getLocation();
            }, TrackerOptions.getInstance().game_save_interval, TimeUnit.SECONDS);
        }

        if(e.type.equals(SpeedrunEventType.FIRST_PORTAL)){
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(()-> {
                this.exitLoc = reader.getLocation();
            }, TrackerOptions.getInstance().game_save_interval, TimeUnit.SECONDS);
        }
    }
}
