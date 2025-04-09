module com.example.unidata {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires com.oracle.database.jdbc;


    opens com.example.unidata to javafx.fxml;
    exports com.example.unidata;
}