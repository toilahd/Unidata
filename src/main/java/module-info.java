module com.example.unidata {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.unidata to javafx.fxml;
    exports com.example.unidata;
}