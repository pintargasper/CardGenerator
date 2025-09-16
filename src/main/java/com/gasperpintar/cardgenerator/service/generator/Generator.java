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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Generator {

    private final double IMAGE_SCALE_FACTOR;
    private final ExcelService excelService;

    private static Map<String, Image> imageCache;

    public Generator() {
        this.IMAGE_SCALE_FACTOR = 3.0;
        this.excelService = new ExcelService();
        imageCache = new ConcurrentHashMap<>();
    }

    public List<CardData> processExcelFile(File file) throws IOException {
        if (file == null) {
            return List.of();
        }
        return excelService.readExcelFile(file);
    }

    public Node generateCard(CardData cardData, List<String> headers, File templateFile, List<File> imageFiles) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(templateFile.toURI().toURL());
            Node cardNode = fxmlLoader.load();

            List<Node> conditionalContainers = cardNode.lookupAll(".show-if").stream().toList();

            for (Node node : conditionalContainers) {
                if (node instanceof ShowIf conditionalContainer) {
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
                }
            }

            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i).toLowerCase();
                String value = cardData.getColumns().get(i);

                updateLabel(cardNode, header, value);
                updateImageView(cardNode, header, value, imageFiles);
                updateLoadingBar(cardNode, header, value);
            }
            return cardNode;
        } catch (IOException ioException) {
            return new Label("Card loading error");
        }
    }

    private void updateLabel(Node cardNode, String header, String value) {
        Label label = (Label) cardNode.lookup("#" + header + "LabelView");
        if (label != null) {
            String currentText = label.getText();
            label.setText((currentText == null || currentText.isBlank()) ? value : currentText + value);
        }
    }

    private void updateImageView(Node cardNode, String header, String value, List<File> imageFiles) {
        ImageView imageView = (ImageView) cardNode.lookup("#" + header + "ImageView");
        if (imageView == null || imageFiles == null) {
            return;
        }

        File imageFile = findImageFile(value, imageFiles);
        if (imageFile != null) {
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
        }
    }


    private void updateLoadingBar(Node cardNode, String header, String value) {
        LoadingBar loadingBar = (LoadingBar) cardNode.lookup("#" + header + "LoadingBar");
        if (loadingBar != null) {
            loadingBar.setTitle(header.substring(0, 1).toUpperCase() + header.substring(1));
            try {
                int progress = (int) Float.parseFloat(value);
                loadingBar.setProgressTitle(value);
                loadingBar.setProgress(progress);
            } catch (NumberFormatException exception) {
                loadingBar.setProgressTitle("?");
            }
        }
    }

    private File findImageFile(String fileName, List<File> imageFiles) {
        return imageFiles.stream()
                .filter(file -> Objects.equals(FilenameUtils.removeExtension(file.getName()), fileName.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    public static void clearCache() {
        imageCache.clear();
        System.gc();
    }
}
