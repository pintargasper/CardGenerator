package com.gasperpintar.cardgenerator.controller;

import com.gasperpintar.cardgenerator.CardGenerator;
import com.gasperpintar.cardgenerator.utils.BundleUtils;
import com.gasperpintar.cardgenerator.utils.Utils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class InfoPopupController {

    @FXML
    public Label infoLabel;

    @FXML
    public Button closeButton;

    private static Stage popupStage;
    private static InfoPopupController controllerInstance;

    @FXML
    public void initialize() {
        if (closeButton != null) {
            closeButton.setOnAction(_ -> closePopup());
        }
    }

    public void setMessage(String message) {
        if (infoLabel != null) {
            infoLabel.setText(message);
        }
    }

    public static void showPopup(String message) {
        try {
            if (popupStage != null && popupStage.isShowing()) {
                return;
            }
            FXMLLoader loader = new FXMLLoader(CardGenerator.class.getResource("layout/info_popup.fxml"));
            Parent root = loader.load();
            controllerInstance = loader.getController();
            popupStage = new Stage(StageStyle.UNDECORATED);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setResizable(false);
            popupStage.setTitle(BundleUtils.getString("popup.info.title"));
            popupStage.setScene(new Scene(root, 420, 140));
            popupStage.initOwner(Utils.stage);
            popupStage.setOnCloseRequest(Event::consume);
            controllerInstance.setMessage(message);
            popupStage.show();
        } catch (Exception ignored) {}
    }

    public static void closePopup() {
        if (popupStage != null) {
            popupStage.close();
            popupStage = null;
            controllerInstance = null;
        }
    }
}
