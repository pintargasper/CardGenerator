package com.gasperpintar.cardgenerator.service.builder.editor;

import com.gasperpintar.cardgenerator._interface.Editor;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;

public class ImageViewEditor implements Editor<ImageView> {

    private ImageView imageView;
    private final TextField widthField;
    private final TextField heightField;
    private final TextField idField;
    private final Runnable onChange;

    public ImageViewEditor(ImageView imageView,
                           TextField widthField,
                           TextField heightField,
                           TextField idField,
                           Runnable onChange) {
        this.imageView = imageView;
        this.widthField = widthField;
        this.heightField = heightField;
        this.idField = idField;
        this.onChange = onChange;

        setupListeners();
    }

    @Override
    public void updateNode(ImageView node) {
        this.imageView = node;
        updateFields();
    }

    @Override
    public void updateFields() {
        if (imageView == null) {
            return;
        }

        if (widthField != null) {
            widthField.setText(String.valueOf((int) imageView.getFitWidth()));
        }

        if (heightField != null) {
            heightField.setText(String.valueOf((int) imageView.getFitHeight()));
        }

        if (idField != null) {
            idField.setText(imageView.getId() != null ? imageView.getId() : "");
        }
    }

    private void setupListeners() {
        if (widthField != null) {
            widthField.textProperty().addListener((_, _, newVal) -> {
                if (imageView != null) {
                    try {
                        double width = Double.parseDouble(newVal);
                        if (width <= 0) {
                            width = 1;
                        }
                        imageView.setFitWidth(width);
                    } catch (NumberFormatException _) {}
                    onChange.run();
                }
            });
        }

        if (heightField != null) {
            heightField.textProperty().addListener((_, _, newVal) -> {
                if (imageView != null) {
                    try {
                        double height = Double.parseDouble(newVal);
                        if (height <= 0) {
                            height = 1;
                        }
                        imageView.setFitHeight(height);
                    } catch (NumberFormatException _) {}
                    onChange.run();
                }
            });
        }

        if (idField != null) {
            idField.textProperty().addListener((_, _, newVal) -> {
                if (imageView != null) imageView.setId(newVal);
                onChange.run();
            });
        }
    }
}
