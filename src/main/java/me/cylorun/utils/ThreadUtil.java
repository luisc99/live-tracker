package me.cylorun.utils;

import me.cylorun.Tracker;
import org.apache.logging.log4j.Level;

public class ThreadUtil {
    public static void sleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Tracker.log(Level.ERROR, "Interupption occured...");
            throw new RuntimeException(e);
        }
    }

}
