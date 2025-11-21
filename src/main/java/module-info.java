module com.gasperpintar.cardgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.graphics;
    requires static lombok;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.io;
    requires java.desktop;
    requires java.prefs;
    requires javafx.swing;

    exports com.gasperpintar.cardgenerator;
    exports com.gasperpintar.cardgenerator.component;

    opens com.gasperpintar.cardgenerator to javafx.fxml;
    opens com.gasperpintar.cardgenerator.controller to javafx.fxml;
    opens com.gasperpintar.cardgenerator.component to javafx.fxml;
}
