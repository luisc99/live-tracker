package me.cylorun.utils;

import me.cylorun.Tracker;
import org.apache.logging.log4j.Level;

import javax.swing.*;

public class ExceptionUtil {
    public static void showError(Throwable t){
        JOptionPane.showMessageDialog(null, t.toString());
        Tracker.log(Level.ERROR, t.toString());
    }

    public static void showError(Object o){
        JOptionPane.showMessageDialog(null, o.toString());
        Tracker.log(Level.ERROR, o.toString());

    }
}
