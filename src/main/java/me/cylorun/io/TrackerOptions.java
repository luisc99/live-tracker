package me.cylorun.io;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.io.sheets.GoogleSheetsService;
import me.cylorun.utils.ExceptionUtil;
import me.cylorun.utils.I18n;
import me.cylorun.utils.Logging;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.function.Consumer;

public class TrackerOptions {

    public String sheet_id;
    public String sheet_name;
    public String lang;
    public Integer last_win_x = 0;
    public Integer last_win_y = 0;
    public Boolean gen_labels = true;
    public boolean detect_ssg = true;
    public int max_respawn_to_hr_time; // seconds
    public int game_save_interval; // seconds

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Path CONFIG_PATH = Paths.get("config.json");
    private static Consumer<Boolean> consumer;
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
        new Thread(TrackerOptions::validateSettings).start();
    }

    public static void setValidSettingsConsumer(Consumer<Boolean> consumer) {
        TrackerOptions.consumer = consumer;
    }

    public static boolean validateSettings() {
        boolean isValid = true;
        Logging.info("Verifying settings");
        if (!Files.exists(Paths.get(GoogleSheetsService.CREDENTIALS_FILE))) {
            ExceptionUtil.showError("credentials.json file not found");
            isValid = false;
        } else if (getInstance().sheet_name == null || getInstance().sheet_name.isEmpty()) {
            ExceptionUtil.showError("sheet_name is not defined");
            isValid = false;
        } else if (getInstance().lang == null || getInstance().lang.isEmpty()) {
            ExceptionUtil.showError("Language not set");
            isValid = false;
        } else if (!I18n.isValidLanguage(getInstance().lang)) {
            ExceptionUtil.showError(getInstance().lang + " is not a supported language");
            isValid = false;
        }

        try {
            GoogleSheetsService.getSheetsService().spreadsheets().values()
                    .get(getInstance().sheet_id, getInstance().sheet_name + "!A1:B")
                    .execute();
        } catch (NullPointerException a) {
            ExceptionUtil.showError("sheet_id not defined");
            isValid = false;
        } catch (GeneralSecurityException | IOException b) {
            String[] lines = b.toString().split("\\n");
            String errorJson = String.join("\n", Arrays.copyOfRange(lines, 1, lines.length));

            JsonObject o = JsonParser.parseString(errorJson).getAsJsonObject();
            if (o.get("code").getAsInt() == 400) {
                ExceptionUtil.showError("Invalid sheet name");
            } else if (o.get("code").getAsInt() == 404) {
                ExceptionUtil.showError("Invalid sheet id");
            } else {
                ExceptionUtil.showError("Something went wrong\n"+b);
            }
            isValid = false;
        }

        consumer.accept(isValid);
        return isValid;
    }

}
