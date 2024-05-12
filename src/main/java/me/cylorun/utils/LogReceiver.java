package me.cylorun.utils;

import me.cylorun.gui.TrackerFrame;
import org.apache.logging.log4j.Level;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogReceiver {
    public static boolean showDebug = true;

    public static void log(Level level, Object o) {
        String s = String.format("[%s/%s]: %s\n", level.toString(), nowTime(), o.toString());
        TrackerFrame.getInstance().appendLog(s);
    }

    private static String nowTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return now.format(formatter);
    }
}
