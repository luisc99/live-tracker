package me.cylorun.gui.components;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.util.function.Consumer;

public class ColorPicker extends JColorChooser {
    private final JPanel previewPanel;
    private Consumer<Color> consumer;

    public ColorPicker() {
        this(Color.WHITE);
    }

    public ColorPicker(Color color) {
        super(color);
        AbstractColorChooserPanel[] panels = this.getChooserPanels();
        for (AbstractColorChooserPanel p : panels) {
            if (p.getDisplayName().equals("Swatches") || p.getDisplayName().equals("RGB") ||
                    p.getDisplayName().equals("HSL") || p.getDisplayName().equals("CMYK")) {
                this.removeChooserPanel(p);
            }
        }

        this.previewPanel = new JPanel();
        previewPanel.setBackground(this.getColor());
        previewPanel.add(new JLabel(""));
        previewPanel.setPreferredSize(new Dimension(50, 50));
        this.getSelectionModel().addChangeListener(e -> {
            previewPanel.setBackground(this.getColor());
            if (this.consumer != null) {
                consumer.accept(this.getColor());
            }
        });

        this.setPreviewPanel(previewPanel);

    }

    public void addConsumer(Consumer<Color> consumer) {
        this.consumer = consumer;
    }

    public String getColorString() {
        return String.format("%s,%s,%s", this.getColor().getRed(), this.getColor().getGreen(), this.getColor().getBlue());
    }

}
