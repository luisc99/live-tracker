package me.cylorun;

import me.cylorun.event.WorldCreationEventHandler;

public class Tracker {
    public static String[] HEADER_LABELS = {};
    public static void main(String[] args) {
        WorldCreationEventHandler worldHandler = new WorldCreationEventHandler();
        worldHandler.addListener(world -> System.out.println(world.path));

    }
}