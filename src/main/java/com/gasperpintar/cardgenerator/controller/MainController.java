package com.gasperpintar.cardgenerator.controller;

import com.gasperpintar.cardgenerator.CardGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ComboBox<String> languageComboBox;

    private String currentPage = "layout/home.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Locale current = CardGenerator.getResourceBundle().getLocale();
        if (current != null && current.getLanguage().equals("sl")) {
            languageComboBox.getSelectionModel().select("Slovenščina");
        } else {
            languageComboBox.getSelectionModel().select("English");
        }
        languageComboBox.setOnAction(actionEvent -> onLanguageChange());
        loadInitialContent();
    }

    private void loadInitialContent() {
        try {
            loadCenterContent(currentPage);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @FXML
    private void onHomeClick() throws IOException {
        currentPage = "layout/home.fxml";
        loadCenterContent(currentPage);
    }

    @FXML
    private void onGeneratorClick() throws IOException {
        currentPage = "layout/generator.fxml";
        loadCenterContent(currentPage);
    }

    private void onLanguageChange() {
        String selected = languageComboBox.getSelectionModel().getSelectedItem();
        Locale locale = switch (selected) {
            case "Slovenščina" -> Locale.forLanguageTag("sl");
            default -> Locale.ENGLISH;
        };

        if (!locale.equals(CardGenerator.getResourceBundle().getLocale())) {
            CardGenerator.setResourceBundle(locale);
            reloadMainScene();
        }
    }

    private void reloadMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(CardGenerator.class.getResource("layout/main.fxml"));
            loader.setResources(CardGenerator.getResourceBundle());
            BorderPane newRoot = loader.load();
            Scene scene = mainBorderPane.getScene();
            if (scene != null) {
                scene.setRoot(newRoot);
                MainController newController = loader.getController();
                if (newController != null) {
                    newController.setCurrentPage(currentPage);
                }
            } else {
                System.err.println("Scene is null, cannot reload main scene.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCurrentPage(String page) {
        this.currentPage = page;
        loadInitialContent();
    }

    private void loadCenterContent(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(CardGenerator.class.getResource(fxmlPath));
        loader.setResources(CardGenerator.getResourceBundle());
        Pane newCenter = loader.load();
        mainBorderPane.setCenter(newCenter);
    }
}
