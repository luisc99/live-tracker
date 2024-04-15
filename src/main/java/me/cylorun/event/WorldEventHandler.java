package me.cylorun.event;

import me.cylorun.event.callbacks.WorldEventListener;
import me.cylorun.io.minecraft.WorldFile;
import me.cylorun.utils.ExceptionUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldEventHandler extends Thread {
    private final int DELAY_MS = 5000;
    public List<SpeedrunEvent> events;
    private List<WorldEventListener> listeners;
    private SpeedrunEvent latestEvent;
    private WorldFile file;

    public WorldEventHandler(WorldFile file) {
        this.file = file;
        this.latestEvent = null;
        this.events = new ArrayList<>();
        this.listeners = new ArrayList<>();

        this.loadOld();
        this.start();
    }

    private void loadOld() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.file.getEventLog().toFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                this.events.add(new SpeedrunEvent(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFile(WorldFile file) {
        this.file = file;
    }

    public void addListener(WorldEventListener wel) {
        this.listeners.add(wel);
    }

    public void notifyListeners() {
        for (WorldEventListener wel : this.listeners) {
            wel.onNewEvent(this.latestEvent);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.file.getEventLog().toFile()));
                List<SpeedrunEvent> newEvents = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    newEvents.add(new SpeedrunEvent(line));
                }
                if (!newEvents.equals(this.events)) {
                    this.latestEvent = newEvents.get(newEvents.size() - 1);
                    this.events.add(this.latestEvent);
                    this.notifyListeners();
                }

                Thread.sleep(this.DELAY_MS);
            } catch (InterruptedException | IOException e) {
                ExceptionUtil.showError(e);
                throw new RuntimeException(e);
            }
        }
    }

}
