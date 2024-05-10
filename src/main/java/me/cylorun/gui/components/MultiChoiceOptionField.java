package me.cylorun.gui.components;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MultiChoiceOptionField extends JPanel {
    public MultiChoiceOptionField(String[] options, String value, String label, Consumer<String> consumer) {
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        JComboBox<String> comboBox = new JComboBox<>(options);
        SwingUtilities.invokeLater(() -> comboBox.setSelectedItem(value));
        comboBox.addActionListener(e -> consumer.accept((String) comboBox.getSelectedItem()));
        this.add(new JLabel(label));
        this.add(comboBox);
    }
}
