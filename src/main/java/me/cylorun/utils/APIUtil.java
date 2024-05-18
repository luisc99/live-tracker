package me.cylorun.utils;

import me.cylorun.Tracker;
import me.cylorun.instance.Run;
import me.cylorun.io.TrackerOptions;
import okhttp3.*;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class APIUtil {
    public static final String API_URL = "http://localhost:5000";
    private int uploadRun(Run run) {
        TrackerOptions options = TrackerOptions.getInstance();
        OkHttpClient client = new OkHttpClient();

        String json = "{\"key\":\"value\"}";

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .url(API_URL+"/upload")
                .post(requestBody)
                .addHeader("authorization", options.api_key)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Tracker.log(Level.ERROR, "Failed to upload run, trying again");
            return 500;
        }

       return response.code();
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
