package com.example.unidata.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class UserPrivilege {
    private  final SimpleIntegerProperty id;
    private final StringProperty username;
    private final StringProperty tableName;
    private final StringProperty privilege;

    public UserPrivilege(int id, String username, String tableName, String privilege) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username != null ? username : "");
        this.tableName = new SimpleStringProperty(tableName != null ? tableName : "");
        this.privilege = new SimpleStringProperty(privilege != null ? privilege : "");
    }

    public int idProperty() {
        return id.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty tableNameProperty() {
        return tableName;
    }

    public StringProperty privilegeProperty() {
        return privilege;
    }

}

