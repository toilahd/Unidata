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

    // student
    exports com.example.unidata.controller.StudentController;
    opens com.example.unidata.controller.StudentController to javafx.fxml;

    exports com.example.unidata.controller.StudentController.utils;
    opens com.example.unidata.controller.StudentController.utils to javafx.fxml;

    // teacher
    exports com.example.unidata.controller.TeacherController;
    opens com.example.unidata.controller.TeacherController to javafx.fxml;

    exports com.example.unidata.controller.TeacherController.util;
    opens com.example.unidata.controller.TeacherController.util to javafx.fxml;


    // NVCB (nhân viên cơ bản)
    exports com.example.unidata.controller.NVCBController;
    opens com.example.unidata.controller.NVCBController to javafx.fxml;

    // NV_PCTSV (nhân viên phòng công tác sinh viên)
    exports  com.example.unidata.controller.NVCTSVController;
    opens com.example.unidata.controller.NVCTSVController to javafx.fxml;

    // NV_PDT (nhân viên phòng đào tạo)
    exports com.example.unidata.controller.NVPDTController;
    opens com.example.unidata.controller.NVPDTController to javafx.fxml;

    exports com.example.unidata.controller.NVPDTController.utils;
    opens com.example.unidata.controller.NVPDTController.utils to javafx.fxml;

    // TRGDV (trưởng đơn vị)
    exports  com.example.unidata.controller.TRGDVController;
    opens com.example.unidata.controller.TRGDVController to javafx.fxml;

    exports com.example.unidata.controller.TRGDVController.util;
    opens com.example.unidata.controller.TRGDVController.util to javafx.fxml;

    // NV_TCHC (nhân viên phòng tổ chức hành chính)
    exports com.example.unidata.controller.NVTCHCController;
    opens com.example.unidata.controller.NVTCHCController to javafx.fxml;

    exports com.example.unidata.controller.NVTCHCController.utils;
    opens com.example.unidata.controller.NVTCHCController.utils to javafx.fxml;

    // NV_PKT (nhân viên phòng khảo thí)
    exports com.example.unidata.controller.NVPKTController;
    opens com.example.unidata.controller.NVPKTController to javafx.fxml;

    exports com.example.unidata.controller.NVPKTController.utils;
    opens com.example.unidata.controller.NVPKTController.utils to javafx.fxml;
}