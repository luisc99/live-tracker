package me.cylorun.io.minecraft.live;

import me.cylorun.enums.SpeedrunEventType;
import me.cylorun.io.TrackerOptions;
import me.cylorun.io.minecraft.NBTReader;
import me.cylorun.io.minecraft.SpeedrunEvent;
import me.cylorun.io.minecraft.world.WorldEventListener;
import me.cylorun.io.minecraft.world.WorldFile;
import me.cylorun.utils.Vec2i;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.system.CallbackI;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StrongholdTracker implements WorldEventListener {
    private WorldFile file;
    public Vec2i exitLoc;
    public Vec2i strongholdLoc;
    private final NBTReader reader;

    public StrongholdTracker(WorldFile file){
        this.reader = new NBTReader(file.getLevelDatPath());
        this.file = file;
        this.file.eventHandler.addListener(this);
    }

    public String getFinalData() {
        if (this.exitLoc == null || this.strongholdLoc == null) {
            return "Could not determine stronghold distance";
        }
        return String.valueOf(this.exitLoc.distanceTo(this.strongholdLoc));
    }
    public String getStrongholdRing() {
        if (this.strongholdLoc == null) {
            return "Failed to determine stronghold ring";
        }

        int dist = this.strongholdLoc.distanceTo(Vec2i.ZERO);
        if (dist <= 2816) return "1";
        if (dist <= 5888) return "2";
        if (dist <= 8960) return "3";
        if (dist <= 12032) return "4";
        if (dist <= 15104) return "5";
        if (dist <= 18146) return "6";
        if (dist <= 21248) return "7";
        return "8";

    }
    @Override
    public void onSpeedrunEvent(SpeedrunEvent e) {
        if(e.type.equals(SpeedrunEventType.ENTER_STRONGHOLD)){
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(()-> {
                this.strongholdLoc = reader.getPlayerLocation();
            }, TrackerOptions.getInstance().game_save_interval, TimeUnit.SECONDS);
        }

        if(e.type.equals(SpeedrunEventType.FIRST_PORTAL)){
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(()-> {
                this.exitLoc = reader.getPlayerLocation();
            }, TrackerOptions.getInstance().game_save_interval, TimeUnit.SECONDS);
        }
    }
}
