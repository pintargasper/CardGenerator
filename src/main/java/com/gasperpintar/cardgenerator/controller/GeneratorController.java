package com.gasperpintar.cardgenerator.controller;

import com.gasperpintar.cardgenerator.model.CardData;
import com.gasperpintar.cardgenerator.service.generator.Download;
import com.gasperpintar.cardgenerator.service.generator.Generator;
import com.gasperpintar.cardgenerator.utils.BundleUtils;
import com.gasperpintar.cardgenerator.utils.Utils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

    private final List<Node> allCardNodes;

    public GeneratorController() {
        this.generator = new Generator();
        this.download = new Download();
        this.allCardNodes = new ArrayList<>();
    }

    @FXML
    public void initialize() {
        excelButton.setOnAction(actionEvent -> chooseExcelFile());
        imagesButton.setOnAction(actionEvent -> chooseImageFiles());
        templateButton.setOnAction(actionEvent -> chooseTemplateFile());
        uploadButton.setOnAction(actionEvent -> uploadCards());
        downloadButton.setOnAction(actionEvent -> downloadCards());

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
        totalCardsLabel.setText(BundleUtils.getString("generator.cards.total", 0));
    }

    private void chooseExcelFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(BundleUtils.getString("generator.file-chooser.excel"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel file", "*.xlsx", "*.xls"));
        excelFile = fileChooser.showOpenDialog(Utils.stage);
    }

    private void chooseImageFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(BundleUtils.getString("generator.file-chooser.images"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        imageFiles = fileChooser.showOpenMultipleDialog(Utils.stage);
    }

    private void chooseTemplateFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(BundleUtils.getString("generator.file-chooser.template"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Template file", "*.fxml"));
        templateFile = fileChooser.showOpenDialog(Utils.stage);
    }

    private void uploadCards() {

        if (excelFile == null) {
            InfoPopupController.showPopup(BundleUtils.getString("popup.info.excel-not-selected"));
            return;
        }

        if (imageFiles == null || imageFiles.isEmpty()) {
            InfoPopupController.showPopup(BundleUtils.getString("popup.info.images-not-selected"));
            return;
        }

        if (templateFile == null) {
            InfoPopupController.showPopup(BundleUtils.getString("popup.info.template-not-selected"));
            return;
        }

        clearMemory();
        allCardNodes.clear();
        quantityTextField.setDisable(true);
        uploadButton.setDisable(true);
        downloadButton.setDisable(true);

        CompletableFuture.runAsync(() -> {
            try {
                cardDataList = generator.processExcelFile(excelFile);
                if (cardDataList == null || cardDataList.isEmpty()) {
                    Platform.runLater(() -> InfoPopupController.showPopup(BundleUtils.getString("popup.info.no-data")));
                    return;
                }

                CardData firstCardData = cardDataList.getFirst();
                if (firstCardData == null || firstCardData.getColumns() == null) {
                    return;
                }
                headers = firstCardData.getColumns();

                List<CardData> cardsToGenerate = cardDataList.stream().skip(1).toList();
                List<CardData> allCards = new ArrayList<>();
                for (CardData cardData : cardsToGenerate) {
                    int quantity = getQuantityForCard(cardData);
                    for (int i = 0; i < quantity; i++) {
                        allCards.add(cardData);
                    }
                }
                Platform.runLater(() -> animateCardsAdd(allCards));

            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        });
    }

    private void animateCardsAdd(List<CardData> cardsToAdd) {
        Timeline timeline = new Timeline();
        cardsListView.getItems().clear();
        totalCardsLabel.setText(BundleUtils.getString("generator.cards.total", 0));
        allCardNodes.clear();
        uploadButton.setDisable(true);
        downloadButton.setDisable(true);
        int numRows = (int) Math.ceil(cardsToAdd.size() / (double) CARDS_PER_ROW);
        List<List<CardData>> rowDataList = new ArrayList<>();
        int cardIndex = 0;
        for (int i = 0; i < numRows; i++) {
            int cardsInThisRow = Math.min(CARDS_PER_ROW, cardsToAdd.size() - cardIndex);
            List<CardData> rowData = new ArrayList<>();
            for (int j = 0; j < cardsInThisRow; j++) {
                rowData.add(cardsToAdd.get(cardIndex));
                cardIndex++;
            }
            rowDataList.add(rowData);
        }
        AtomicInteger totalGenerated = new AtomicInteger(0);
        for (int i = 0; i < rowDataList.size(); i++) {
            final int idx = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(120 * (idx + 1)), actionEvent -> {
                List<CardData> rowData = rowDataList.get(idx);
                HBox row = new HBox(10);
                for (CardData cardData : rowData) {
                    Node cardNode = generator.generateCard(cardData, headers, templateFile, imageFiles);
                    row.getChildren().add(cardNode);
                    allCardNodes.add(cardNode);
                }
                cardsListView.getItems().add(row);
                cardsListView.scrollTo(cardsListView.getItems().size());
                totalGenerated.addAndGet(row.getChildren().size());
                totalCardsLabel.setText(BundleUtils.getString("generator.cards.total", totalGenerated.get()));
            }));
        }
        timeline.setOnFinished(actionEvent -> Platform.runLater(() -> {
            cardsListView.scrollTo(0);
            quantityTextField.setDisable(false);
            uploadButton.setDisable(false);
            downloadButton.setDisable(false);
        }));
        timeline.play();
    }

    private void downloadCards() {
        if (cardDataList == null || cardDataList.isEmpty()) {
            InfoPopupController.showPopup(BundleUtils.getString("popup.info.no-cards"));
            return;
        }

        String selectedFormat = formatComboBox.getValue();
        String selectedType = typeComboBox.getValue();

        if (selectedFormat == null || selectedType == null) {
            return;
        }

        if (allCardNodes.isEmpty()) {
            InfoPopupController.showPopup(BundleUtils.getString("popup.info.no-cards"));
            return;
        }

        LoadingPopupController.showPopup();

        Platform.runLater(() -> {
            File selectedFile = download.showSaveDialog();
            if (selectedFile == null) {
                LoadingPopupController.closePopup();
                return;
            }

            download.saveCards(
                    allCardNodes,
                    selectedFormat,
                    selectedType,
                    selectedFile,
                    (progress) -> {
                        int total = allCardNodes.size();
                        int current = (int) Math.round(progress * total);
                        LoadingPopupController.updateProgress(progress, current, total);
                    },
                    () -> {
                        LoadingPopupController.closePopup();
                        InfoPopupController.showPopup(BundleUtils.getString("popup.info.zip-created"));
                    }
            );
        });
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
            totalCardsLabel.setText(BundleUtils.getString("generator.cards.total", 0));
        });

        if (cardDataList != null) {
            cardDataList.clear();
            cardDataList = null;
        }

        allCardNodes.clear();
        Generator.clearCache();
        System.gc();
    }
}
