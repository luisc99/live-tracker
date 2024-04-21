package me.cylorun.io.minecraft.logs;

import me.cylorun.io.minecraft.LogEvent;

public interface LogEventListener {

    void onLogEvent(LogEvent e);
}
