package com.gasperpintar.cardgenerator.controller;

import com.gasperpintar.cardgenerator._interface.Editor;
import com.gasperpintar.cardgenerator.service.builder.Converter;
import com.gasperpintar.cardgenerator.service.builder.DragAndDrop;
import com.gasperpintar.cardgenerator.service.builder.editor.BoxEditor;
import com.gasperpintar.cardgenerator.service.builder.editor.ImageViewEditor;
import com.gasperpintar.cardgenerator.service.builder.editor.LabelEditor;
import com.gasperpintar.cardgenerator.utils.Utils;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

public class BuilderController {

    @FXML
    private Button dragLabelButton;

    @FXML
    private Button dragImageViewButton;

    @FXML
    private Button dragBoxButton;

    @FXML
    private VBox labelEditorVBox;

    @FXML
    private TextField labelIdField;

    @FXML
    private TextField componentTextField;

    @FXML
    private TextField componentColorField;

    @FXML
    private TextField componentTextSizeField;

    @FXML
    private TextField componentTextPaddingField;

    @FXML
    private VBox ImageEditorVBox;

    @FXML
    private TextField imageIdField;

    @FXML
    private TextField imageWidthField;

    @FXML
    private TextField imageHeightField;

    @FXML
    private VBox BoxEditorVBox;

    @FXML
    private TextField boxWidthField;

    @FXML
    private TextField boxHeightField;

    @FXML
    private TextField boxBackgroundColorField;

    @FXML
    private TextField boxBorderColorField;

    @FXML
    private TextField boxBorderWidthField;

    @FXML
    private SplitPane splitPane;

    @FXML
    private AnchorPane builderPane;

    @FXML
    private StackPane cardPreviewStackPane;

    @FXML
    private AnchorPane cardPreviewRectangle;

    @FXML
    private Slider zoomSlider;

    @FXML
    private AnchorPane textPane;

    @FXML
    private TextArea fxmlTextArea;

    private final DragAndDrop dragAndDrop;
    private final Converter converter;

    private LabelEditor labelEditor;
    private BoxEditor boxEditor;
    private ImageViewEditor imageViewEditor;

    private boolean isBuilderPanelFull;
    private boolean isTextPanelFull;
    private boolean isUpdatingFromGraphics;

    public BuilderController() {
        this.dragAndDrop = new DragAndDrop();
        this.converter = new Converter();
        this.isBuilderPanelFull = false;
        this.isTextPanelFull = false;
        this.isUpdatingFromGraphics = false;
    }

    @FXML
    public void initialize() {

        if (zoomSlider != null && cardPreviewStackPane != null) {
            cardPreviewStackPane.scaleXProperty().bind(zoomSlider.valueProperty());
            cardPreviewStackPane.scaleYProperty().bind(zoomSlider.valueProperty());
        }

        if (fxmlTextArea != null && cardPreviewRectangle != null) {
            fxmlTextArea.textProperty().addListener((_, _, newValue) -> {
                if (isUpdatingFromGraphics) {
                    return;
                }
                converter.convertFXMLToGraphics(cardPreviewRectangle, newValue);
                cardPreviewRectangle.getChildren().forEach(this::enableDrag);
            });

            fxmlTextArea.focusedProperty().addListener((_, _, _) -> hideAllEditors());
            fxmlTextArea.sceneProperty().addListener((_, _, newScene) -> {
                if (newScene != null) {
                    newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                        if (!fxmlTextArea.isFocused()) {
                            return;
                        }

                        Node node = mouseEvent.getPickResult().getIntersectedNode();
                        if (node != fxmlTextArea) {
                            hideAllEditors();
                            cardPreviewRectangle.requestFocus();
                        }
                    });
                }
            });

            cardPreviewRectangle.getChildren().addListener((ListChangeListener<Node>) _ -> {
                if (isUpdatingFromGraphics) {
                    return;
                }
                updateTextAreaFromGraphics();
            });

        }

        setupDragAndDrop();
        setupNodeClickHandling();
        updatePanels();

        labelEditor = new LabelEditor(null, componentTextField, componentColorField,
                componentTextSizeField, componentTextPaddingField,
                labelIdField,
                this::updateTextAreaFromGraphics);

        boxEditor = new BoxEditor(null, boxWidthField, boxHeightField, boxBackgroundColorField,
                boxBorderColorField, boxBorderWidthField,
                this::updateTextAreaFromGraphics);

        imageViewEditor = new ImageViewEditor(null, imageWidthField, imageHeightField, imageIdField,
                this::updateTextAreaFromGraphics);
    }

    @FXML
    public void openTemplate() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Izberi template datoteko");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Template file", "*.fxml"));
        File templateFile = fileChooser.showOpenDialog(Utils.stage);

        if (templateFile != null) {
            try {
                String fxmlContent = Files.readString(templateFile.toPath());
                if (fxmlTextArea != null) {
                    fxmlTextArea.setText(fxmlContent);
                }
                converter.convertFXMLToGraphics(cardPreviewRectangle, fxmlContent);
                cardPreviewRectangle.getChildren().forEach(this::enableDrag);
            } catch (Exception _) {
                InfoPopupController.showPopup("Napaka pri odpiranju datoteke");
            }
        }
    }

    @FXML
    public void downloadTemplate() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Shrani template datoteko");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Template file", "*.fxml"));
        fileChooser.setInitialFileName("template.fxml");

        File saveFile = fileChooser.showSaveDialog(Utils.stage);

        if (saveFile != null) {
            try {
                String fxmlContent = fxmlTextArea.getText();
                Files.writeString(saveFile.toPath(), fxmlContent);
                InfoPopupController.showPopup("Template je bil uspešno shranjen");
            } catch (Exception _) {
                InfoPopupController.showPopup("Napaka pri shranjevanju datoteke");
            }
        }
    }

    @FXML
    public void openBuilderPanel() {
        togglePanel(true);
    }

    @FXML
    public void openTextPanel() {
        togglePanel(false);
    }

    private void updatePanels() {
        Map<Pane, Boolean> panelVisibility = Map.of(
                builderPane, !isTextPanelFull,
                textPane, !isBuilderPanelFull
        );

        panelVisibility.forEach((pane, visible) -> {
            pane.setVisible(visible);
            pane.setManaged(visible);
        });

        double dividerPosition = isBuilderPanelFull ? 1.0 : isTextPanelFull ? 0.0 : 0.5;
        splitPane.setVisible(true);
        splitPane.setManaged(true);
        splitPane.setDividerPositions(dividerPosition);
    }

    private void updateTextAreaFromGraphics() {
        isUpdatingFromGraphics = true;
        fxmlTextArea.setText(converter.convertGraphicsToFXML(cardPreviewRectangle));
        isUpdatingFromGraphics = false;
    }

    private void hideAllEditors() {
        labelEditorVBox.setVisible(false);
        labelEditorVBox.setManaged(false);

        BoxEditorVBox.setVisible(false);
        BoxEditorVBox.setManaged(false);

        ImageEditorVBox.setVisible(false);
        ImageEditorVBox.setManaged(false);
    }

    private void togglePanel(boolean isBuilder) {
        if (isBuilder) {
            isBuilderPanelFull = !isBuilderPanelFull;
            if (isBuilderPanelFull) {
                isTextPanelFull = false;
            }
        } else {
            isTextPanelFull = !isTextPanelFull;
            if (isTextPanelFull) {
                isBuilderPanelFull = false;
            }
        }
        updatePanels();
    }

    private void setupDragAndDrop() {
        dragAndDrop.setup(dragLabelButton, "Label");
        dragAndDrop.setup(dragImageViewButton, "ImageView");
        dragAndDrop.setup(dragBoxButton, "Box");
        dragAndDrop.setupDropTarget(cardPreviewRectangle, this::enableDrag);
    }

    private void setupNodeClickHandling() {
        if (cardPreviewRectangle != null) {
            cardPreviewRectangle.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                Node node = mouseEvent.getPickResult().getIntersectedNode();
                Label foundLabel = null;
                Pane foundBox = null;
                ImageView foundImageView = null;

                while (node != null && node != cardPreviewRectangle) {
                    if (node instanceof Label label) {
                        foundLabel = label;
                        break;
                    }

                    if (node instanceof VBox || node instanceof HBox) {
                        foundBox = (Pane) node;
                        break;
                    }

                    if (node instanceof ImageView imageView) {
                        foundImageView = imageView;
                        break;
                    }
                    node = node.getParent();
                }
                selectNode(foundLabel, labelEditorVBox, labelEditor);
                selectNode(foundBox, BoxEditorVBox, boxEditor);
                selectNode(foundImageView, ImageEditorVBox, imageViewEditor);
            });
        }
    }

    private <T extends Node> void selectNode(T node, VBox editorVBox, Editor<T> editor) {
        boolean isSelected = node != null;

        if (editorVBox != null) {
            editorVBox.setVisible(isSelected);
            editorVBox.setManaged(isSelected);
        }

        if (!isSelected) {
            return;
        }
        editor.updateNode(node);
    }

    private void enableDrag(Node node) {
        dragAndDrop.enableNodeDrag(node, _ -> {
            switch (node) {
                case Label label -> labelEditor.updateNode(label);
                case Pane pane -> boxEditor.updateNode(pane);
                case ImageView imageView -> imageViewEditor.updateNode(imageView);
                default -> {
                    return;
                }
            }
            updateTextAreaFromGraphics();
        });
    }
}
