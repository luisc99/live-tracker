package com.cylorun.gui.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.Stack;
import java.util.function.Consumer;

public class TextEditor extends JScrollPane {
    private final JTextPane textPane;
    private Consumer<String> consumer;

    public TextEditor(Consumer<String> consumer) {
        super(new JTextPane());
        int h = 200, w = 250;
        this.textPane = (JTextPane) this.getViewport().getView();
        this.textPane.setPreferredSize(new Dimension(w, h));
        this.textPane.setMinimumSize(new Dimension(w, h));
        this.textPane.setMaximumSize(new Dimension(w, h));
        this.setPreferredSize(new Dimension(w, h));
        this.setMinimumSize(new Dimension(w, h));
        this.setMaximumSize(new Dimension(w, h));
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.getVerticalScrollBar().setUnitIncrement(8);

        this.consumer = consumer;
        setupKeyBindings();

        if (this.consumer != null) {
            this.textPane.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    consumer.accept(getHtmlText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    consumer.accept(getHtmlText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    consumer.accept(getHtmlText());
                }
            });
        }
    }

    public TextEditor() {
        this(null);
    }

    public void setValue(String s) {
        this.textPane.setText(s);
    }

    public String getText() {
        return this.textPane.getText();
    }

    public String getHtmlText() {
        return getStyledTextAsHTML();
    }

    private void setupKeyBindings() {
        this.textPane.getInputMap().put(KeyStroke.getKeyStroke("control B"), "bold");
        this.textPane.getActionMap().put("bold", new StyledEditorKit.BoldAction());

        this.textPane.getInputMap().put(KeyStroke.getKeyStroke("control I"), "italic");
        this.textPane.getActionMap().put("italic", new StyledEditorKit.ItalicAction());

        this.textPane.getInputMap().put(KeyStroke.getKeyStroke("control U"), "underline");
        this.textPane.getActionMap().put("underline", new StyledEditorKit.UnderlineAction());
    }

    private String getStyledTextAsHTML() {
        StyledDocument doc = textPane.getStyledDocument();
        StringBuilder sb = new StringBuilder();
        Stack<String> openTags = new Stack<>();

        for (int i = 0; i < doc.getLength(); ) {
            Element element = doc.getCharacterElement(i);
            AttributeSet as = element.getAttributes();
            String text;
            try {
                text = doc.getText(i, element.getEndOffset() - i);
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }

            handleTags(openTags, as, sb);
            sb.append(text);

            i = element.getEndOffset();
        }

        while (!openTags.isEmpty()) {
            sb.append("</").append(openTags.pop()).append(">");
        }

        return sb.toString();
    }

    private void handleTags(Stack<String> openTags, AttributeSet as, StringBuilder sb) {
        boolean isBold = StyleConstants.isBold(as);
        boolean isItalic = StyleConstants.isItalic(as);
        boolean isUnderline = StyleConstants.isUnderline(as);

        if (!isBold && openTags.contains("b")) {
            sb.append("</b>");
            openTags.remove("b");
        }
        if (!isItalic && openTags.contains("i")) {
            sb.append("</i>");
            openTags.remove("i");
        }
        if (!isUnderline && openTags.contains("u")) {
            sb.append("</u>");
            openTags.remove("u");
        }

        if (isBold && !openTags.contains("b")) {
            sb.append("<b>");
            openTags.add("b");
        }
        if (isItalic && !openTags.contains("i")) {
            sb.append("<i>");
            openTags.add("i");
        }
        if (isUnderline && !openTags.contains("u")) {
            sb.append("<u>");
            openTags.add("u");
        }
    }
}
