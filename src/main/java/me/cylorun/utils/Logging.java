package me.cylorun.utils;

import me.cylorun.gui.TrackerFrame;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logging {
    public static boolean showDebug = true;

    public static void error(Object o) {
        String s = String.format("[ERROR/%s] %s\n", nowTime(), o.toString());
        System.err.print(s);
        TrackerFrame.getInstance().appendLog(s);
    }

    public static void warn(Object o) {
        String s = String.format("[WARN/%s] %s\n", nowTime(), o.toString());
        System.out.print(s);
        TrackerFrame.getInstance().appendLog(s);

    }

    public static void debug(Object o) {
        if (showDebug) {
            String s = String.format("[DEBUG/%s] %s\n", nowTime(), o.toString());
            System.out.print(s);
            TrackerFrame.getInstance().appendLog(s);
        }
    }

    public static void info(Object o) {
        String s = String.format("[INFO/%s] %s\n", nowTime(), o.toString());
        System.out.print(s);
        TrackerFrame.getInstance().appendLog(s);

    }

    private static String nowTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return now.format(formatter);
    }


}
