package me.cylorun.event;

import me.cylorun.io.minecraft.WorldFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldEventHandler extends Thread {
    public List<SpeedrunEvent> events;
    public SpeedrunEvent latestEvent;
    private final WorldFile file;

    public WorldEventHandler(WorldFile file) {
        this.file = file;
        this.latestEvent = null;
        this.events = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.file));

                Thread.sleep(1000);
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
