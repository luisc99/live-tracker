package me.cylorun.event;

import me.cylorun.enums.LogEvent;
import me.cylorun.event.callbacks.LogEventListener;
import me.cylorun.io.minecraft.WorldFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

    @Override
    public void run() {
        while (true) {
            try {
                File logFile = this.file.getLogPath().toFile();
                if (logFile.length() > this.lastSize) {
                    this.lastSize = logFile.length();
                    FileReader reader = new FileReader(logFile);

                }


                Thread.sleep(this.DELAY);
            } catch (InterruptedException | FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
