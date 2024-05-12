package me.cylorun.instance;

import me.cylorun.enums.LogEventType;
import me.cylorun.instance.logs.LogParser;

public class LogEvent {

    public LogEventType type;
    public long time;
    public LogEvent(LogEventType type, String line) {
        this.time = LogParser.getTime(line);
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type.toString();
    }
}
