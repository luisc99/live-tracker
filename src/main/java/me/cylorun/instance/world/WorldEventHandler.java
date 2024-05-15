package me.cylorun.instance.world;

import me.cylorun.Tracker;
import me.cylorun.instance.SpeedrunEvent;
import me.cylorun.utils.ExceptionUtil;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldEventHandler extends Thread {
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


    public void setFile(WorldFile file) {
        this.file = file;
    }

    public void addListener(WorldEventListener wel) {
        this.listeners.add(wel);
    }

    public void notifyListeners(SpeedrunEvent e) {
        for (WorldEventListener wel : this.listeners) {
            wel.onSpeedrunEvent(e);
        }
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

    private boolean hasChanges(List<SpeedrunEvent> list) {
        if (this.events.size() != list.size()){
            return true;
        }
        for (int i = 0; i < this.events.size(); i++) {
            if (!this.events.get(i).equals(list.get(i))) {
                return true;
            }
        }
        return false;
    }

    private List<SpeedrunEvent> getChanges(List<SpeedrunEvent> newEvents) { // returns events that havent already been added
        List<SpeedrunEvent> changes = new ArrayList<>();
        for (int i = 0; i < newEvents.size(); i++) {
            if (this.events.size() - 1 < i) {
                changes.add(newEvents.get(i));
            } else if (!this.events.get(i).equals(newEvents.get(i))) {
                changes.add(newEvents.get(i));
            }
        }
        return changes;
    }

    @Override
    public void run() {
        while (true) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.file.getEventLog().toFile()));
                List<SpeedrunEvent> eventLog = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    eventLog.add(new SpeedrunEvent(line));
                }

                if (this.hasChanges(eventLog)) {
                    this.latestEvent = eventLog.get(eventLog.size() - 1);
                    List<SpeedrunEvent> changes = this.getChanges(eventLog);
                    for (SpeedrunEvent e : changes) {
                        this.notifyListeners(e);
                    }

                    this.events = eventLog;

                }

                Thread.sleep(2000);
            } catch (InterruptedException | IOException e) {
                Tracker.log(Level.ERROR, e);
                throw new RuntimeException(e);
            }
        }
    }

}
