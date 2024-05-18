package me.cylorun.instance;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.Tracker;
import me.cylorun.utils.Assert;
import me.cylorun.utils.JSONUtil;
import org.apache.logging.log4j.Level;

import javax.sound.midi.Track;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LoggingPermission;

public class RecordFile {
    private JsonObject jsonObject;

    public RecordFile(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }
    public RecordFile(File file) {
        Assert.isTrue(file.getName().endsWith(".json"), "Record file must be a JSON file\n"+file.getAbsolutePath());
        this.jsonObject = JSONUtil.parseFile(file);
    }

    public JsonObject getJson() {
        return jsonObject;
    }
}
