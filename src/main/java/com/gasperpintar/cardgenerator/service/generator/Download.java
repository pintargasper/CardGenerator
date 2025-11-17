package com.gasperpintar.cardgenerator.service.generator;

import com.gasperpintar.cardgenerator.model.Settings;
import com.gasperpintar.cardgenerator.utils.Utils;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Download {

    public void saveCards(List<Node> cards, String format, String type, File selectedFile, Consumer<Double> progressCallback, Runnable onComplete) {
        Settings settings = CardFormatUtil.setupFormat(format);
        if (settings == null) {
            if (onComplete != null) {
                Platform.runLater(onComplete);
            }
            return;
        }

        int sheetWidthPx = (int) Math.round(settings.cardWidthCm / settings.cmToInch * settings.dpi);
        int sheetHeightPx = (int) Math.round(settings.cardHeightCm / settings.cmToInch * settings.dpi);
        int cardWidthPx = sheetWidthPx / settings.numCols;
        int cardHeightPx = sheetHeightPx / settings.numRows;
        int cardsPerSheet = settings.numCols * settings.numRows;
        int numberOfSheets = (int) Math.ceil((double) cards.size() / cardsPerSheet);

        if (selectedFile == null) {
            if (onComplete != null) {
                Platform.runLater(onComplete);
            }
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                saveAsZip(cards, selectedFile, numberOfSheets,
                        cardsPerSheet, sheetWidthPx, sheetHeightPx,
                        cardWidthPx, cardHeightPx, type, settings, progressCallback
                );
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            } finally {
                if (onComplete != null) {
                    Platform.runLater(onComplete);
                }
            }
        });
    }

    public File showSaveDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP archive", "*.zip"));
        fileChooser.setInitialFileName("cards.zip");
        return fileChooser.showSaveDialog(Utils.stage);
    }

    private void saveAsZip(List<Node> cards, File zipFile, int numSheets, int cardsPerSheet,
                           int sheetWidthPx, int sheetHeightPx, int cardWidthPx, int cardHeightPx,
                           String type, Settings settings, Consumer<Double> progressCallback) throws IOException {
        int imageType = getImageType(type);
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            for (int sheetIndex = 0; sheetIndex < numSheets; sheetIndex++) {
                BufferedImage sheetImage = renderSheet(cards, sheetIndex, cardsPerSheet,
                        sheetWidthPx, sheetHeightPx, cardWidthPx, cardHeightPx, settings, imageType);
                String entryName = getEntryName(sheetIndex, type);
                zipOut.putNextEntry(new ZipEntry(entryName));

                writeImageToZip(sheetImage, type, zipOut);
                sheetImage.flush();
                zipOut.closeEntry();
                System.gc();
                if (progressCallback != null) {
                    double progress = (sheetIndex + 1) / (double) numSheets;
                    progressCallback.accept(progress);
                }
            }
        }
    }

    private int getImageType(String type) {
        if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg")) {
            return BufferedImage.TYPE_INT_RGB;
        }
        return BufferedImage.TYPE_INT_ARGB;
    }

    private String getEntryName(int sheetIndex, String type) {
        return "image_" + (sheetIndex + 1) + "." + type.toLowerCase();
    }

    private void writeImageToZip(BufferedImage image, String type, ZipOutputStream zipOut) throws IOException {
        if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg")) {
            writeJpgToZip(image, zipOut);
        } else if (type.equalsIgnoreCase("png")) {
            ImageIO.write(image, "png", zipOut);
        }
    }

    private void writeJpgToZip(BufferedImage image, ZipOutputStream zipOut) throws IOException {
        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(zipOut);
        jpgWriter.setOutput(ios);
        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
        if (jpgWriteParam.canWriteCompressed()) {
            jpgWriteParam.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(0.9f);
        }
        jpgWriter.write(null, new javax.imageio.IIOImage(image, null, null), jpgWriteParam);
        ios.flush();
        jpgWriter.dispose();
        ios.close();
    }

    private BufferedImage renderSheet(List<Node> cards, int sheetIndex, int cardsPerSheet,
                                      int sheetWidthPx, int sheetHeightPx, int cardWidthPx, int cardHeightPx,
                                      Settings settings, int imageType) {
        BufferedImage sheetImage = new BufferedImage(sheetWidthPx, sheetHeightPx, imageType);
        Graphics2D graphics = sheetImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        for (int i = 0; i < cardsPerSheet; i++) {
            int cardIndex = sheetIndex * cardsPerSheet + i;
            if (cardIndex >= cards.size()) break;

            Node cardNode = cards.get(cardIndex);
            BufferedImage bufferedCard = snapshotNode(cardNode, settings.scaleFactor);
            BufferedImage scaledCard = new BufferedImage(cardWidthPx, cardHeightPx, imageType);
            Graphics2D graphics2D = scaledCard.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.drawImage(bufferedCard, 0, 0, cardWidthPx, cardHeightPx, null);
            graphics2D.dispose();

            int x = (i % settings.numCols) * cardWidthPx;
            int y = (i / settings.numCols) * cardHeightPx;
            graphics.drawImage(scaledCard, x, y, null);
        }
        graphics.dispose();
        return sheetImage;
    }

    private BufferedImage snapshotNode(Node node, double scaleFactor) {
        final WritableImage[] writableImages = new WritableImage[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Rectangle rectangle = new Rectangle(node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight());
            node.setClip(rectangle);

            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setTransform(javafx.scene.transform.Transform.scale(scaleFactor, scaleFactor));
            snapshotParameters.setDepthBuffer(true);

            writableImages[0] = node.snapshot(snapshotParameters, null);
            node.setClip(null);

            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException interruptedException) {
            throw new RuntimeException(interruptedException);
        }
        return SwingFXUtils.fromFXImage(writableImages[0], null);
    }
}
