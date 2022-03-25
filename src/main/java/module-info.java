module com.example.hypera {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.hypera to javafx.fxml;
    exports com.example.hypera;
    exports domain;
    exports service;
    exports repository.database;
    opens repository;
    opens controller to javafx.fxml;
    exports controller;

    requires org.apache.pdfbox;
    requires javafx.graphics;

}