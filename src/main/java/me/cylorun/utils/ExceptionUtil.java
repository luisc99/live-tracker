package me.cylorun.utils;

import javax.swing.*;

public class ExceptionUtil {
    public static void showError(Throwable t){
        JOptionPane.showMessageDialog(null, t.toString());
        Logging.error(t);
    }

    public static void showError(Object o){
        JOptionPane.showMessageDialog(null, o.toString());
        Logging.error(o);
    }
}
