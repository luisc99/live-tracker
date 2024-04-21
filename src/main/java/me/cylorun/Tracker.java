package me.cylorun;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.io.minecraft.RecordFile;
import me.cylorun.io.minecraft.Run;
import me.cylorun.io.minecraft.logs.LogHandler;
import me.cylorun.io.minecraft.world.WorldCreationEventHandler;
import me.cylorun.io.sheets.GoogleSheetsClient;
import me.cylorun.utils.ExceptionUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


public class Tracker {
    public static void main(String[] args) {
        GoogleSheetsClient.setup();

        WorldCreationEventHandler worldHandler = new WorldCreationEventHandler(); // only one WorldFile object is created per world path
        worldHandler.addListener(world -> {
            LogHandler logH = new LogHandler(world);
            world.setCompletionHandler((e)->{
                world.finished = true;
                FileReader reader = null;
                try {
                    reader = new FileReader(world.getRecordPath().toFile());
                } catch (FileNotFoundException ex) {
                    ExceptionUtil.showError(ex);
                    throw new RuntimeException(ex);
                }

                JsonObject o = JsonParser.parseReader(reader).getAsJsonObject();
                RecordFile record = new RecordFile(o);
                Run thisRun = new Run(world, record);
                List<Object> runData = thisRun.gatherAll();
                try {
                    GoogleSheetsClient.appendRowTop(runData);
                } catch (IOException | GeneralSecurityException ex) {
                    throw new RuntimeException(ex);
                }
            });

        });

    }

}