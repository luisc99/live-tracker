package com.cylorun.utils;

import com.cylorun.Tracker;
import com.github.tuupertunut.powershelllibjava.PowerShell;
import com.github.tuupertunut.powershelllibjava.PowerShellExecutionException;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class PowerShellUtil {
    private static final PowerShell POWER_SHELL;

    static {
        try {
            POWER_SHELL = PowerShell.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String execute(String command) throws PowerShellExecutionException, IOException {
        Tracker.log(Level.INFO, "Running PS command: " + command);
        return POWER_SHELL.executeCommands(command);
    }
}
