package com.gasperpintar.cardgenerator.service.builder.editor;

import com.gasperpintar.cardgenerator._interface.Editor;
import com.gasperpintar.cardgenerator.service.builder.Styles;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextField;

public class BoxEditor implements Editor<Pane> {

    private Pane box;
    private final TextField widthField;
    private final TextField heightField;
    private final TextField bgColorField;
    private final TextField borderColorField;
    private final TextField borderWidthField;
    private final Runnable onChange;

    public BoxEditor(Pane box,
                     TextField widthField,
                     TextField heightField,
                     TextField bgColorField,
                     TextField borderColorField,
                     TextField borderWidthField,
                     Runnable onChange) {
        this.box = box;
        this.widthField = widthField;
        this.heightField = heightField;
        this.bgColorField = bgColorField;
        this.borderColorField = borderColorField;
        this.borderWidthField = borderWidthField;
        this.onChange = onChange;

        setupListeners();
    }

    @Override
    public void updateNode(Pane node) {
        this.box = node;
        updateFields();
    }

    @Override
    public void updateFields() {
        if (box == null) {
            return;
        }

        if (widthField != null) {
            widthField.setText(String.valueOf((int) box.getPrefWidth()));
        }

        if (heightField != null) {
            heightField.setText(String.valueOf((int) box.getPrefHeight()));
        }

        if (bgColorField != null) {
            bgColorField.setText(Styles.extractStyleValue(box.getStyle(), "-fx-background-color"));
        }

        if (borderColorField != null) {
            borderColorField.setText(Styles.extractStyleValue(box.getStyle(), "-fx-border-color"));
        }

        if (borderWidthField != null) {
            borderWidthField.setText(Styles.extractStyleValue(box.getStyle(), "-fx-border-width"));
        }
    }

    private void setupListeners() {
        if (widthField != null) {
            widthField.textProperty().addListener((_, _, newVal) -> {
                if (box != null) {
                    try {
                        box.setPrefWidth(Double.parseDouble(newVal));
                    } catch (NumberFormatException _) {}
                    onChange.run();
                }
            });
        }

        if (heightField != null) {
            heightField.textProperty().addListener((_, _, newVal) -> {
                if (box != null) {
                    try {
                        box.setPrefHeight(Double.parseDouble(newVal));
                    } catch (NumberFormatException _) {}
                    onChange.run();
                }
            });
        }

        if (bgColorField != null) {
            bgColorField.textProperty().addListener((_, _, newVal) -> {
                updateStyle("-fx-background-color", newVal);
                onChange.run();
            });
        }

        if (borderColorField != null) {
            borderColorField.textProperty().addListener((_, _, newVal) -> {
                updateStyle("-fx-border-color", newVal);
                onChange.run();
            });
        }

        if (borderWidthField != null) {
            borderWidthField.textProperty().addListener((_, _, newVal) -> {
                updateStyle("-fx-border-width", newVal);
                onChange.run();
            });
        }
    }

    private void updateStyle(String property, String value) {
        if (box != null) {
            box.setStyle(Styles.setStyleValue(box.getStyle(), property, value));
        }
    }
}
