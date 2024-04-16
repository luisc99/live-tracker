package me.cylorun;

import me.cylorun.event.WorldCreationEventHandler;
import me.cylorun.io.TrackerOptions;

public class Tracker {
    public static String[] HEADER_LABELS = {};
    public static void main(String[] args) {
        WorldCreationEventHandler worldHandler = new WorldCreationEventHandler();
        worldHandler.addListener(world -> System.out.println(world.getAbsolutePath())); // only one WorldFile object is created per world path
    }
}