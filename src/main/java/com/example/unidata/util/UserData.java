package com.example.unidata.util;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserData {
    private final StringProperty id;
    private final StringProperty username;

    public UserData(String id, String username) {
        this.id = new SimpleStringProperty(id);
        this.username = new SimpleStringProperty(username);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }
}
