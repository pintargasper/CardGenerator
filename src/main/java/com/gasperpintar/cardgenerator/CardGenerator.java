package com.gasperpintar.cardgenerator;

import com.gasperpintar.cardgenerator.utils.Utils;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CardGenerator extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        showSplashScreen(primaryStage);
    }

    private void showSplashScreen(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CardGenerator.class.getResource("layout/splash.fxml"));
        VBox splashLayout = fxmlLoader.load();

        Image iconImage = new Image(Objects.requireNonNull(
                CardGenerator.class.getResourceAsStream("images/logo.png")));

        ProgressBar progressBar = (ProgressBar) splashLayout.lookup("#progressBar");
        ImageView logoView = (ImageView) splashLayout.lookup("#logoView");
        logoView.setImage(iconImage);

        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        splashStage.getIcons().add(iconImage);
        splashStage.setScene(new Scene(splashLayout));
        splashStage.show();

        CompletableFuture.runAsync(() -> {
            try {
                for (int i = 1; i <= 100; i++) {
                    Thread.sleep(20);
                    final double progress = i / 100.0;
                    Platform.runLater(() -> progressBar.setProgress(progress));
                }
                Platform.runLater(() -> loadMainStage(primaryStage, splashStage, iconImage, splashLayout));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void loadMainStage(Stage primaryStage, Stage splashStage, Image iconImage, VBox splashLayout) {
        try {
            FXMLLoader mainLoader = new FXMLLoader(CardGenerator.class.getResource("layout/main.fxml"));
            Scene mainScene = new Scene(mainLoader.load(), 320, 240);

            primaryStage.getIcons().add(iconImage);
            primaryStage.setTitle("Card Generator");
            primaryStage.setScene(mainScene);
            primaryStage.setMaximized(true);

            Utils.stage = primaryStage;

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), splashLayout);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(actionEvent -> {
                splashStage.close();
                primaryStage.show();
            });
            fadeOut.play();
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
