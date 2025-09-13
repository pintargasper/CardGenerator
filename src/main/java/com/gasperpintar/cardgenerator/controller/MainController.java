package com.gasperpintar.cardgenerator.controller;

import com.gasperpintar.cardgenerator.CardGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private BorderPane mainBorderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadCenterContent("layout/home.fxml");
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @FXML
    private void onHomeClick() throws IOException {
        loadCenterContent("layout/home.fxml");
    }

    @FXML
    private void onGeneratorClick() throws IOException {
        loadCenterContent("layout/generator.fxml");
    }

    @FXML
    private void onCreateClick() throws IOException {
        loadCenterContent("layout/create.fxml");
    }

    private void loadCenterContent(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(CardGenerator.class.getResource(fxmlPath));
        Pane newCenter = loader.load();
        mainBorderPane.setCenter(newCenter);
    }
}
