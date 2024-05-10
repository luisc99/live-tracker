package me.cylorun.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.cylorun.io.sheets.GoogleSheetsClient;
import me.cylorun.io.sheets.GoogleSheetsService;
import me.cylorun.utils.ExceptionUtil;
import me.cylorun.utils.I18n;
import me.cylorun.utils.Logging;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class TrackerOptions {

    public String sheet_id;
    public String sheet_name = "Raw Data";
    public String lang = "en_us";
    public Integer last_win_x = 0;
    public Integer last_win_y = 0;
    public Boolean gen_labels = true;
    public boolean detect_ssg = true;
    public int max_respawn_to_hr_time = 30; // seconds
    public int game_save_interval = 5; // seconds
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Path CONFIG_PATH = Paths.get("config.json");
    private static TrackerOptions instance;

    private TrackerOptions() {

    }

    public static TrackerOptions getInstance() {
        if (instance == null) {
            if (Files.exists(CONFIG_PATH)) {
                try {
                    instance = GSON.fromJson(new String((Files.readAllBytes(CONFIG_PATH))), TrackerOptions.class);
                } catch (IOException e) {
                    ExceptionUtil.showError(e);
                    throw new RuntimeException(e);
                }
            } else {
                instance = new TrackerOptions();
            }

        }
        return instance;
    }

    public static void save() {
        FileWriter writer;
        try {
            writer = new FileWriter(CONFIG_PATH.toFile());
            GSON.toJson(instance, writer);
            writer.close();
        } catch (IOException e) {
            ExceptionUtil.showError(e);
            throw new RuntimeException();
        }
    }


    public static void validateSettings() {
        Logging.info("Verifying settings");
        if (!Files.exists(Paths.get(GoogleSheetsService.CREDENTIALS_FILE))) {
            ExceptionUtil.showError("credentials.json file not found");
        } else if (getInstance().sheet_name == null || getInstance().sheet_name.isEmpty()) {
            ExceptionUtil.showError("sheet_name is not defined");
        } else if (getInstance().lang == null || getInstance().lang.isEmpty()) {
            ExceptionUtil.showError("Language not set");
        } else if (!I18n.isValidLanguage(getInstance().lang)) {
            ExceptionUtil.showError(getInstance().lang + " is not a supported language");

        } else if(!GoogleSheetsClient.isValidSheet(getInstance().sheet_id, getInstance().sheet_name)) {
            ExceptionUtil.showError("Invalid sheet_id or sheet_name");
        } else {
            Logging.info("Settings good");
        }

    }

}
