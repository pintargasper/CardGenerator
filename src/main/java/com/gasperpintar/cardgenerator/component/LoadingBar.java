package com.gasperpintar.cardgenerator.component;

import com.gasperpintar.cardgenerator.CardGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.io.IOException;

public class LoadingBar extends HBox {

    @FXML
    private Rectangle backgroundBar;

    @FXML
    private Rectangle progressBar;

    @FXML
    private Label titleLabel;

    @FXML
    private Label progressLabel;

    public LoadingBar() {
        FXMLLoader fxmlLoader = new FXMLLoader(CardGenerator.class.getResource("layout/component/loading-bar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public double getTitleWidth() {
        return titleLabel.getPrefWidth();
    }

    public void setTitleWidth(double width) {
        titleLabel.setMinWidth(width);
        titleLabel.setMaxWidth(width);
    }

    public double getTitleSize() {
        return titleLabel.getFont().getSize();
    }

    public void setTitleSize(double size) {
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), size));
    }

    public Paint getTitleColor() {
        return titleLabel.getTextFill();
    }

    public void setTitleColor(Paint color) {
        titleLabel.setTextFill(color);
    }

    public double getProgressWidth() {
        return backgroundBar.getWidth();
    }

    public void setProgressWidth(double width) {
        backgroundBar.setWidth(width);
    }

    public Paint getProgressColor() {
        return progressBar.getFill();
    }

    public void setProgressColor(Paint color) {
        progressBar.setFill(color);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setProgressTitle(String title) {
        progressLabel.setText(title);
    }

    public void setProgress(int progress) {
        progressBar.setWidth(backgroundBar.getWidth() * progress / 100.0);
    }
}
