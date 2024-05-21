package me.cylorun.instance.live;

import com.mojang.datafixers.util.Pair;
import me.cylorun.instance.world.WorldFile;
import me.cylorun.io.TrackerOptions;
import me.cylorun.utils.Vec2i;

public class PathTracker extends Thread {
    private Pair<Vec2i, Vec2i> lastCoord;

    public PathTracker(WorldFile file) {

    }

    @Override
    public void run() {
        while (true) {

            try {
                Thread.sleep(TrackerOptions.getInstance().path_interval * 1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
