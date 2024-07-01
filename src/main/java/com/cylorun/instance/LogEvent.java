package com.cylorun.instance;

import com.cylorun.enums.LogEventType;
import com.cylorun.instance.logs.LogParser;

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
