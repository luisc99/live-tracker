package me.cylorun.utils;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import me.cylorun.Tracker;
import me.cylorun.instance.Run;
import me.cylorun.io.TrackerOptions;
import org.apache.logging.log4j.Level;

public class APIUtil {
    private int uploadRun(Run run) {
        String json = "{\"name\": \"John\", \"age\": 30}";
        TrackerOptions options = TrackerOptions.getInstance();
        HttpResponse<String> response = Unirest.post("https://couri100k.com/api/upload")
                .header("Content-Type", "application/json")
                .header("authorization", options.api_key)
                .body(json)
                .asString();

        return response.getStatus();
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
