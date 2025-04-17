package com.example.unidata.controller.AdminController.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PrivilegeData {
    private final StringProperty grantee;
    private final StringProperty privilege;
    private final StringProperty grantable;
    private final StringProperty objectName;

    public PrivilegeData(String grantee, String privilege, String grantable, String objectName) {
        this.grantee = new SimpleStringProperty(grantee);
        this.privilege = new SimpleStringProperty(privilege);
        this.grantable = new SimpleStringProperty(grantable);
        this.objectName = new SimpleStringProperty(objectName);
    }

    public String getGrantee() {
        return grantee.get();
    }

    public StringProperty granteeProperty() {
        return grantee;
    }

    public void setGrantee(String grantee) {
        this.grantee.set(grantee);
    }

    public String getPrivilege() {
        return privilege.get();
    }

    public StringProperty privilegeProperty() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege.set(privilege);
    }

    public String getGrantable() {
        return grantable.get();
    }

    public StringProperty grantableProperty() {
        return grantable;
    }

    public void setGrantable(String grantable) {
        this.grantable.set(grantable);
    }

    public String getObjectName() {
        return objectName.get();
    }

    public StringProperty objectNameProperty() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName.set(objectName);
    }
}
