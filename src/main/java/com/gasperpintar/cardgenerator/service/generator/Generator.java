package com.gasperpintar.cardgenerator.service.generator;

import com.gasperpintar.cardgenerator.CardGenerator;
import com.gasperpintar.cardgenerator.component.LoadingBar;
import com.gasperpintar.cardgenerator.model.CardData;
import com.gasperpintar.cardgenerator.service.ExcelService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Generator {

    private static final double IMAGE_SCALE_FACTOR = 3.0;

    private final ExcelService excelService;

    public Generator() {
        this.excelService = new ExcelService();
    }

    public List<CardData> processExcelFile(File file) throws IOException {
        if (file == null) return List.of();
        return excelService.readExcelFile(file);
    }

    public Node generateCard(CardData cardData, List<String> headers, List<File> imageFiles) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CardGenerator.class.getResource("layout/template/card.fxml"));
            Node cardNode = fxmlLoader.load();

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
            Image hiResImage = new Image(
                    imageFile.toURI().toString(),
                    imageView.getFitWidth() * IMAGE_SCALE_FACTOR,
                    imageView.getFitHeight() * IMAGE_SCALE_FACTOR,
                    false,
                    true
            );
            imageView.setImage(hiResImage);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);
            imageView.setCache(true);
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
                .filter(file -> Objects.equals(file.getName(), fileName))
                .findFirst()
                .orElse(null);
    }
}
