package me.cylorun.utils;

import com.google.gson.Gson;
import me.cylorun.Tracker;
import me.cylorun.instance.Run;
import me.cylorun.io.TrackerOptions;
import okhttp3.*;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class APIUtil {
    //    public static final String API_URL = "https://100k-backend.vercel.app";
    public static final String API_URL = "http://localhost:5000";

    private static int uploadRun(Run run) {
        TrackerOptions options = TrackerOptions.getInstance();
        OkHttpClient client = new OkHttpClient();

        String json = new Gson().toJson(run);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .url(API_URL + "/upload")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("authorization", options.api_key)
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Tracker.log(Level.ERROR, "Failed to upload run, trying again");
            return 502;
        }
        return response.code();
    }

    public static void tryUploadRun(Run run) {
        if (!APIUtil.verifyKey(TrackerOptions.getInstance().api_key)) {
            Tracker.log(Level.WARN, "Invalid API key or none provided, will not upload run");
            return;
        }

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

    public static boolean verifyKey(String key) {
        OkHttpClient client = new OkHttpClient();

        Request req = new Request.Builder()
                .url(API_URL + "/verify")
                .addHeader("authorization", key)
                .build();
        Response res;
        try {
            res = client.newCall(req).execute();
        } catch (IOException e) {
            Tracker.log(Level.WARN, "Invalid API key");
            return false;
        }

        return res.code() == 200;
    }
}
