package me.cylorun.enums;

public enum LogEvent {
    DEATH("death"),
    RESPAWN_SET("respawn_set");

    private String label;

    private LogEvent(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
