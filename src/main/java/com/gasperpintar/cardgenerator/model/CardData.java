package com.gasperpintar.cardgenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CardData {

    private List<String> columns;

    public CardData(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "CardData{" +
                "columns=" + columns +
                '}';
    }
}
