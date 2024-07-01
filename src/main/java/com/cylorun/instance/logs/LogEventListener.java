package com.cylorun.instance.logs;

import com.cylorun.instance.LogEvent;

public interface LogEventListener {

    void onLogEvent(LogEvent e);
}
