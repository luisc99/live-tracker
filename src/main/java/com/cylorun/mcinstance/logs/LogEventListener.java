package com.cylorun.mcinstance.logs;

import com.cylorun.mcinstance.LogEvent;

public interface LogEventListener {
    void onLogEvent(LogEvent e);
}
