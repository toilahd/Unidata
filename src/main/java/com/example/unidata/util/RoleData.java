package com.example.unidata.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class RoleData {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty roleName;

    public RoleData(int id, String roleName) {
        this.id = new SimpleIntegerProperty(id);
        this.roleName = new SimpleStringProperty(roleName);
    }

    public int getId() {
        return id.get();
    }

    public String getRoleName() {
        return roleName.get();
    }

    public void setRoleName(String name) {
        roleName.set(name);
    }
}
