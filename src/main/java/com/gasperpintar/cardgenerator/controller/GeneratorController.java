package com.gasperpintar.cardgenerator.controller;

import com.gasperpintar.cardgenerator.model.CardData;
import com.gasperpintar.cardgenerator.service.generator.Download;
import com.gasperpintar.cardgenerator.service.generator.Generator;
import com.gasperpintar.cardgenerator.utils.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

public class GeneratorController {

    @FXML
    private Button excelButton;

    @FXML
    private Button imagesButton;

    @FXML
    private Button templateButton;

    @FXML
    private Button uploadButton;

    @FXML
    public Button downloadButton;

    @FXML
    private ScrollPane cardsScrollPane;

    @FXML
    private TilePane cardsTilePane;

    @FXML
    private Label totalCardsLabel;

    @FXML
    private ComboBox<String> formatComboBox;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField quantityTextField;

    private File excelFile;
    private File templateFile;
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
        templateButton.setOnAction(actionEvent -> chooseTemplateFile());
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

    private void chooseTemplateFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Template file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Template file", "*.fxml")
        );
        templateFile = fileChooser.showOpenDialog(Utils.stage);
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
        int quantityColumnIndex = IntStream.range(0, headers.size())
                .filter(index -> headers.get(index).equalsIgnoreCase(quantityTextField.getText().trim()))
                .findFirst()
                .orElse(-1);

        new Thread(() -> cardDataList.stream().skip(1).forEach(cardData -> {
            int repeat = 1;
            if (quantityColumnIndex != -1 && quantityColumnIndex < cardData.getColumns().size()) {
                String value = cardData.getColumns().get(quantityColumnIndex).trim();
                try {
                    repeat = Math.max(1, Integer.parseInt(value));
                } catch (NumberFormatException numberFormatException) {
                    throw new RuntimeException(numberFormatException);
                }
            }

            for (int r = 0; r < repeat; r++) {
                Node cardNode = generator.generateCard(cardData, headers, templateFile, imageFiles);
                Platform.runLater(() -> {
                    cardsTilePane.getChildren().add(cardNode);
                    cardsScrollPane.setVvalue(1.0);
                    totalCardsLabel.setText("Total number of cards: " + cardsTilePane.getChildren().size());
                });
            }
        })).start();
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
