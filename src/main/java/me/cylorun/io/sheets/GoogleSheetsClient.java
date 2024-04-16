package me.cylorun.io.sheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import me.cylorun.io.TrackerOptions;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static me.cylorun.io.sheets.GoogleSheetsService.getSheetsService;

public class GoogleSheetsClient {

    public static void generateLabels() {

    }

    public static void appendRowTop(String sheetId, List<Object> rowData) throws IOException, GeneralSecurityException {
        Sheets sheetsService = getSheetsService();
        String sheetName = TrackerOptions.getInstance().sheet_name;
        String range = "A3:CE";

        ValueRange response = sheetsService.spreadsheets().values()
                .get(sheetId, sheetName + "!" + range)
                .execute();
        List<List<Object>> values = response.getValues();

        List<List<Object>> newValues = new ArrayList<>();
        newValues.add(rowData);
        if (values != null) {
            newValues.addAll(values);
        }

        ValueRange body = new ValueRange().setValues(newValues);
        UpdateValuesResponse result = sheetsService.spreadsheets().values()
                .update(sheetId, sheetName + "!" + range.split(":")[0], body)
                .setValueInputOption("RAW")
                .execute();
    }
}
