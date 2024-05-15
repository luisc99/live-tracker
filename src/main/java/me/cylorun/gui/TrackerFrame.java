package me.cylorun.gui;

import me.cylorun.Tracker;
import me.cylorun.gui.components.BooleanOptionField;
import me.cylorun.gui.components.MultiChoiceOptionField;
import me.cylorun.gui.components.NumberOptionField;
import me.cylorun.gui.components.TextOptionField;
import me.cylorun.io.TrackerOptions;
import me.cylorun.utils.ExceptionUtil;
import me.cylorun.utils.I18n;

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
        this.add(this.getTextArea());
        this.add(this.getTabbedPane());
        this.setVisible(true);
    }

    private JScrollPane getTextArea() {
        this.logArea = new JTextArea();
        this.logArea.setEditable(false);
        this.logArea.setLineWrap(true);

        return new JScrollPane(this.logArea);
    }

    private JTabbedPane getTabbedPane() {
        this.tabbedPane = new JTabbedPane();
        JPanel generalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel advancedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        TrackerOptions options = TrackerOptions.getInstance();

        generalPanel.add(new TextOptionField("Sheet Name", options.sheet_name, (val) -> {
                options.sheet_name = val;
                TrackerOptions.save();

        }));

        generalPanel.add(new TextOptionField("Sheet ID", options.sheet_id, (val) -> {
                options.sheet_id = val;
                TrackerOptions.save();

        }));

        generalPanel.add(new MultiChoiceOptionField(I18n.getSupported().toArray(new String[0]), options.lang, "Game lang",(val) -> {
            options.lang = val;
            TrackerOptions.save();
        }));

        generalPanel.add(new BooleanOptionField("Generate Headers", options.gen_labels, (val)->{
            options.gen_labels = val;
            TrackerOptions.save();
        }));

        generalPanel.add(new BooleanOptionField("Detect SSG", options.detect_ssg, (val)->{
            options.detect_ssg = val;
            TrackerOptions.save();
        }));

        JButton manualButton = new JButton("Manually edit");
        manualButton.addActionListener((e) -> {
            try {
                Desktop.getDesktop().open(TrackerOptions.CONFIG_PATH.toFile());
            } catch (Exception ex) {
                ExceptionUtil.showError(ex);
            }
        });
        JButton validationButton = new JButton("Validate Settings");
        validationButton.addActionListener(e -> TrackerOptions.validateSettings());

        generalPanel.add(manualButton);
        generalPanel.add(validationButton);
        advancedPanel.add(new NumberOptionField("Save interval (s)", "The interval which game files are updated at", options.game_save_interval, (val) -> {
            options.game_save_interval = val;
            TrackerOptions.save();
        }));
        advancedPanel.add(new NumberOptionField("Max respawn to hunger reset (s)", "The maximum time between a respawn point being set and a death for it to count as a hunger reset", options.max_respawn_to_hr_time, (val) -> {
            options.max_respawn_to_hr_time = val;
            TrackerOptions.save();
        }));

        advancedPanel.add(new BooleanOptionField("Debug messages", options.show_debug, (val)->{
            options.show_debug = val;
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
