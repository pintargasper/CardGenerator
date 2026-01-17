package com.gasperpintar.cardgenerator.service.builder;

import com.gasperpintar.cardgenerator.controller.BuilderController;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {

    public void convertGraphicsToFXML(AnchorPane cardPreviewPane, TextArea textArea) {
        StringBuilder code = new StringBuilder();

        boolean hasLabel = false;
        boolean hasButton = false;
        boolean hasProgressBar = false;
        boolean hasImageView = false;
        boolean hasVBox = false;
        boolean hasHBox = false;

        for (Node node : cardPreviewPane.getChildren()) {
            if (node instanceof Label) {
                hasLabel = true;
            } else if (node instanceof Button) {
                hasButton = true;
            } else if (node instanceof ProgressBar) {
                hasProgressBar = true;
            } else if (node instanceof ImageView) {
                hasImageView = true;
            } else if (node instanceof VBox) {
                hasVBox = true;
            } else if (node instanceof HBox) {
                hasHBox = true;
            }
        }

        code.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        if (hasLabel) {
            code.append("<?import javafx.scene.control.Label?>\n");
        }

        if (hasButton) {
            code.append("<?import javafx.scene.control.Button?>\n");
        }

        if (hasProgressBar) {
            code.append("<?import javafx.scene.control.ProgressBar?>\n");
        }

        if (hasImageView) {
            code.append("<?import javafx.scene.image.ImageView?>\n");
        }

        if (hasVBox) {
            code.append("<?import javafx.scene.layout.VBox?>\n");
        }

        if (hasHBox) {
            code.append("<?import javafx.scene.layout.HBox?>\n");
        }
        code.append("<?import javafx.scene.layout.AnchorPane?>\n");
        code.append("<AnchorPane prefHeight=\"332\" prefWidth=\"240\" xmlns:fx=\"http://javafx.com/fxml/1\">\n");

        for (Node node : cardPreviewPane.getChildren()) {
            code.append(convertGraphicsToFXML(node));
        }
        code.append("</AnchorPane>");
        textArea.setText(code.toString());
    }

    public void convertFXMLToGraphics(AnchorPane targetPane, String fxmlText) {
        if (targetPane == null) {
            return;
        }

        targetPane.getChildren().clear();
        if (fxmlText == null || fxmlText.trim().isEmpty()) {
            return;
        }

        String[] lines = fxmlText.split("\\r?\\n");
        boolean inAnchorPane = false;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("<AnchorPane")) {
                inAnchorPane = true;
                continue;
            }

            if (trimmed.startsWith("</AnchorPane>")) {
                inAnchorPane = false;
                continue;
            }

            if (!inAnchorPane) {
                continue;
            }

            Node node = createNodeFromFXMLLine(targetPane, trimmed);
            if (node != null) {
                targetPane.getChildren().add(node);
            }
        }
    }

    public void adjustLabelWidth(AnchorPane anchorPane, Label label) {
        if (label == null || anchorPane == null) {
            return;
        }

        label.setWrapText(true);
        label.setMinWidth(0);
        label.setMaxWidth(Double.MAX_VALUE);
        double parentWidth = anchorPane.getWidth();

        Text temporaryText = new Text(label.getText());
        temporaryText.setFont(label.getFont());
        double textWidth = temporaryText.getLayoutBounds().getWidth() + 10;

        if (textWidth > parentWidth) {
            label.setPrefWidth(parentWidth);
        } else {
            label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        }
    }

    private String convertGraphicsToFXML(Node node) {
        StringBuilder atribute = new StringBuilder();

        Double x = AnchorPane.getLeftAnchor(node);
        Double y = AnchorPane.getTopAnchor(node);

        if (x != null && x != 0.0) {
            atribute.append(String.format(Locale.US, " layoutX=\"%.1f\"", x));
        }

        if (y != null && y != 0.0) {
            atribute.append(String.format(Locale.US, " layoutY=\"%.1f\"", y));
        }

        String style = node.getStyle();
        if (style != null && !style.isEmpty()) {
            atribute.append(String.format(" style=\"%s\"", escapeXML(style)));
        }

        String fxId = node.getId();
        switch (node) {
            case Label label -> {
                if (fxId != null && !fxId.isEmpty()) {
                    atribute.append(String.format(" fx:id=\"%sLabelView\"", escapeXML(fxId)));
                }
                atribute.append(" wrapText=\"true\"");

                if (node.getParent() instanceof AnchorPane parentPane) {
                    adjustLabelWidth(parentPane, label);
                }

                String padding = Styles.extractStyleValue(label.getStyle(), "-fx-padding");
                if (padding != null && !padding.isEmpty()) {
                    atribute.append(String.format(" padding=\"%s\"", escapeXML(padding)));
                }

                double prefWidth = label.getPrefWidth();
                if (prefWidth > 0.1) {
                    atribute.append(String.format(Locale.US, " prefWidth=\"%.1f\"", prefWidth));
                }

                atribute.append(String.format(" text=\"%s\"", escapeXML(label.getText())));
                return String.format("  <Label%s />\n", atribute);
            }
            case ImageView imageView -> {
                if (fxId != null && !fxId.isEmpty()) {
                    atribute.append(String.format(" fx:id=\"%sImageView\"", escapeXML(fxId)));
                }

                if (imageView.getFitWidth() != 0.0) {
                    atribute.append(String.format(Locale.US, " fitWidth=\"%.1f\"", imageView.getFitWidth()));
                }

                if (imageView.getFitHeight() != 0.0) {
                    atribute.append(String.format(Locale.US, " fitHeight=\"%.1f\"", imageView.getFitHeight()));
                }
                return String.format("  <ImageView%s />\n", atribute);
            }
            case VBox vBox -> {
                if (vBox.getPrefWidth() != 0.0) {
                    atribute.append(String.format(Locale.US, " prefWidth=\"%.1f\"", vBox.getPrefWidth()));
                }

                if (vBox.getPrefHeight() != 0.0) {
                    atribute.append(String.format(Locale.US, " prefHeight=\"%.1f\"", vBox.getPrefHeight()));
                }
                return String.format("  <VBox%s />\n", atribute);
            }
            case HBox hBox -> {
                if (hBox.getPrefWidth() != 0.0) {
                    atribute.append(String.format(Locale.US, " prefWidth=\"%.1f\"", hBox.getPrefWidth()));
                }

                if (hBox.getPrefHeight() != 0.0) {
                    atribute.append(String.format(Locale.US, " prefHeight=\"%.1f\"", hBox.getPrefHeight()));
                }
                return String.format("  <HBox%s />\n", atribute);
            }
            default -> {
                return "";
            }
        }
    }

    private Node createNodeFromFXMLLine(AnchorPane anchorPane, String line) {
        try {
            if (line.startsWith("<Label")) {
                Label label = new Label();
                setAttributes(anchorPane, label, line);
                label.getStyleClass().add("card-label");
                return label;
            }
            if (line.startsWith("<Button")) {
                Button button = new Button();
                setAttributes(anchorPane, button, line);
                button.getStyleClass().add("card-button");
                return button;
            }
            if (line.startsWith("<ProgressBar")) {
                ProgressBar progressBar = new ProgressBar();
                setAttributes(anchorPane, progressBar, line);
                progressBar.getStyleClass().add("card-progressbar");
                return progressBar;
            }
            if (line.startsWith("<ImageView")) {
                ImageView imageView = new ImageView();
                setImageViewAttributes(imageView, line);
                return imageView;
            }
            if (line.startsWith("<VBox")) {
                VBox vBox = new VBox();
                setPaneAttributes(vBox, line);
                return vBox;
            }
            if (line.startsWith("<HBox")) {
                HBox hBox = new HBox();
                setPaneAttributes(hBox, line);
                return hBox;
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return null;
    }

    private void setAttributes(AnchorPane anchorPane, Control control, String line) {
        String text = getAttribute(line, "text");
        if (control instanceof Label && text != null) {
            ((Label) control).setText(text);
        }

        if (control instanceof Button && text != null) {
            ((Button) control).setText(text);
        }

        Double x = getDoubleAttribute(line, "layoutX");
        Double y = getDoubleAttribute(line, "layoutY");

        if (x != null) {
            AnchorPane.setLeftAnchor(control, x);
        }

        if (y != null) {
            AnchorPane.setTopAnchor(control, y);
        }

        String style = getAttribute(line, "style");
        if (style != null && !style.isEmpty()) {
            control.setStyle(style.replaceAll("#[0-9a-fA-F]{1,2}(?![0-9a-fA-F])", "#000000"));
        }

        if (control instanceof Label) {
            String padding = null;
            if (style != null && !style.isEmpty()) {
                padding = Styles.extractStyleValue(style, "-fx-padding");
            }

            if (padding != null && !padding.isEmpty()) {
                String oldStyle = control.getStyle();
                String newStyle = Styles.setStyleValue(oldStyle, "-fx-padding", String.format("%spx", padding));
                control.setStyle(newStyle);
            }
        }

        if (control instanceof ProgressBar) {
            Double width = getDoubleAttribute(line, "prefWidth");
            Double progress = getDoubleAttribute(line, "progress");
            if (width != null) {
                control.setPrefWidth(width);
            }

            if (progress != null) {
                ((ProgressBar) control).setProgress(progress);
            }
        }

        String fxId = getAttribute(line, "fx:id");
        if (fxId != null && !fxId.isEmpty()) {
            if (fxId.endsWith("LabelView")) fxId = fxId.substring(0, fxId.length() - "LabelView".length());
            control.setId(fxId);
        }

        if (control instanceof Label) {
            adjustLabelWidth(anchorPane, (Label) control);
        }
    }

    private void setImageViewAttributes(javafx.scene.image.ImageView node, String line) {
        Double x = getDoubleAttribute(line, "layoutX");
        Double y = getDoubleAttribute(line, "layoutY");
        Double width = getDoubleAttribute(line, "fitWidth");
        Double height = getDoubleAttribute(line, "fitHeight");

        if (x != null) {
            AnchorPane.setLeftAnchor(node, x);
        }

        if (y != null) {
            AnchorPane.setTopAnchor(node, y);
        }

        if (width != null) {
            node.setFitWidth(width);
        }

        if (height != null) {
            node.setFitHeight(height);
        }

        String style = getAttribute(line, "style");
        if (style != null && !style.isEmpty()) {
            node.setStyle(style);
        }

        String imageAttribute = getAttribute(line, "image");
        if (imageAttribute == null || imageAttribute.isEmpty()) {
            node.setImage(new Image(Objects.requireNonNull(getClass().getResource(BuilderController.DEFAULT_IMAGE)).toExternalForm()));
        } else {
            try {
                node.setImage(new Image(imageAttribute));
            } catch (Exception _) {
                node.setImage(new Image(Objects.requireNonNull(getClass().getResource(BuilderController.DEFAULT_IMAGE)).toExternalForm()));
            }
        }

        String fxId = getAttribute(line, "fx:id");
        if (fxId != null && !fxId.isEmpty()) {
            if (fxId.endsWith("ImageView")) fxId = fxId.substring(0, fxId.length() - "ImageView".length());
            node.setId(fxId);
        }
    }

    private void setPaneAttributes(Pane node, String line) {
        Double x = getDoubleAttribute(line, "layoutX");
        Double y = getDoubleAttribute(line, "layoutY");
        Double width = getDoubleAttribute(line, "prefWidth");
        Double height = getDoubleAttribute(line, "prefHeight");

        if (x != null) {
            AnchorPane.setLeftAnchor(node, x);
        }

        if (y != null) {
            AnchorPane.setTopAnchor(node, y);
        }

        if (width != null) {
            node.setPrefWidth(width);
        }

        if (height != null) {
            node.setPrefHeight(height);
        }

        String style = getAttribute(line, "style");
        if (style != null && !style.isEmpty()) {
            node.setStyle(style);
        }
    }

    private String getAttribute(String line, String attribute) {
        String regex = attribute + "\\s*=\\s*['\"]([^'\"]*)['\"]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private Double getDoubleAttribute(String line, String attr) {
        String value = getAttribute(line, attr);
        if (value == null) {
            return null;
        }

        try {
            return Double.parseDouble(value.replace(',', '.'));
        } catch (Exception _) {
            return null;
        }
    }

    private String escapeXML(String text) {
        if (text == null) {
            return null;
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
