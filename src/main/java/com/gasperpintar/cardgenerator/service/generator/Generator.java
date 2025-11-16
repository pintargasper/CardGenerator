package com.gasperpintar.cardgenerator.service.generator;

import com.gasperpintar.cardgenerator.component.LoadingBar;
import com.gasperpintar.cardgenerator.component.ShowIf;
import com.gasperpintar.cardgenerator.model.CardData;
import com.gasperpintar.cardgenerator.service.ExcelService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Generator {

    private static final double IMAGE_SCALE_FACTOR = 3.0;
    private static final String CARD_LOADING_ERROR = "Card loading error";
    private static final Map<String, Image> imageCache = new ConcurrentHashMap<>();

    private final ExcelService excelService;

    public Generator() {
        this.excelService = new ExcelService();
    }

    public List<CardData> processExcelFile(File file) throws IOException {
        if (file == null) {
            return List.of();
        }
        return excelService.readExcelFile(file);
    }

    public Node generateCard(CardData cardData, List<String> headers, File templateFile, List<File> imageFiles) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(templateFile.toURI().toURL());
            final Node cardNode = fxmlLoader.load();

            cardNode.lookupAll(".show-if").stream()
                    .filter(ShowIf.class::isInstance)
                    .map(ShowIf.class::cast)
                    .forEach(conditionalContainer -> {
                        String conditionKey = conditionalContainer.getType();
                        int columnIndex = -1;
                        for (int i = 0; i < headers.size(); i++) {
                            if (headers.get(i).equalsIgnoreCase(conditionKey)) {
                                columnIndex = i;
                                break;
                            }
                        }
                        if (columnIndex != -1) {
                            conditionalContainer.setCardType(cardData.getColumns().get(columnIndex));
                        }
                    });

            for (int i = 0; i < headers.size(); i++) {
                final String header = headers.get(i).toLowerCase();
                final String value = cardData.getColumns().get(i);
                updateLabel(cardNode, header, value);
                updateImageView(cardNode, header, value, imageFiles);
                updateLoadingBar(cardNode, header, value);
            }
            return cardNode;
        } catch (IOException ioException) {
            return new Label(CARD_LOADING_ERROR);
        }
    }

    private void updateLabel(Node cardNode, String header, String value) {
        Label label = (Label) cardNode.lookup("#" + header + "LabelView");
        if (label != null) {
            String currentText = label.getText();
            if (currentText == null || currentText.isBlank()) {
                label.setText(value);
            } else {
                label.setText(currentText + value);
            }
        }
    }

    private void updateImageView(Node cardNode, String header, String value, List<File> imageFiles) {
        ImageView imageView = (ImageView) cardNode.lookup("#" + header + "ImageView");
        if (imageView == null || imageFiles == null) {
            return;
        }
        findImageFile(value, imageFiles).ifPresent(imageFile -> {
            String cacheKey = imageFile.getAbsolutePath() + "-" + imageView.getFitWidth() + "x" + imageView.getFitHeight();
            Image image = imageCache.computeIfAbsent(cacheKey, key -> new Image(
                    imageFile.toURI().toString(),
                    imageView.getFitWidth() * IMAGE_SCALE_FACTOR,
                    imageView.getFitHeight() * IMAGE_SCALE_FACTOR,
                    false,
                    true,
                    false
            ));
            imageView.setImage(image);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(false);
        });
    }

    private void updateLoadingBar(Node cardNode, String header, String value) {
        LoadingBar loadingBar = (LoadingBar) cardNode.lookup("#" + header + "LoadingBar");
        if (loadingBar != null) {
            loadingBar.setTitle(capitalize(header));
            try {
                int progress = (int) Float.parseFloat(value);
                loadingBar.setProgressTitle(value);
                loadingBar.setProgress(progress);
            } catch (NumberFormatException exception) {
                loadingBar.setProgressTitle("?");
            }
        }
    }

    private Optional<File> findImageFile(String fileName, List<File> imageFiles) {
        if (imageFiles == null) return Optional.empty();
        return imageFiles.stream()
                .filter(file -> FilenameUtils.removeExtension(file.getName()).equalsIgnoreCase(fileName))
                .findFirst();
    }

    private String capitalize(String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static void clearCache() {
        imageCache.clear();
        System.gc();
    }
}
