package me.cylorun.gui.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.function.Consumer;

public class TextOptionField extends JPanel {

    public TextOptionField(String label, String value, Consumer<String> consumer) {
        this(label, value, false, consumer);
    }

    public TextOptionField(String label, String value, boolean isPasswordField, Consumer<String> consumer) {
        this.setLayout(new FlowLayout(FlowLayout.CENTER));

        JComponent textField = isPasswordField ? createPasswordField(value) : createTextField(value);
        this.add(new JLabel(label));
        this.add(textField);

        addChangeListener(textField, consumer);
    }

    private JComponent createTextField(String value) {
        JTextField textField = new JTextField(value);
        textField.setPreferredSize(new Dimension(200, 25));
        return textField;
    }

    private JComponent createPasswordField(String value) {
        JPasswordField passwordField = new JPasswordField(value);
        passwordField.setPreferredSize(new Dimension(200, 25));
        return passwordField;
    }

    private void addChangeListener(JComponent component, Consumer<String> consumer) {
        if (component instanceof JTextField textField) {
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
        } else if (component instanceof JPasswordField passwordField) {
            passwordField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    consumer.accept(String.valueOf(passwordField.getPassword()));
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    consumer.accept(String.valueOf(passwordField.getPassword()));
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    consumer.accept(String.valueOf(passwordField.getPassword()));
                }
            });
        }
    }
}
