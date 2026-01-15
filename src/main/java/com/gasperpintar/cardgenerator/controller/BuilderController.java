package com.gasperpintar.cardgenerator.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class BuilderController {

    @FXML
    private ComboBox<String> formatComboBox;

    @FXML
    private Button showIfButton, templateButton;

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
    private VBox cardPreviewRectangle;

    private boolean isBuilderPanelFull;
    private boolean isTextPanelFull;

    public BuilderController() {
        this.isBuilderPanelFull = false;
        this.isTextPanelFull = false;
    }

    @FXML
    public void initialize() {
        formatComboBox.setOnAction(_ -> updateCardSize());

        if (templateButton != null) templateButton.setOnAction(_ -> handleTemplateButton());
        if (showIfButton != null) showIfButton.setOnAction(_ -> handleShowIfButton());

        if (zoomSlider != null && cardPreviewStackPane != null) {
            cardPreviewStackPane.scaleXProperty().bind(zoomSlider.valueProperty());
            cardPreviewStackPane.scaleYProperty().bind(zoomSlider.valueProperty());
        }

        if (fxmlTextArea != null && cardPreviewRectangle != null) {
            fxmlTextArea.textProperty().addListener((_, _, newText) -> renderFXML(newText));
        }
        updatePanels();
    }

    private void updateCardSize() {
        //Settings settings = CardFormatUtil.setupFormat(formatComboBox.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void handleTemplateButton() {

    }

    @FXML
    private void handleShowIfButton() {

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

    private void renderFXML(String fxml) {
        cardPreviewRectangle.getChildren().clear();
        if (fxml == null || fxml.trim().isEmpty()) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fxml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            Parent node = loader.load(inputStream);
            cardPreviewRectangle.getChildren().add(node);
        } catch (Exception exception) {
            Label errorLabel = new Label(String.format("Error in FXML: %s", exception.getMessage()));
            cardPreviewRectangle.getChildren().add(errorLabel);
        }
    }
}
