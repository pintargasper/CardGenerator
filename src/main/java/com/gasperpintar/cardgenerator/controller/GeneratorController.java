package com.gasperpintar.cardgenerator.controller;

import com.gasperpintar.cardgenerator.model.CardData;
import com.gasperpintar.cardgenerator.service.generator.Download;
import com.gasperpintar.cardgenerator.service.generator.Generator;
import com.gasperpintar.cardgenerator.utils.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
    private Button downloadButton;

    @FXML
    private ListView<HBox> cardsListView;

    @FXML
    private Label totalCardsLabel;

    @FXML
    private ComboBox<String> formatComboBox, typeComboBox;

    @FXML
    private TextField quantityTextField;

    private File excelFile;
    private File templateFile;
    private List<File> imageFiles;

    private final Generator generator;
    private final Download download;

    private List<CardData> cardDataList;
    private List<String> headers;
    private static final int CARDS_PER_ROW = 5;

    public GeneratorController() {
        this.generator = new Generator();
        this.download = new Download();
    }

    @FXML
    public void initialize() {
        excelButton.setOnAction(event -> chooseExcelFile());
        imagesButton.setOnAction(event -> chooseImageFiles());
        templateButton.setOnAction(event -> chooseTemplateFile());
        uploadButton.setOnAction(event -> uploadCards());
        downloadButton.setOnAction(event -> downloadCards());

        cardsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(HBox hBox, boolean empty) {
                super.updateItem(hBox, empty);
                setGraphic(empty || hBox == null ? null : hBox);
                if (hBox != null) {
                    hBox.setAlignment(Pos.CENTER);
                }
                setText(null);
            }
        });
    }

    private void chooseExcelFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Excel file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel file", "*.xlsx", "*.xls"));
        excelFile = fileChooser.showOpenDialog(Utils.stage);
    }

    private void chooseImageFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose images");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        imageFiles = fileChooser.showOpenMultipleDialog(Utils.stage);
    }

    private void chooseTemplateFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Template file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Template file", "*.fxml"));
        templateFile = fileChooser.showOpenDialog(Utils.stage);
    }

    private void uploadCards() {

        if (excelFile == null) {
            return;
        }

        clearMemory();
        quantityTextField.setDisable(true);
        uploadButton.setDisable(true);

        new Thread(() -> {
            try {
                cardDataList = generator.processExcelFile(excelFile);
                if (cardDataList == null || cardDataList.isEmpty()) {
                    return;
                }

                CardData firstCardData = cardDataList.getFirst();
                if (firstCardData == null || firstCardData.getColumns() == null) {
                    return;
                }
                headers = firstCardData.getColumns();

                List<CardData> cardsToGenerate = cardDataList.stream().skip(1).toList();
                List<Node> currentRowNodes = new ArrayList<>();
                AtomicInteger totalGenerated = new AtomicInteger(0);

                for (CardData cardData : cardsToGenerate) {
                    int quantity = getQuantityForCard(cardData);
                    for (int i = 0; i < quantity; i++) {
                        Node cardNode = generator.generateCard(cardData, headers, templateFile, imageFiles);
                        currentRowNodes.add(cardNode);

                        if (currentRowNodes.size() == CARDS_PER_ROW) {
                            List<Node> rowToAdd = new ArrayList<>(currentRowNodes);
                            currentRowNodes.clear();
                            Platform.runLater(() -> {
                                HBox hBox = new HBox(10);
                                hBox.getChildren().addAll(rowToAdd);
                                cardsListView.getItems().add(hBox);
                                totalGenerated.addAndGet(rowToAdd.size());
                                totalCardsLabel.setText("Total number of cards: " + totalGenerated.get());
                            });
                        }
                    }
                }

                if (!currentRowNodes.isEmpty()) {
                    Platform.runLater(() -> {
                        HBox hBox = new HBox(10);
                        hBox.getChildren().addAll(currentRowNodes);
                        cardsListView.getItems().add(hBox);
                        totalGenerated.addAndGet(currentRowNodes.size());
                        totalCardsLabel.setText("Total number of cards: " + totalGenerated.get());
                    });
                }

            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            } finally {
                Platform.runLater(() -> {
                    quantityTextField.setDisable(false);
                    uploadButton.setDisable(false);
                });
            }
        }).start();
    }

    private void downloadCards() {
        if (cardDataList == null || cardDataList.isEmpty()) {
            return;
        }

        String selectedFormat = formatComboBox.getValue();
        String selectedType = typeComboBox.getValue();

        if (selectedFormat == null || selectedType == null) {
            return;
        }

        List<Node> allCardNodes = new ArrayList<>();
        for (HBox row : cardsListView.getItems()) {
            allCardNodes.addAll(row.getChildren());
        }

        if (allCardNodes.isEmpty()) {
            return;
        }
        download.saveCards(allCardNodes, selectedFormat, selectedType);
    }

    private int getQuantityForCard(CardData cardData) {
        if (headers == null || headers.isEmpty() || quantityTextField.getText().isBlank()) {
            return 1;
        }

        int quantityColumnIndex = IntStream.range(0, headers.size())
                .filter(i -> headers.get(i).equalsIgnoreCase(quantityTextField.getText().trim()))
                .findFirst()
                .orElse(-1);

        if (quantityColumnIndex == -1 || quantityColumnIndex >= cardData.getColumns().size()) {
            return 1;
        }

        try {
            return Math.max(1, Integer.parseInt(cardData.getColumns().get(quantityColumnIndex).trim()));
        } catch (NumberFormatException numberFormatException) {
            return 1;
        }
    }

    private void clearMemory() {
        Platform.runLater(() -> {
            cardsListView.getItems().forEach(hbox -> hbox.getChildren().forEach(node -> {
                if (node instanceof ImageView imageView) {
                    imageView.setImage(null);
                }
            }));
            cardsListView.getItems().forEach(hbox -> hbox.getChildren().clear());
            cardsListView.getItems().clear();
            totalCardsLabel.setText("Total number of cards: 0");
        });

        if (cardDataList != null) {
            cardDataList.clear();
            cardDataList = null;
        }
        Generator.clearCache();
        System.gc();
    }
}
