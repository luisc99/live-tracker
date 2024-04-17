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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogHandler extends Thread {
    public final int DELAY = 5000;
    public final WorldFile file;
    public List<LogEventListener> listeners;

    private String lastLine = "";
    private long lastSize = 0L;

    public LogHandler(WorldFile file) {
        this.file = file;
        this.start();
    }

    public void notifyListeners(LogEvent e) {
        for (LogEventListener lel : this.listeners) {

        }
    }

    private void parseLog(List<String> lines) {
        String regex = "^\\[\\d{2}:\\d{2}:\\d{2}\\]\\s*\\[Render\\s+thread\\/INFO\\]:\\s*\\[CHAT\\]\\s*(?!.*(?:\\[Debug\\]:|<\\w+>)).*$";

        Pattern pattern = Pattern.compile(regex);
        for (String l : lines) {
                        /*
                        [15:51:29] [Render thread/INFO]: [CHAT] Respawn point set -- pass
                        [15:51:48] [Render thread/INFO]: [CHAT] [Debug]: Render Distance: 9 -- ignore
                        [15:51:05] [Render thread/INFO]: [CHAT] <cylorun> hi -- ignore
                        */

            Matcher matcher = pattern.matcher(l);
            if (matcher.find()) {
                System.out.println("yoyoyo");
            }
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
                    this.lastLine = newLines.get(newLines.size() - 1);
                    this.parseLog(newLines);
                }


                Thread.sleep(this.DELAY);
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
