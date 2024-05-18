package me.cylorun.utils;

import me.cylorun.Tracker;
import me.cylorun.instance.Run;
import me.cylorun.io.TrackerOptions;
import org.apache.logging.log4j.Level;

public class APIUtil {
    public static final String API_URL = "http://localhost:5000";
    private int uploadRun(Run run) {
       return 200;
    }

    public void tryUploadRun(Run run) {
        int retries = 0;
        while (uploadRun(run) != 200) {
            if (retries++ > 50) {
                Tracker.log(Level.ERROR, "Failed to upload run");
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
