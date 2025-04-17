package com.example.unidata.controller.AdminController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.example.unidata.controller.AdminController.util.RoleData;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.sql.*;
import com.example.unidata.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;


public class RoleManagement implements Initializable {
    @FXML
    private Button btnGrantPrivileges;
    @FXML
    private Button btnRevokePrivileges;
    @FXML
    private Button btnSignOut;
    @FXML
    private Button btnUserRoleList;
    @FXML
    private Button btnViewPrivileges;

    @FXML
    private TableView<RoleData> addAccounts_tableView;
    @FXML
    private TableColumn<RoleData, Integer> addAccounts_col_id;
    @FXML
    private TableColumn<RoleData, String> addAccounts_col_username;
    @FXML
    private TableColumn<RoleData, Void> addAccounts_col_role;

    @FXML
    private TextField roleNameField; // trỏ vào TextField bạn dùng nhập tên role mới
    @FXML
    private Button createRoleButton;


    public void onSignOut(ActionEvent event) {
        try {
            Stage stage = (Stage) btnSignOut.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/unidata/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onUserRoleList(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/user_role_list.fxml", "User/Role List - ADMIN");
    }

    public void onGrantPrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/grant_privileges.fxml", "Grant Privileges - ADMIN");
    }

    public void onRevokePrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/revoke_privileges.fxml", "Revoke Privileges - ADMIN");
    }

    public void onViewPrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/view_privileges.fxml", "View Privileges - ADMIN");
    }

    public void onUserManagement(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/user_management.fxml", "User/Role Management - ADMIN");
    }


    private void loadScene(String fxmlFile, String title) {
        try {
            Stage stage = (Stage) btnSignOut.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRolesFromDB() {
        ObservableList<RoleData> roles = FXCollections.observableArrayList();
        String sql = """
            SELECT role
            FROM dba_roles
            WHERE role LIKE 'RL_%'
            ORDER BY role
        """;

        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.prepareStatement(sql).executeQuery();

            int id = 1;
            while (rs.next()) {
                String roleName = rs.getString("role");
                roles.add(new RoleData(id++, roleName));
            }

            addAccounts_tableView.setItems(roles);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTableView() {
        addAccounts_col_id.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        addAccounts_col_username.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRoleName()));

        addAccounts_col_role.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Delete");

            {
                btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btn.setOnAction(event -> {
                    RoleData role = getTableView().getItems().get(getIndex());
                    deleteRole(role.getRoleName());
                    loadRolesFromDB(); // refresh
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    // xoa role
    private void deleteRole(String roleName) {
        Alert alert;
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);

        String sql = "DROP ROLE " + roleName;
        try{
            Connection connect = DatabaseConnection.getConnection();
            connect.prepareStatement(sql).executeQuery();

            System.out.println("Role deleted: " + roleName);

            alert.setContentText("Role " + roleName + " is deleted successfully!");
            alert.showAndWait();

            loadRolesFromDB();
        } catch (SQLException e) {
            alert.setContentText("There is something wrong");
            alert.showAndWait();

            e.printStackTrace();
        }
    }
    // them role
    @FXML
    private void onCreateRole() {
        Alert alert;
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);

        String newRole = roleNameField.getText().trim();
        if (newRole.isEmpty()) return;

        String sql = "CREATE ROLE " + "RL_" + newRole;
        try{
            Connection connect = DatabaseConnection.getConnection();
            connect.prepareStatement(sql).executeQuery();
            roleNameField.clear();

            alert.setContentText("Created role " + newRole + " successfully!");
            alert.showAndWait();

            loadRolesFromDB();
        } catch (SQLException e) {
            alert.setContentText("Role " + newRole + " is existing!");
            alert.showAndWait();

            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableView();
        loadRolesFromDB();

        createRoleButton.setOnAction(event -> onCreateRole());
    }

}
