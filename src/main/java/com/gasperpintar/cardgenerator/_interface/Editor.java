package com.gasperpintar.cardgenerator._interface;

import javafx.scene.Node;

public interface Editor<T extends Node> {

    void updateNode(T node);
    void updateFields();
}
