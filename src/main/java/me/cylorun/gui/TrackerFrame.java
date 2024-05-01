package me.cylorun.gui;

import me.cylorun.Tracker;
import me.cylorun.gui.components.NumberOptionField;
import me.cylorun.gui.components.TextOptionField;
import me.cylorun.io.TrackerOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class TrackerFrame extends JFrame implements WindowListener {

    private static TrackerFrame instance;
    private JTextArea logArea;
    private JTabbedPane tabbedPane;

    public TrackerFrame() {
        super("Live-Tracker " + Tracker.VERSION);


        this.setSize(700, 300);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(this);
        this.setLayout(new GridLayout(1, 2));
//        this.setLayout(new BorderLayout());

//        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
//        separator.setSize(new Dimension(5, Integer.MAX_VALUE));
//        JPanel hp = new JPanel();
//        hp.setLayout(new BoxLayout(hp, BoxLayout.X_AXIS));
//        hp.add(this.getTextArea());
//        hp.add(separator);
//        hp.add(this.getTabbedPane());

//        this.add(hp);

        this.add(this.getTextArea());
        this.add(this.getTabbedPane());

    }

    private JScrollPane getTextArea() {
        this.logArea = new JTextArea();
        this.logArea.setEditable(false);
        this.logArea.setLineWrap(true);

        return new JScrollPane(this.logArea);
    }

    private JTabbedPane getTabbedPane() {
        this.tabbedPane = new JTabbedPane();
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new GridLayout(3, 1));
        JPanel advancedPanel = new JPanel();
        TrackerOptions options = TrackerOptions.getInstance();

        generalPanel.add(new TextOptionField("Sheet Name", options.sheet_name, (val) -> {
            options.sheet_name = val;
            TrackerOptions.save();
        }));
        generalPanel.add(new TextOptionField("Sheet ID", options.sheet_id, (val) -> {
            options.sheet_id = val;
            TrackerOptions.save();
        }));
        generalPanel.add(new TextOptionField("Game lang", options.lang, (val) -> {
            options.lang = val;
            TrackerOptions.save();
        }));

        advancedPanel.add(new NumberOptionField("Save interval (s)", "The interval which game files are updated at", options.game_save_interval, (val) -> {
            options.game_save_interval = val;
            TrackerOptions.save();
        }));
        advancedPanel.add(new NumberOptionField("Max respawn to HR (s)", "The maximum time between a respawn point being set and a death for it to count as a hunger reset", options.max_respawn_to_hr_time, (val) -> {
            options.max_respawn_to_hr_time = val;
            TrackerOptions.save();
        }));

        this.tabbedPane.add("General", generalPanel);
        this.tabbedPane.add("Advanced", advancedPanel);

        return this.tabbedPane;
    }

    public void appendLog(Object o) {
        SwingUtilities.invokeLater(() -> this.logArea.append(o.toString()));
    }


    public void open() {
        TrackerOptions options = TrackerOptions.getInstance();
        this.setLocation(options.last_win_x, options.last_win_y);
        this.setVisible(true);
    }

    public static TrackerFrame getInstance() {
        if (instance == null) {
            instance = new TrackerFrame();
        }
        return instance;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        TrackerOptions options = TrackerOptions.getInstance();
        options.last_win_x = e.getWindow().getX();
        options.last_win_y = e.getWindow().getY();
        TrackerOptions.save();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
