module com.computernetwork.filetransferserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.computernetwork.filetransferserver to javafx.fxml;
    exports com.computernetwork.filetransferserver;
}