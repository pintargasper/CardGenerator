package com.gasperpintar.cardgenerator.service.builder.editor;

import com.gasperpintar.cardgenerator._interface.Editor;
import com.gasperpintar.cardgenerator.service.builder.Styles;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LabelEditor implements Editor<Label> {

    private Label label;
    private final TextField textField;
    private final TextField colorField;
    private final TextField sizeField;
    private final TextField paddingField;
    private final TextField idField;
    private final Runnable onChange;

    public LabelEditor(Label label,
                       TextField textField,
                       TextField colorField,
                       TextField sizeField,
                       TextField paddingField,
                       TextField idField,
                       Runnable onChange) {
        this.label = label;
        this.textField = textField;
        this.colorField = colorField;
        this.sizeField = sizeField;
        this.paddingField = paddingField;
        this.idField = idField;
        this.onChange = onChange;

        setupListeners();
    }

    @Override
    public void updateNode(Label node) {
        this.label = node;
        updateFields();
    }

    @Override
    public void updateFields() {
        if (label == null) {
            return;
        }

        if (textField != null) {
            textField.setText(label.getText());
        }

        if (colorField != null) {
            colorField.setText(Styles.extractStyleValue(label.getStyle(), "-fx-text-fill"));
        }

        if (sizeField != null) {
            sizeField.setText(Styles.extractStyleValue(label.getStyle(), "-fx-font-size").replace("px", ""));
        }

        if (paddingField != null) {
            paddingField.setText(Styles.extractStyleValue(label.getStyle(), "-fx-padding"));
        }

        if (idField != null) {
            idField.setText(label.getId() != null ? label.getId() : "");
        }
    }

    private void setupListeners() {
        if (textField != null) {
            textField.textProperty().addListener((_, _, newVal) -> {
                if (label != null) {
                    label.setText(newVal);
                }
                onChange.run();
            });
        }

        if (colorField != null) {
            colorField.textProperty().addListener((_, _, newVal) -> {
                if (newVal != null && newVal.matches("^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$"))
                    updateStyle("-fx-text-fill", newVal);
                onChange.run();
            });
        }

        if (sizeField != null) {
            sizeField.textProperty().addListener((_, _, newVal) -> {
                if (newVal != null && !newVal.isEmpty()) {
                    updateStyle("-fx-font-size", String.format("%spx", newVal));
                } else {
                    updateStyle("-fx-font-size", null);
                }
                onChange.run();
            });
        }

        if (paddingField != null) {
            paddingField.textProperty().addListener((_, _, newVal) -> {
                updateStyle("-fx-padding", newVal != null && !newVal.isEmpty() ? String.format("%spx", newVal) : null);
                onChange.run();
            });
        }

        if (idField != null) {
            idField.textProperty().addListener((_, _, newVal) -> {
                if (label != null) label.setId(newVal);
                onChange.run();
            });
        }
    }

    private void updateStyle(String property, String value) {
        if (label != null) {
            label.setStyle(Styles.setStyleValue(label.getStyle(), property, value));
        }
    }
}
