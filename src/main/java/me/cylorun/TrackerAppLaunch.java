package me.cylorun;

import com.formdev.flatlaf.FlatDarculaLaf;
import me.cylorun.utils.ThreadUtil;
import me.cylorun.utils.UpdateUtil;
import org.apache.logging.log4j.Level;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class TrackerAppLaunch {
    public static String[] args;

    public static void main(String[] args) {
        TrackerAppLaunch.args = args;

        ToolTipManager.sharedInstance().setInitialDelay(0);
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        FlatDarculaLaf.setup();

        UpdateUtil.checkForUpdates(Tracker.VERSION);
        checkDeleteOldjar();

        Tracker.run();
    }

    private static void checkDeleteOldjar() {
        List<String> argList = Arrays.asList(args);
        if (!argList.contains("-deleteOldJar")) {
            return;
        }

        File toDelete = new File(argList.get(argList.indexOf("-deleteOldJar") + 1));
        Tracker.log(Level.INFO, "Deleting old jar " + toDelete.getName());

        for (int i = 0; i < 200 && !toDelete.delete(); i++) {
            ThreadUtil.sleep(10);
        }

        if (toDelete.exists()) {
            Tracker.log(Level.ERROR, "Failed to delete " + toDelete.getName());
        } else {
            Tracker.log(Level.INFO, "Deleted " + toDelete.getName());
        }

    }
}
