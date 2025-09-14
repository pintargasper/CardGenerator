package com.gasperpintar.cardgenerator.controller;

import com.gasperpintar.cardgenerator.model.CardData;
import com.gasperpintar.cardgenerator.service.GeneratorService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
    public Button generateButton;

    @FXML
    private TilePane cardsTilePane;

    @FXML
    private Label totalCardsLabel;

    private File excelFile;
    private List<File> imageFiles;

    private final GeneratorService generatorService;

    public GeneratorController() {
        this.generatorService = new GeneratorService();
    }

    @FXML
    public void initialize() {
        excelButton.setOnAction(actionEvent -> chooseExcelFile());
        imagesButton.setOnAction(actionEvent -> chooseImageFiles());
        uploadButton.setOnAction(actionEvent -> uploadCards());
    }

    private void chooseExcelFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Excel file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel file", "*.xlsx", "*.xls")
        );
        excelFile = fileChooser.showOpenDialog(null);
    }

    private void chooseImageFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose images");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        imageFiles = fileChooser.showOpenMultipleDialog(null);
    }

    private void uploadCards() {
        if (excelFile == null) {
            System.out.println("Excel file not selected");
            return;
        }

        List<CardData> cardDataList;
        try {
            cardDataList = generatorService.processExcelFile(excelFile);
        } catch (IOException ioException) {
            System.out.println("Error reading Excel file: " + ioException.getMessage());
            return;
        }

        if (cardDataList.isEmpty()) {
            return;
        }

        cardsTilePane.getChildren().clear();

        List<String> headers = cardDataList.getFirst().getColumns();

        for (int i = 1; i < cardDataList.size(); i++) {
            Node cardNode = generatorService.generateCard(cardDataList.get(i), headers, imageFiles);
            cardsTilePane.getChildren().add(cardNode);
        }
        totalCardsLabel.setText("Total number of cards: " + cardsTilePane.getChildren().size());
    }
}
