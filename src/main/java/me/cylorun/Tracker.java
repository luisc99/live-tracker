package me.cylorun;

import me.cylorun.event.LogHandler;
import me.cylorun.event.WorldCreationEventHandler;
import me.cylorun.io.sheets.GoogleSheetsClient;

public class Tracker {
    public static void main(String[] args) {
        WorldCreationEventHandler worldHandler = new WorldCreationEventHandler();
        worldHandler.addListener(world -> {
            LogHandler logH = new LogHandler(world);
        }); // only one WorldFile object is created per world path
//        GoogleSheetsClient.generateLabels();

    }

}