package me.cylorun.instance.live;

import kaptainwutax.mcutils.state.Dimension;
import me.cylorun.enums.LogEventType;
import me.cylorun.instance.LogEvent;
import me.cylorun.instance.logs.LogEventListener;
import me.cylorun.instance.world.WorldFile;
import me.cylorun.io.TrackerOptions;
import me.cylorun.utils.Vec2i;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PathTracker implements LogEventListener {
    private Vec2i lastCoord;
    private WorldFile file;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public PathTracker(WorldFile file) {
        this.file = file;
        this.file.logHandler.addListener(this);
        this.lastCoord = file.reader.getPlayerLocation();
        this.executor.scheduleAtFixedRate(this::tick, 0L, TrackerOptions.getInstance().path_interval, TimeUnit.SECONDS);
    }

    private void tick() {
        Dimension dim = this.file.reader.getPlayerDimension();
        Vec2i currCoord = file.reader.getPlayerLocation();
        Pair<Pair<Vec2i, Vec2i>, Dimension> p = Pair.of(Pair.of(this.lastCoord, currCoord), dim);

        this.file.playerPath.add(p);
        this.lastCoord = currCoord;
    }

    @Override
    public void onLogEvent(LogEvent e) {
        if (e.type.equals(LogEventType.DEATH)) {
            executor.schedule(() -> {
                this.lastCoord = file.reader.getPlayerLocation();
            }, 10L, TimeUnit.SECONDS);
        }
    }
}
