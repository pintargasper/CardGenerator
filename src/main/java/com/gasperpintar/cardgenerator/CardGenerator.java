package com.gasperpintar.cardgenerator;

import com.gasperpintar.cardgenerator.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CardGenerator extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CardGenerator.class.getResource("layout/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        Image image = new Image(Objects.requireNonNull(CardGenerator.class.getResourceAsStream("images/logo.png")));
        stage.getIcons().add(image);

        stage.setTitle("Card Generator");
        stage.setScene(scene);

        Utils.stage = stage;

        stage.show();
    }
}
