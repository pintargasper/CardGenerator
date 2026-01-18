package com.gasperpintar.cardgenerator.component;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.util.Optional;

public class ShowIf extends VBox {

    @Getter
    private String type;
    private String cardType;

    private final StringProperty conditionExpression;

    public ShowIf() {
        this.conditionExpression = new SimpleStringProperty(this, "condition");
        this.getStyleClass().add("show-if");

        conditionExpression.addListener((_, _, _) -> {
            parseTypePart();
            evaluateCondition();
        });
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
        evaluateCondition();
    }

    private void parseTypePart() {
        Optional.ofNullable(conditionExpression.get())
                .map(String::trim)
                .filter(string -> string.contains("!=") || string.contains("=="))
                .ifPresent(string -> type = string.split(string.contains("!=") ? "!=" : "==", 2)[0].trim());
    }

    private void evaluateCondition() {
        boolean shouldShow = Optional.ofNullable(conditionExpression.get())
                .filter(_ -> cardType != null)
                .map(String::trim)
                .map(string -> {
                    String operator = string.contains("!=") ? "!=" : string.contains("==") ? "==" : null;

                    if (operator == null) {
                        return false;
                    }

                    String[] parts = string.split(operator, 2);
                    String rightPart = parts[1].trim().replace("\"", "");

                    if (operator.equals("==")) {
                        return cardType.equalsIgnoreCase(rightPart);
                    } else {
                        return !cardType.equalsIgnoreCase(rightPart);
                    }
                })
                .orElse(false);
        this.setVisible(shouldShow);
        this.setManaged(shouldShow);
    }

    public String getCondition() {
        return conditionExpression.get();
    }

    public void setCondition(String conditionExpression) {
        this.conditionExpression.set(conditionExpression);
    }
}
