package me.cylorun.event;

import me.cylorun.enums.LogEvent;
import me.cylorun.event.callbacks.LogEventListener;
import me.cylorun.io.minecraft.WorldFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogHandler extends Thread {
    public final int DELAY = 5000;
    public final WorldFile file;
    public List<LogEventListener> listeners;

    private String lastPreviousLine = "";
    private long lastSize = 0L;

    public LogHandler(WorldFile file) {
        this.file = file;
        this.start();
    }

    public void notifyListeners(LogEvent e) {
        for (LogEventListener lel : this.listeners) {

        }
    }

    private List<String> getChanges(List<String> lines) {
        List<String> newLines = new ArrayList<>();
        int endIdx = 0;
        for (int i = lines.size() - 1; i > 0; i--) {
            if (lines.get(i).equals(this.lastPreviousLine)) {
                endIdx = i;
                break;
            }
        }
        for (int i = endIdx; i < lines.size(); i++) {
            newLines.add(lines.get(i));
        }
        return newLines;
    }

    @Override
    public void run() {
        while (true) {
            try {
                File logFile = this.file.getLogPath().toFile();
                if (logFile.length() > this.lastSize) { // log has updated
                    this.lastSize = logFile.length();
                    BufferedReader reader = new BufferedReader(new FileReader(logFile));
                    List<String> lines = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }

                    List<String> newLines = this.getChanges(lines);

                    for (String l : newLines){

                    }
                }


                Thread.sleep(this.DELAY);
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
