package me.cylorun.utils;

import com.google.gson.Gson;
import me.cylorun.Tracker;
import me.cylorun.instance.Run;
import me.cylorun.io.TrackerOptions;
import okhttp3.*;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.regex.Pattern;

public class APIUtil {

    private static int uploadRun(Run run) {
        TrackerOptions options = TrackerOptions.getInstance();
        OkHttpClient client = new OkHttpClient();

        String runJson = new Gson().toJson(run);
        String bodyJson = String.format("{\"run\":%s}", runJson);
        Tracker.log(Level.DEBUG, "Uploading " + bodyJson);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), bodyJson);
        Request request = new Request.Builder()
                .url(TrackerOptions.getInstance().api_url+ "/upload")
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
        int code = response.code();
        response.close();
        return code;
    }

    public static void tryUploadRun(Run run) {
        if (!APIUtil.isValidKey(TrackerOptions.getInstance().api_key)) {
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

    public static boolean verifyUrl(String url) {
        String regex = "\\bhttps?:\\/\\/((localhost:\\d+)|(localhost)|([\\w.-]+\\.[a-z]{2,}))(:\\d+)?(\\/[^\\s]*)?\\b";
        Pattern p = Pattern.compile(regex);
        return p.matcher(url).matches();
    }

    public static boolean isValidKey(String key) {
        if (!isValidUrl(TrackerOptions.getInstance().api_url)) {
            Tracker.log(Level.WARN, "Provided API url is invalid, can't verify key");
            return false;
        }
        if (key == null) {
            Tracker.log(Level.WARN, "No API key added");
            return false;
        }
        OkHttpClient client = new OkHttpClient();

        Request req = new Request.Builder()
                .url(TrackerOptions.getInstance().api_url + "/verify")
                .addHeader("authorization", key)
                .build();
        try (Response res = client.newCall(req).execute()) {
            if (res.code() == 429) {
                Tracker.log(Level.WARN, "Ratelimit exceeded, try again in 15 minutes");
                return false;
            }
            if (res.code() != 200) {
                Tracker.log(Level.WARN, "Invalid API key");
            }
            return res.code() == 200;
        } catch (IOException e) {
            Tracker.log(Level.ERROR, "Failed to verify key");
            return false;
        }
    }

    public static boolean isValidUrl(String url) {
        if (url == null) {
            Tracker.log(Level.WARN, "No API Url provided");
            return false;
        }

        if (!verifyUrl(url)) {
            Tracker.log(Level.WARN, "Invalid URL Format");
            return false;
        }

        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder()
                .url(url + "/ping")
                .get()
                .build();

        try (Response res = client.newCall(req).execute()) {
            if (res.code() != 200) {
                Tracker.log(Level.WARN, "Invalid API key");
            }

            return res.code() == 200;
        } catch (IOException | IllegalArgumentException e) {
            Tracker.log(Level.ERROR, "Failed to verify URL: " + e.getMessage());
            return false;
        }
    }
}
