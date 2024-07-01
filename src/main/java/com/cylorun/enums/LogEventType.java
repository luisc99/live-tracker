package com.cylorun.enums;

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
