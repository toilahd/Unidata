package com.example.unidata.controller.AdminController;

import com.example.unidata.DatabaseConnection;
import com.example.unidata.controller.AdminController.util.PrivilegeData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class RevokePrivileges implements Initializable {
    @FXML
    private Button btnSignOut;

    @FXML
    private Button btnBack;

    @FXML
    private ComboBox<String> comboTargetType;

    @FXML
    private ComboBox<String> comboTargetName;

    @FXML
    private ComboBox<String> comboPrivilege;

    @FXML
    private CheckBox checkWithGrantOption;

    @FXML
    private Button btnRevoke;

    @FXML
    private TableView<PrivilegeData> revokePrivileges_tableView;

    @FXML
    private TableColumn<PrivilegeData, String> col_grantee;

    @FXML
    private TableColumn<PrivilegeData, String> col_privilege;

    @FXML
    private TableColumn<PrivilegeData, String> col_grantable;

    @FXML
    private TableColumn<PrivilegeData, String> col_object;

    // db connection
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;

    public void onSignOut(ActionEvent event) {
        try {
            DatabaseConnection.logout();
            Stage stage = (Stage) btnSignOut.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/unidata/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onUserRoleManagement(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/user_management.fxml", "User/Role Management - ADMIN");
    }

    public void onUserRoleList(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/user_role_list.fxml", "User/Role List - ADMIN");
    }

    public void onGrantPrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/grant_privileges.fxml", "Grant Privileges - ADMIN");
    }

    public void onViewPrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/view_privileges.fxml", "View Privileges - ADMIN");
    }

    public void onBack(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/user_management.fxml", "User Management - ADMIN");
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

    @FXML
    private void onTargetTypeChanged(ActionEvent event) {
        String targetType = comboTargetType.getValue();
        loadTargetNames(targetType);
    }

    private void loadTargetNames(String targetType) {
        try {
            connect = DatabaseConnection.getConnection();
            ObservableList<String> targetNames = FXCollections.observableArrayList();

            String sql;
            if ("USER".equals(targetType)) {
                sql = """
                    SELECT username
                    FROM dba_users
                    WHERE account_status = 'OPEN'
                    AND (
                        username LIKE 'DBA_%' OR
                        username LIKE 'GV_%' OR
                        username LIKE 'NVCB_%' OR
                        username LIKE 'NVCTSV_%' OR
                        username LIKE 'NVPDT_%' OR
                        username LIKE 'NVPKT_%' OR
                        username LIKE 'NVTCHC_%' OR
                        username LIKE 'SV_%' OR
                        username LIKE 'TRGDV_%'
                    )
                    ORDER BY username
                    """;
            } else { // ROLE
                sql = """
                    SELECT role
                    FROM dba_roles
                    WHERE role LIKE 'RL_%'
                    ORDER BY role
                    """;
            }

            result = connect.prepareStatement(sql).executeQuery();

            while (result.next()) {
                String name = result.getString(1);
                targetNames.add(name);
            }

            comboTargetName.setItems(targetNames);
            if (!targetNames.isEmpty()) {
                comboTargetName.setValue(targetNames.get(0));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load " + targetType + " names: " + e.getMessage());
        }
    }

    private void loadPrivileges() {
        try {
            connect = DatabaseConnection.getConnection();
            ObservableList<String> privileges = FXCollections.observableArrayList(
                    "SELECT", "INSERT", "UPDATE", "DELETE", "EXECUTE", "CREATE SESSION",
                    "CREATE TABLE", "CREATE VIEW", "CREATE PROCEDURE", "CREATE SEQUENCE",
                    "CREATE TRIGGER", "CREATE ANY TABLE", "DROP ANY TABLE", "ALL PRIVILEGES"
            );

            comboPrivilege.setItems(privileges);
            if (!privileges.isEmpty()) {
                comboPrivilege.setValue(privileges.get(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load privileges: " + e.getMessage());
        }
    }

    @FXML
    private void onRevoke(ActionEvent event) {
        String targetType = comboTargetType.getValue();
        String targetName = comboTargetName.getValue();
        String privilege = comboPrivilege.getValue();
        boolean withGrantOption = checkWithGrantOption.isSelected();

        if (targetType == null || targetName == null || privilege == null) {
            showAlert("Warning", "Please select target type, target name, and privilege.");
            return;
        }

        try {
            revokePrivilege(targetType, targetName, privilege, withGrantOption);
            showAlert("Success", "Successfully revoked " + privilege + " from " + targetName);
            loadGrantedPrivileges();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to revoke privilege: " + e.getMessage());
        }
    }

    private void revokePrivilege(String targetType, String targetName, String privilege, boolean withGrantOption) throws SQLException {
        // Validate inputs to prevent SQL injection
        if (!targetName.matches("[A-Za-z0-9_$#]+")) {
            throw new SQLException("Invalid target name format");
        }

        if (!privilege.matches("[A-Za-z0-9_ ]+")) {
            throw new SQLException("Invalid privilege format");
        }

        StringBuilder sql = new StringBuilder();
        sql.append("REVOKE ");

        if ("ALL PRIVILEGES".equals(privilege)) {
            sql.append("ALL PRIVILEGES");
        } else {
            sql.append(privilege);
        }

        sql.append(" FROM \"").append(targetName).append("\"");

        if (withGrantOption) {
            sql.append(" CASCADE CONSTRAINTS");
        }

        executeStatement(sql.toString());
        System.out.println("Privilege " + privilege + " revoked from " + targetName);
    }

    private void loadGrantedPrivileges() {
        try {
            connect = DatabaseConnection.getConnection();
            ObservableList<PrivilegeData> privilegeList = FXCollections.observableArrayList();

            String sql = """
                SELECT GRANTEE, PRIVILEGE, GRANTABLE, TABLE_NAME
                FROM DBA_TAB_PRIVS
                WHERE (
                    GRANTEE LIKE 'DBA_%' OR
                    GRANTEE LIKE 'GV_%' OR
                    GRANTEE LIKE 'NVCB_%' OR
                    GRANTEE LIKE 'NVCTSV_%' OR
                    GRANTEE LIKE 'NVPDT_%' OR
                    GRANTEE LIKE 'NVPKT_%' OR
                    GRANTEE LIKE 'NVTCHC_%' OR
                    GRANTEE LIKE 'SV_%' OR
                    GRANTEE LIKE 'TRGDV_%'
                )
                ORDER BY GRANTEE, PRIVILEGE
                """;

            result = connect.prepareStatement(sql).executeQuery();

            while (result.next()) {
                String grantee = result.getString("GRANTEE");
                String privilege = result.getString("PRIVILEGE");
                String grantable = result.getString("GRANTABLE");
                String tableName = result.getString("TABLE_NAME");

                privilegeList.add(new PrivilegeData(grantee, privilege,
                        "YES".equals(grantable) ? "Yes" : "No", tableName));
            }

            revokePrivileges_tableView.setItems(privilegeList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load granted privileges: " + e.getMessage());
        }
    }

    // method to execute SQL statements
    private void executeStatement(String sql) throws SQLException {
        connect = DatabaseConnection.getConnection();
        try (Statement stmt = connect.createStatement()) {
            stmt.execute(sql);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize combobox values
        comboTargetType.setItems(FXCollections.observableArrayList("USER", "ROLE"));
        comboTargetType.setValue("USER");

        // Initialize table columns
        col_grantee.setCellValueFactory(cellData -> cellData.getValue().granteeProperty());
        col_privilege.setCellValueFactory(cellData -> cellData.getValue().privilegeProperty());
        col_grantable.setCellValueFactory(cellData -> cellData.getValue().grantableProperty());
        col_object.setCellValueFactory(cellData -> cellData.getValue().objectNameProperty());

        // Load initial data
        loadTargetNames("USER");
        loadPrivileges();
        loadGrantedPrivileges();
    }
}