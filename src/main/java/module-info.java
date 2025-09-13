module com.gasperpintar.cardgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    exports com.gasperpintar.cardgenerator;

    opens com.gasperpintar.cardgenerator to javafx.fxml;
    opens com.gasperpintar.cardgenerator.controller to javafx.fxml;
}
