package com.gasperpintar.cardgenerator;

import com.gasperpintar.cardgenerator.utils.Utils;
import com.gasperpintar.cardgenerator.utils.FallbackResourceBundle;
import com.gasperpintar.cardgenerator.utils.IniUtils;
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

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class CardGenerator extends Application {

    private static ResourceBundle resourceBundle;
    private static final String INI_PATH = "languages/global.ini";
    private static final String INI_SECTION = "Global";
    private static final String INI_KEY = "LanguageCode";

    @Override
    public void start(Stage primaryStage) throws IOException {
        showSplashScreen(primaryStage);
    }

    public static ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            Locale locale = getLocaleFromIni();
            try {
                ResourceBundle primary = ResourceBundle.getBundle("com.gasperpintar.Messages", locale);
                ResourceBundle fallback = ResourceBundle.getBundle("com.gasperpintar.Messages", Locale.ENGLISH);
                resourceBundle = new FallbackResourceBundle(primary, fallback, locale);
            } catch (Exception exception) {
                resourceBundle = ResourceBundle.getBundle("com.gasperpintar.Messages", Locale.ENGLISH);
            }
        }
        return resourceBundle;
    }

    public static void setResourceBundle(Locale locale) {
        try {
            setLocaleToIni(locale);
            ResourceBundle primary = ResourceBundle.getBundle("com.gasperpintar.Messages", locale);
            ResourceBundle fallback = ResourceBundle.getBundle("com.gasperpintar.Messages", Locale.ENGLISH);
            resourceBundle = new FallbackResourceBundle(primary, fallback, locale);
        } catch (Exception exception) {
            resourceBundle = ResourceBundle.getBundle("com.gasperpintar.Messages", Locale.ENGLISH);
        }
    }

    private static Locale getLocaleFromIni() {
        try {
            File iniFile = getIniFile();
            String code = IniUtils.getIniValue(iniFile, INI_SECTION, INI_KEY, null);
            if (code != null && !code.isEmpty()) {
                return Locale.forLanguageTag(code);
            }
        } catch (Exception ignored) {}
        return Locale.getDefault();
    }

    private static void setLocaleToIni(Locale locale) {
        try {
            File iniFile = getIniFile();
            IniUtils.setIniValue(iniFile, INI_SECTION, INI_KEY, locale.getLanguage());
        } catch (Exception ignored) {}
    }

    private static File getIniFile() {
        String base = System.getProperty("user.dir");
        File ini = new File(base, INI_PATH);
        if (!ini.exists()) {
            String jarPath = CardGenerator.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            File jarDir = new File(jarPath).getParentFile();
            File alt = new File(jarDir, INI_PATH);
            if (alt.exists()) return alt;
        }
        return ini;
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
            } catch (InterruptedException interruptedException) {
                throw new RuntimeException(interruptedException);
            }
        });
    }

    private void loadMainStage(Stage primaryStage, Stage splashStage, Image iconImage, VBox splashLayout) {
        try {
            FXMLLoader mainLoader = new FXMLLoader(CardGenerator.class.getResource("layout/main.fxml"));
            mainLoader.setResources(getResourceBundle());
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
