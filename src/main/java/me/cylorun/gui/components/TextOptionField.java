package me.cylorun.gui.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.function.Consumer;

public class TextOptionField extends JPanel {
    public TextOptionField(String label, String value, Consumer<String> consumer) {
        this.setLayout(new FlowLayout(FlowLayout.CENTER));

        JTextField textField = new JTextField(value);
        textField.setPreferredSize(new Dimension(200, 25));
        this.add(new JLabel(label));
        this.add(textField);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                consumer.accept(textField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                consumer.accept(textField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                consumer.accept(textField.getText());
            }
        });

    }
}
