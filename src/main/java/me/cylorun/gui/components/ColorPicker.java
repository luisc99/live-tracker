package me.cylorun.gui.components;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class ColorPicker extends JPanel {
    private final JPanel previewPanel;
    private final JColorChooser colorChooser;
    private Consumer<Color> consumer;
    private Color currColor;

    public ColorPicker() {
        this(Color.WHITE);
    }

    public ColorPicker(Color color) {
        this.currColor = color;
        this.previewPanel = new JPanel();
        this.colorChooser = createColorChooser(color);
        this.previewPanel.setToolTipText("Click to edit");
        this.previewPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openColorChooserDialog();
            }
        });

        this.previewPanel.setBackground(color);
        this.previewPanel.setPreferredSize(new Dimension(70, 70));
        this.add(previewPanel);
    }

    private JColorChooser createColorChooser(Color color) {
        JColorChooser colorChooser = new JColorChooser(color);

        AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
        for (AbstractColorChooserPanel panel : panels) {
            if (!panel.getDisplayName().equals("HSV")) {
                colorChooser.removeChooserPanel(panel);
            }
        }

        colorChooser.setPreviewPanel(this.previewPanel);
        colorChooser.getSelectionModel().addChangeListener(e -> {
            Color newColor = colorChooser.getColor();
            previewPanel.setBackground(newColor);
            currColor = newColor;
            if (consumer != null) {
                consumer.accept(newColor);
            }
        });

        return colorChooser;
    }

    private void openColorChooserDialog() {
        JDialog dialog = JColorChooser.createDialog(
                this,
                "Choose a Color",
                true,
                this.colorChooser,
                e -> {
                    Color selectedColor = colorChooser.getColor();
                    previewPanel.setBackground(selectedColor);
                    currColor = selectedColor;
                    if (consumer != null) {
                        consumer.accept(selectedColor);
                    }
                },
                null
        );

        dialog.setSize(600, 400);
        dialog.setVisible(true);
    }

    public void addConsumer(Consumer<Color> consumer) {
        this.consumer = consumer;
    }

    public void setColor(Color color) {
        this.colorChooser.setColor(color);
        this.currColor = color;
        this.previewPanel.setBackground(color);
    }

    public String getColorString() {
        Color color = this.colorChooser.getColor();
        return String.format("%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue());
    }

    public Color getCurrentColor() {
        return currColor;
    }
}
