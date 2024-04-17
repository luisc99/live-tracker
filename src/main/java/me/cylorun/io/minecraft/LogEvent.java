package me.cylorun.io.minecraft;

import me.cylorun.enums.LogEventType;
import me.cylorun.io.minecraft.logs.LogParser;

public class LogEvent {

    public LogEventType type;
    public long time;
    public LogEvent(LogEventType type, String line) {
        this.time = LogParser.getTime(line);
        this.type = type;
    }
}
