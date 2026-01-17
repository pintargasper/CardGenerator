package com.gasperpintar.cardgenerator.controller;

import com.gasperpintar.cardgenerator._interface.Editor;
import com.gasperpintar.cardgenerator.service.builder.Converter;
import com.gasperpintar.cardgenerator.service.builder.DragAndDrop;
import com.gasperpintar.cardgenerator.service.builder.editor.BoxEditor;
import com.gasperpintar.cardgenerator.service.builder.editor.ImageViewEditor;
import com.gasperpintar.cardgenerator.service.builder.editor.LabelEditor;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.Map;

public class BuilderController {

    @FXML
    private Button templateButton;

    @FXML
    private SplitPane splitPane;

    @FXML
    private AnchorPane builderPane, textPane;

    @FXML
    private StackPane cardPreviewStackPane;

    @FXML
    private Slider zoomSlider;

    @FXML
    private TextArea fxmlTextArea;

    @FXML
    private AnchorPane cardPreviewRectangle;

    @FXML
    private Button dragLabelButton, dragImageViewButton, dragBoxButton;

    @FXML
    private VBox labelEditorVBox;

    @FXML
    private TextField componentTextField;

    @FXML
    private TextField componentColorField;

    @FXML
    private TextField componentTextSizeField;

    @FXML
    private TextField componentTextPaddingField;

    @FXML
    private VBox BoxEditorVBox;

    @FXML
    private TextField boxWidthField;

    @FXML
    private TextField boxHeightField;

    @FXML
    private TextField boxBgColorField;

    @FXML
    private TextField boxBorderColorField;

    @FXML
    private TextField boxBorderWidthField;

    @FXML
    private TextField imageWidthField;

    @FXML
    private TextField imageHeightField;

    private boolean isBuilderPanelFull;
    private boolean isTextPanelFull;

    @FXML
    private VBox ImageEditorVBox;

    public static final String DEFAULT_IMAGE = "/com/gasperpintar/cardgenerator/images/logo.png";

    @FXML
    private TextField labelIdField;

    @FXML
    private TextField imageIdField;

    private final DragAndDrop dragAndDrop;
    private final Converter converter;

    private LabelEditor labelEditor;
    private BoxEditor boxEditor;
    private ImageViewEditor imageViewEditor;

    public BuilderController() {
        this.dragAndDrop = new DragAndDrop();
        this.converter = new Converter();

        this.isBuilderPanelFull = false;
        this.isTextPanelFull = false;
    }

    @FXML
    public void initialize() {

        if (templateButton != null) {
            templateButton.setOnAction(_ -> handleTemplateButton());
        }

        if (zoomSlider != null && cardPreviewStackPane != null) {
            cardPreviewStackPane.scaleXProperty().bind(zoomSlider.valueProperty());
            cardPreviewStackPane.scaleYProperty().bind(zoomSlider.valueProperty());
        }

        if (fxmlTextArea != null && cardPreviewRectangle != null) {
            cardPreviewRectangle.getChildren().addListener((ListChangeListener<Node>) _ -> converter.convertGraphicsToFXML(cardPreviewRectangle, fxmlTextArea));
        }

        setupDragAndDrop();
        setupNodeClickHandling();
        updatePanels();

        labelEditor = new LabelEditor(null, componentTextField, componentColorField,
                componentTextSizeField, componentTextPaddingField,
                labelIdField,
                () -> converter.convertGraphicsToFXML(cardPreviewRectangle, fxmlTextArea));

        boxEditor = new BoxEditor(null, boxWidthField, boxHeightField, boxBgColorField,
                boxBorderColorField, boxBorderWidthField,
                () -> converter.convertGraphicsToFXML(cardPreviewRectangle, fxmlTextArea));

        imageViewEditor = new ImageViewEditor(null, imageWidthField, imageHeightField, imageIdField,
                () -> converter.convertGraphicsToFXML(cardPreviewRectangle, fxmlTextArea));
    }

    @FXML
    private void handleTemplateButton() {

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

    @FXML
    public void openBuilderPanel() {
        togglePanel(true);
    }

    @FXML
    public void openTextPanel() {
        togglePanel(false);
    }

    private void togglePanel(boolean isBuilder) {
        if (isBuilder) {
            isBuilderPanelFull = !isBuilderPanelFull;
            if (isBuilderPanelFull) isTextPanelFull = false;
        } else {
            isTextPanelFull = !isTextPanelFull;
            if (isTextPanelFull) isBuilderPanelFull = false;
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
            converter.convertGraphicsToFXML(cardPreviewRectangle, fxmlTextArea);
        });
    }
}
