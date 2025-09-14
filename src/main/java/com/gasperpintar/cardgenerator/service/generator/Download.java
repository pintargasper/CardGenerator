package com.gasperpintar.cardgenerator.service.generator;

import com.gasperpintar.cardgenerator.model.Settings;
import com.gasperpintar.cardgenerator.utils.Utils;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Download {

    public void saveCards(List<Node> cards, String format, String type) {

        Settings settings = setupFormat(format);
        if (settings == null) {
            return;
        }

        int sheetWidthPx = (int) Math.round(settings.cardWidthCm / settings.cmToInch * settings.dpi);
        int sheetHeightPx = (int) Math.round(settings.cardHeightCm / settings.cmToInch * settings.dpi);
        int cardWidthPx = sheetWidthPx / settings.numCols;
        int cardHeightPx = sheetHeightPx / settings.numRows;

        int cardsPerSheet = settings.numCols * settings.numRows;
        int numberOfSheets = (int) Math.ceil((double) cards.size() / cardsPerSheet);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");

        if ("pdf".equalsIgnoreCase(type)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF document", "*.pdf"));
        } else {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP archive", "*.zip"));
        }

        File selectedFile = fileChooser.showSaveDialog(Utils.stage);
        if (selectedFile == null) {
            return;
        }

        if (type.equalsIgnoreCase("pdf")) {
            saveAsPdf(cards, selectedFile, numberOfSheets, cardsPerSheet, sheetWidthPx, sheetHeightPx, cardWidthPx, cardHeightPx, settings);
        } else {
            saveAsZip(cards, selectedFile, numberOfSheets, cardsPerSheet, sheetWidthPx, sheetHeightPx, cardWidthPx, cardHeightPx, type, settings);
        }
    }

    private Settings setupFormat(String format) {
        Settings settings = new Settings();
        settings.cmToInch = 2.54;

        if (format.equals("13x18")) {
            settings.dpi = 300;
            settings.numCols = 2;
            settings.numRows = 2;
            settings.scaleFactor = 3.0;
            settings.cardWidthCm = 13.0;
            settings.cardHeightCm = 18.0;
        } else {
            return null;
        }
        return settings;
    }

    private void saveAsPdf(List<Node> cards, File pdfFile, int numSheets, int cardsPerSheet,
                           int sheetWidthPx, int sheetHeightPx, int cardWidthPx, int cardHeightPx, Settings settings) {
        try (PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(pdfFile));
             PdfDocument pdfDocument = new PdfDocument(pdfWriter);
             Document pdfLayout = new Document(pdfDocument)) {

            for (int sheetIndex = 0; sheetIndex < numSheets; sheetIndex++) {
                BufferedImage sheetImage = renderSheet(cards, sheetIndex, cardsPerSheet, sheetWidthPx, sheetHeightPx, cardWidthPx, cardHeightPx, settings);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(sheetImage, "png", byteArrayOutputStream);
                Image pdfImage = new Image(ImageDataFactory.create(byteArrayOutputStream.toByteArray()));
                pdfLayout.add(pdfImage);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void saveAsZip(List<Node> cards, File zipFile, int numSheets, int cardsPerSheet,
                           int sheetWidthPx, int sheetHeightPx, int cardWidthPx, int cardHeightPx,
                           String type, Settings settings) {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            for (int sheetIndex = 0; sheetIndex < numSheets; sheetIndex++) {
                BufferedImage sheetImage = renderSheet(cards, sheetIndex, cardsPerSheet, sheetWidthPx, sheetHeightPx, cardWidthPx, cardHeightPx, settings);
                String entryName = "image_" + (sheetIndex + 1) + "." + type.toLowerCase();
                zipOut.putNextEntry(new ZipEntry(entryName));

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(sheetImage, type.toLowerCase(), byteArrayOutputStream);
                zipOut.write(byteArrayOutputStream.toByteArray());
                zipOut.closeEntry();
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private BufferedImage renderSheet(List<Node> cards, int sheetIndex, int cardsPerSheet,
                                      int sheetWidthPx, int sheetHeightPx, int cardWidthPx, int cardHeightPx,
                                      Settings settings) {
        BufferedImage sheetImage = new BufferedImage(sheetWidthPx, sheetHeightPx, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = sheetImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        for (int i = 0; i < cardsPerSheet; i++) {
            int cardIndex = sheetIndex * cardsPerSheet + i;
            if (cardIndex >= cards.size()) break;

            Node cardNode = cards.get(cardIndex);

            Rectangle clip = new Rectangle(cardNode.getLayoutBounds().getWidth(), cardNode.getLayoutBounds().getHeight());
            cardNode.setClip(clip);

            SnapshotParameters snapshotParams = new SnapshotParameters();
            snapshotParams.setTransform(javafx.scene.transform.Transform.scale(settings.scaleFactor, settings.scaleFactor));
            snapshotParams.setDepthBuffer(true);

            WritableImage snapshot = cardNode.snapshot(snapshotParams, null);
            cardNode.setClip(null);

            BufferedImage bufferedCard = SwingFXUtils.fromFXImage(snapshot, null);
            BufferedImage scaledCard = new BufferedImage(cardWidthPx, cardHeightPx, BufferedImage.TYPE_INT_ARGB);

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
}
