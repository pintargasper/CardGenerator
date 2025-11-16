package com.gasperpintar.cardgenerator.controller;

import com.gasperpintar.cardgenerator.CardGenerator;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingPopupController {

    @FXML
    public ProgressBar loadingProgressBar;

    private static Stage popupStage;
    private static LoadingPopupController controllerInstance;

    @FXML
    public void initialize() {

    }

    public void setProgress(double progress) {
        if (loadingProgressBar != null) {
            loadingProgressBar.setProgress(progress);
        }
    }

    public static void showPopup() {
        Platform.runLater(() -> {
            try {
                if (popupStage != null && popupStage.isShowing()) return;
                FXMLLoader loader = new FXMLLoader(CardGenerator.class.getResource("layout/loading_popup.fxml"));
                Parent root = loader.load();
                controllerInstance = loader.getController();
                popupStage = new Stage(StageStyle.UNDECORATED);
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setResizable(false);
                popupStage.setTitle("Please wait");
                popupStage.setScene(new Scene(root, 420, 180));
                popupStage.initOwner(com.gasperpintar.cardgenerator.utils.Utils.stage);
                popupStage.setOnCloseRequest(Event::consume);
                popupStage.show();
            } catch (Exception ignored) {}
        });
    }

    public static void closePopup() {
        Platform.runLater(() -> {
            if (popupStage != null) {
                popupStage.close();
                popupStage = null;
                controllerInstance = null;
            }
        });
    }

    public static void updateProgress(double progress) {
        Platform.runLater(() -> {
            if (controllerInstance != null) {
                controllerInstance.setProgress(progress);
            }
        });
    }
}
