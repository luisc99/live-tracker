package com.cylorun.utils;

import com.cylorun.Tracker;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    public static String toDetailedString(Throwable t) {
        StringWriter out = new StringWriter();
        out.write(t.toString() + "\n");
        t.printStackTrace(new PrintWriter(out));
        return out.toString();
    }

    public static void showDialogAndExit(String message) {
        JOptionPane.showMessageDialog(null,  message, "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
        Tracker.stop();
    }
}
