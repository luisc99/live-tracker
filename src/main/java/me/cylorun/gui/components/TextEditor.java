package me.cylorun.gui.components;

import javax.swing.*;
import javax.swing.text.*;


public class TextEditor extends JScrollPane {
    private final JTextPane textPane;
    public TextEditor() {
        super(new JTextPane());
        this.textPane = (JTextPane) this.getViewport().getView();
        setupKeyBindings();
    }

    private void setupKeyBindings() {
        this.textPane.getInputMap().put(KeyStroke.getKeyStroke("control B"), "bold");
        this.textPane.getActionMap().put("bold", new StyledEditorKit.BoldAction());

        this.textPane.getInputMap().put(KeyStroke.getKeyStroke("control I"), "italic");
        this.textPane.getActionMap().put("italic", new StyledEditorKit.ItalicAction());

        this.textPane.getInputMap().put(KeyStroke.getKeyStroke("control U"), "underline");
        this.textPane.getActionMap().put("underline", new StyledEditorKit.UnderlineAction());
    }
}