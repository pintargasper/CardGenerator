package com.gasperpintar.cardgenerator.service.builder;

import com.gasperpintar.cardgenerator._interface.DragDropListener;
import com.gasperpintar.cardgenerator._interface.NodeDragListener;
import com.gasperpintar.cardgenerator.controller.BuilderController;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class DragAndDrop {

    public void setup(Node node, String type) {
        node.setOnDragDetected(mouseEvent -> {
            Dragboard dragboard = node.startDragAndDrop(TransferMode.COPY);
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(type);
            dragboard.setContent(clipboardContent);
            mouseEvent.consume();
        });
    }

    public void setupDropTarget(Pane targetPane, DragDropListener dragDropListener) {
        if (targetPane == null) {
            return;
        }

        targetPane.setOnDragOver(dragEvent -> {
            if (dragEvent.getGestureSource() != targetPane && dragEvent.getDragboard().hasString()) {
                dragEvent.acceptTransferModes(TransferMode.COPY);
            }
            dragEvent.consume();
        });

        targetPane.setOnDragDropped(dragEvent -> {
            boolean success = false;
            Dragboard dragboard = dragEvent.getDragboard();

            if (dragboard.hasString()) {
                String type = dragboard.getString();
                Node node = createNodeFromType(type);

                if (node != null) {
                    double x = dragEvent.getX();
                    double y = dragEvent.getY();
                    targetPane.getChildren().add(node);
                    node.setLayoutX(x);
                    node.setLayoutY(y);

                    if (targetPane instanceof AnchorPane) {
                        AnchorPane.setLeftAnchor(node, x);
                        AnchorPane.setTopAnchor(node, y);
                    }

                    if (dragDropListener != null) {
                        dragDropListener.onNodeDropped(node);
                    }
                }
                success = true;
            }
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });
    }

    public void enableNodeDrag(Node node, NodeDragListener nodeDragListener) {
        final double[] offset = new double[2];

        node.setOnMousePressed((MouseEvent mouseEvent) -> {
            offset[0] = mouseEvent.getX();
            offset[1] = mouseEvent.getY();
        });

        node.setOnMouseDragged((MouseEvent mouseEvent) -> {
            if (node.getParent() == null) {
                return;
            }

            double parentWidth = node.getParent().getLayoutBounds().getWidth();
            double parentHeight = node.getParent().getLayoutBounds().getHeight();
            double nodeWidth = node.getBoundsInParent().getWidth();
            double nodeHeight = node.getBoundsInParent().getHeight();

            double newX = mouseEvent.getX() + node.getLayoutX() - offset[0];
            double newY = mouseEvent.getY() + node.getLayoutY() - offset[1];

            double maxX = Math.max(parentWidth - nodeWidth, 0);
            double maxY = Math.max(parentHeight - nodeHeight, 0);

            newX = Math.max(0, Math.min(newX, maxX));
            newY = Math.max(0, Math.min(newY, maxY));

            AnchorPane.setLeftAnchor(node, newX);
            AnchorPane.setTopAnchor(node, newY);
            node.setLayoutX(newX);
            node.setLayoutY(newY);

            if (nodeDragListener != null) {
                nodeDragListener.onNodeDragged(node);
            }
        });
    }

    private Node createNodeFromType(String type) {
        switch (type) {
            case "Label" -> {
                Label label = new Label("Text");
                label.getStyleClass().add("card-label");
                label.setStyle("-fx-text-fill: #222; -fx-font-size: 18;");
                return label;
            } case "ImageView" -> {
                String imagePath = getClass().getResource(BuilderController.DEFAULT_IMAGE) != null
                        ? Objects.requireNonNull(getClass().getResource(BuilderController.DEFAULT_IMAGE)).toExternalForm()
                        : null;
                if (imagePath != null) {
                    ImageView imageView = new ImageView(imagePath);
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);
                    imageView.setStyle("-fx-border-color: #ff6600; -fx-border-width: 3; -fx-border-style: solid; -fx-background-color: #fff;");
                    return imageView;
                }
                return null;
            } case "Box" -> {
                VBox box = new VBox();
                box.setPrefSize(120, 60);
                box.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #888; -fx-border-width: 2; -fx-border-style: solid;");
                return box;
            } default -> {
                return null;
            }
        }
    }
}
