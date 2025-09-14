package com.gasperpintar.cardgenerator.controller;

import com.gasperpintar.cardgenerator.model.CardData;
import com.gasperpintar.cardgenerator.service.generator.Download;
import com.gasperpintar.cardgenerator.service.generator.Generator;
import com.gasperpintar.cardgenerator.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GeneratorController {

    @FXML
    private Button excelButton;

    @FXML
    private Button imagesButton;

    @FXML
    private Button uploadButton;

    @FXML
    public Button downloadButton;

    @FXML
    private TilePane cardsTilePane;

    @FXML
    private Label totalCardsLabel;

    @FXML
    private ComboBox<String> formatComboBox;

    @FXML
    private ComboBox<String> typeComboBox;

    private File excelFile;
    private List<File> imageFiles;

    private final Generator generator;
    private final Download download;

    public GeneratorController() {
        this.generator = new Generator();
        this.download = new Download();
    }

    @FXML
    public void initialize() {
        excelButton.setOnAction(actionEvent -> chooseExcelFile());
        imagesButton.setOnAction(actionEvent -> chooseImageFiles());
        uploadButton.setOnAction(actionEvent -> uploadCards());
        downloadButton.setOnAction(actionEvent -> downloadCards());
    }

    private void chooseExcelFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Excel file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel file", "*.xlsx", "*.xls")
        );
        excelFile = fileChooser.showOpenDialog(Utils.stage);
    }

    private void chooseImageFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose images");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        imageFiles = fileChooser.showOpenMultipleDialog(Utils.stage);
    }

    private void uploadCards() {
        if (excelFile == null) {
            return;
        }

        List<CardData> cardDataList;
        try {
            cardDataList = generator.processExcelFile(excelFile);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        if (cardDataList.isEmpty()) {
            return;
        }

        cardsTilePane.getChildren().clear();

        List<String> headers = cardDataList.getFirst().getColumns();

        for (int i = 1; i < cardDataList.size(); i++) {
            Node cardNode = generator.generateCard(cardDataList.get(i), headers, imageFiles);
            cardsTilePane.getChildren().add(cardNode);
        }
        totalCardsLabel.setText("Total number of cards: " + cardsTilePane.getChildren().size());
    }

    private void downloadCards() {
        List<Node> cards = cardsTilePane.getChildren();
        if (cards == null || cards.isEmpty()) {
            return;
        }

        String selectedFormat = formatComboBox.getValue();
        String selectedType = typeComboBox.getValue();

        if (selectedFormat == null || selectedType == null) {
            return;
        }
        download.saveCards(cards, selectedFormat, selectedType);
    }
}
