package me.cylorun.io.minecraft.logs;

import me.cylorun.enums.LogEventType;
import me.cylorun.io.minecraft.world.WorldFile;

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

    private String lastLine = "";
    private long lastSize = 0L;

    public LogHandler(WorldFile file) {
        this.file = file;
        this.lastSize = this.file.getLogPath().toFile().length();
        this.start();
    }

    public void notifyListeners(LogEventType e) {
        for (LogEventListener lel : this.listeners) {

        }
    }

    private List<String> getChanges(List<String> lines) {
        List<String> newLines = new ArrayList<>();
        int endIdx = 0;
        for (int i = lines.size() - 1; i > 0; i--) {
            if (lines.get(i).equals(this.lastLine)) {
                endIdx = i;
                break;
            }
        }
        for (int i = endIdx + 1; i < lines.size(); i++) {
            newLines.add(lines.get(i));
        }
        return newLines;
    }
    private List<String> readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }

    @Override
    public void run() {
        File logFile = this.file.getLogPath().toFile();
        List<String> t = null;
        try {
            t = this.getChanges(this.readFile(logFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.lastLine = t.get(t.size() - 1); // to ignore events that occured before loading the world, prevents duplication when rejoining

        while (true) {
            try {
                if (logFile.length() > this.lastSize) { // log has updated
                    this.lastSize = logFile.length();


                    List<String> newLines = this.getChanges(this.readFile(logFile));
                    List<LogEventType> events = LogParser.getAllEvents(newLines, this.file);
                    this.lastLine = newLines.get(newLines.size() - 1);

                    System.out.println(events);
                }


                Thread.sleep(this.DELAY);
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
