package com.cylorun.instance;

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

    public enum LogEventType {
        DEATH("death"),
        HUNGER_RESET("hunger_reset"),
        RESPAWN_SET("respawn_set");

        private String label;

        private LogEventType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return this.label;
        }
    }
}
