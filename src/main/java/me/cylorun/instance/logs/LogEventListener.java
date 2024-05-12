package me.cylorun.instance.logs;

import me.cylorun.instance.LogEvent;

public interface LogEventListener {

    void onLogEvent(LogEvent e);
}
