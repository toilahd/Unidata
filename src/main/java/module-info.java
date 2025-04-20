module com.example.unidata {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop;
    requires com.oracle.database.jdbc;
    requires javafx.base;
    requires java.management;

    opens com.example.unidata to javafx.fxml;
    exports com.example.unidata;

    // controller chính
    exports com.example.unidata.controller;
    opens com.example.unidata.controller to javafx.fxml;

    // dba controller
    exports com.example.unidata.controller.AdminController;
    opens com.example.unidata.controller.AdminController to javafx.fxml;

    exports com.example.unidata.controller.AdminController.util;
    opens com.example.unidata.controller.AdminController.util to javafx.fxml;

    // student controller
    exports com.example.unidata.controller.StudentController;
    opens com.example.unidata.controller.StudentController to javafx.fxml;

    // teacher controller
    exports com.example.unidata.controller.TeacherController;
    opens com.example.unidata.controller.TeacherController to javafx.fxml;

    exports  com.example.unidata.controller.NVCTSVController;
    opens com.example.unidata.controller.NVCTSVController to javafx.fxml;

    exports com.example.unidata.controller.TeacherController.util;
    opens com.example.unidata.controller.TeacherController.util to javafx.fxml;
    exports com.example.unidata.controller.StudentController.utils;
    opens com.example.unidata.controller.StudentController.utils to javafx.fxml;
}