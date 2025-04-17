package com.example.unidata.controller.AdminController.util;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RolePrivilege {
    private final SimpleIntegerProperty id;
    private final StringProperty roleName;
    private final StringProperty tableName;
    private final StringProperty privilege;

    public RolePrivilege(int id, String roleName, String tableName, String privilege) {
        this.id = new SimpleIntegerProperty(id);
        this.roleName = new SimpleStringProperty(roleName != null ? roleName : "");
        this.tableName = new SimpleStringProperty(tableName != null ? tableName : "");
        this.privilege = new SimpleStringProperty(privilege != null ? privilege : "");
    }

    public int idProperty() {
        return id.get();
    }
    public StringProperty roleNameProperty() {
        return roleName;
    }

    public StringProperty tableNameProperty() {
        return tableName;
    }

    public StringProperty privilegeProperty() {
        return privilege;
    }
}
