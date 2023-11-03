module com.computernetwork.filetransferserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.computernetwork.filetransferserver to javafx.fxml;
    exports com.computernetwork.filetransferserver;
}