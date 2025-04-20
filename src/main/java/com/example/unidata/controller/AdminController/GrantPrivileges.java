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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;


public class GrantPrivileges implements Initializable {
    @FXML
    private Button btnRevokePrivileges;

    @FXML
    private Button btnSignOut;

    @FXML
    private Button btnUserRoleList;

    @FXML
    private Button btnUserRoleManagement;

    @FXML
    private Button btnViewPrivileges;

    @FXML
    private Button btnBack;

    @FXML
    private ComboBox<String> comboGrantType;

    @FXML
    private ComboBox<String> comboGrantee;

    @FXML
    private ComboBox<String> comboPrivilege;

    @FXML
    private ComboBox<String> comboObjectType;

    @FXML
    private ComboBox<String> comboObjectName;

    @FXML
    private CheckBox checkWithGrantOption;

    @FXML
    private Button btnGrant;

    @FXML
    private VBox columnsContainer;

    @FXML
    private TableView<PrivilegeData> grantedPrivileges_tableView;

    @FXML
    private TableColumn<PrivilegeData, String> col_grantee;

    @FXML
    private TableColumn<PrivilegeData, String> col_privilege;

    @FXML
    private TableColumn<PrivilegeData, String> col_grantable;

    @FXML
    private TableColumn<PrivilegeData, String> col_object;


    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;

    private List<CheckBox> columnCheckboxes = new ArrayList<>();


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

    public void onRevokePrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/revoke_privileges.fxml", "Revoke Privileges - ADMIN");
    }

    public void onViewPrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/view_privileges.fxml", "View Privileges - ADMIN");
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
    private void onGrantTypeChanged(ActionEvent event) {
        String grantType = comboGrantType.getValue();
        loadGrantees(grantType);

        // Enable/disable privilege combobox based on grant type
        if ("ROLE_TO_USER".equals(grantType)) {
            comboPrivilege.setDisable(true);
            comboObjectType.setDisable(true);
            comboObjectName.setDisable(true);
            columnsContainer.setDisable(true);
            loadRoles(); // Load roles instead of privileges
        } else {
            comboPrivilege.setDisable(false);
            comboObjectType.setDisable(false);
            comboObjectName.setDisable(false);
            columnsContainer.setDisable(false);
            loadPrivileges();
        }
    }

    @FXML
    private void onPrivilegeChanged(ActionEvent event) {
        String privilege = comboPrivilege.getValue();

        // Only enable column selection for SELECT and UPDATE privileges
        boolean columnsEnabled = "SELECT".equals(privilege) || "UPDATE".equals(privilege);
        columnsContainer.setDisable(!columnsEnabled);

        if (columnsEnabled) {
            loadColumns();
        }
    }

    @FXML
    private void onObjectTypeChanged(ActionEvent event) {
        String objectType = comboObjectType.getValue();
        loadObjectNames(objectType);
    }

    @FXML
    private void onObjectNameChanged(ActionEvent event) {
        if ("SELECT".equals(comboPrivilege.getValue()) || "UPDATE".equals(comboPrivilege.getValue())) {
            loadColumns();
        }
    }

    private void loadGrantees(String grantType) {
        try {
            connect = DatabaseConnection.getConnection();
            ObservableList<String> grantees = FXCollections.observableArrayList();

            String sql;
            if ("PRIVILEGE_TO_USER".equals(grantType) || "ROLE_TO_USER".equals(grantType)) {
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
            } else { // PRIVILEGE_TO_ROLE
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
                grantees.add(name);
            }

            comboGrantee.setItems(grantees);
            if (!grantees.isEmpty()) {
                comboGrantee.setValue(grantees.get(0));
            } else {
                comboGrantee.setValue(null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load grantees: " + e.getMessage());
        }
    }

    private void loadRoles() {
        try {
            connect = DatabaseConnection.getConnection();
            ObservableList<String> roles = FXCollections.observableArrayList();

            String sql = """
                SELECT role
                FROM dba_roles
                WHERE role LIKE 'RL_%'
                ORDER BY role
                """;

            result = connect.prepareStatement(sql).executeQuery();

            while (result.next()) {
                String role = result.getString("role");
                roles.add(role);
            }

            comboPrivilege.setItems(roles);
            if (!roles.isEmpty()) {
                comboPrivilege.setValue(roles.get(0));
            } else {
                comboPrivilege.setValue(null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load roles: " + e.getMessage());
        }
    }

    private void loadPrivileges() {
        ObservableList<String> privileges = FXCollections.observableArrayList(
                "SELECT", "INSERT", "UPDATE", "DELETE", "EXECUTE", "ALL"
        );

        comboPrivilege.setItems(privileges);
        comboPrivilege.setValue("SELECT");
    }

    private void loadObjectTypes() {
        ObservableList<String> objectTypes = FXCollections.observableArrayList(
                "TABLE", "VIEW", "PROCEDURE", "FUNCTION"
        );

        comboObjectType.setItems(objectTypes);
        comboObjectType.setValue("TABLE");

        // Load object names for the default object type
        loadObjectNames("TABLE");
    }

    private void loadObjectNames(String objectType) {
        try {
            connect = DatabaseConnection.getConnection();
            ObservableList<String> objectNames = FXCollections.observableArrayList();

            String sql;
            if ("TABLE".equals(objectType)) {
                sql = "SELECT table_name FROM user_tables ORDER BY table_name";
            } else if ("VIEW".equals(objectType)) {
                sql = "SELECT view_name FROM user_views ORDER BY view_name";
            } else if ("PROCEDURE".equals(objectType) || "FUNCTION".equals(objectType)) {
                sql = "SELECT object_name FROM user_objects WHERE object_type = ? ORDER BY object_name";
                prepare = connect.prepareStatement(sql);
                prepare.setString(1, objectType);
                result = prepare.executeQuery();
            } else {
                return;
            }

            if (result == null) {
                result = connect.prepareStatement(sql).executeQuery();
            }

            while (result.next()) {
                String name = result.getString(1);
                objectNames.add(name);
            }

            comboObjectName.setItems(objectNames);
            if (!objectNames.isEmpty()) {
                comboObjectName.setValue(objectNames.get(0));

                // If select or update is active, load columns
                if (("SELECT".equals(comboPrivilege.getValue()) || "UPDATE".equals(comboPrivilege.getValue()))
                        && ("TABLE".equals(objectType) || "VIEW".equals(objectType))) {
                    loadColumns();
                }
            } else {
                comboObjectName.setValue(null);
                columnsContainer.getChildren().clear();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load object names: " + e.getMessage());
        }
    }

    private void loadColumns() {
        String objectType = comboObjectType.getValue();
        String objectName = comboObjectName.getValue();

        // Only load columns for tables and views
        if (objectName == null || (!("TABLE".equals(objectType) || "VIEW".equals(objectType)))) {
            columnsContainer.getChildren().clear();
            return;
        }

        try {
            connect = DatabaseConnection.getConnection();
            columnCheckboxes.clear();
            columnsContainer.getChildren().clear();

            // Add "Select All" checkbox
            CheckBox selectAllCheckbox = new CheckBox("Select All Columns");
            selectAllCheckbox.setOnAction(e -> {
                boolean selected = selectAllCheckbox.isSelected();
                for (CheckBox cb : columnCheckboxes) {
                    cb.setSelected(selected);
                }
            });
            columnsContainer.getChildren().add(selectAllCheckbox);

            String sql = "SELECT column_name FROM user_tab_columns WHERE table_name = ? ORDER BY column_id";
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, objectName);
            result = prepare.executeQuery();

            // Create checkboxes in groups of 3 per row
            HBox currentRow = null;
            int columnCount = 0;

            while (result.next()) {
                if (columnCount % 3 == 0) {
                    currentRow = new HBox(10);
                    currentRow.setPadding(new Insets(5));
                    columnsContainer.getChildren().add(currentRow);
                }

                String columnName = result.getString("column_name");
                CheckBox cb = new CheckBox(columnName);
                cb.setUserData(columnName); // Store column name as user data
                columnCheckboxes.add(cb);
                currentRow.getChildren().add(cb);

                columnCount++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load columns: " + e.getMessage());
        }
    }

    @FXML
    private void onGrant(ActionEvent event) {
        String grantType = comboGrantType.getValue();
        String grantee = comboGrantee.getValue();

        if (grantee == null) {
            showAlert("Warning", "Please select a grantee.");
            return;
        }

        try {
            if ("ROLE_TO_USER".equals(grantType)) {
                String role = comboPrivilege.getValue();
                if (role == null) {
                    showAlert("Warning", "Please select a role.");
                    return;
                }

                grantRoleToUser(role, grantee);
            } else {
                String privilege = comboPrivilege.getValue();
                String objectType = comboObjectType.getValue();
                String objectName = comboObjectName.getValue();
                boolean withGrantOption = checkWithGrantOption.isSelected();

                if (privilege == null || objectType == null || objectName == null) {
                    showAlert("Warning", "Please select privilege, object type, and object name.");
                    return;
                }

                // For SELECT and UPDATE, check if any columns are selected
                if (("SELECT".equals(privilege) || "UPDATE".equals(privilege)) &&
                        ("TABLE".equals(objectType) || "VIEW".equals(objectType))) {

                    List<String> selectedColumns = new ArrayList<>();
                    for (CheckBox cb : columnCheckboxes) {
                        if (cb.isSelected()) {
                            selectedColumns.add((String) cb.getUserData());
                        }
                    }

                    if (selectedColumns.isEmpty()) {
                        showAlert("Warning", "Please select at least one column for " + privilege + " privilege.");
                        return;
                    }

                    grantColumnPrivilege(grantType, grantee, privilege, objectName, selectedColumns, withGrantOption);
                } else {
                    grantObjectPrivilege(grantType, grantee, privilege, objectType, objectName, withGrantOption);
                }
            }

            showAlert("Success", "Privilege granted successfully!");
            loadGrantedPrivileges();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to grant privilege: " + e.getMessage());
        }
    }

    private void grantRoleToUser(String role, String username) throws SQLException {
        // Validate inputs to prevent SQL injection
        if (!username.matches("[A-Za-z0-9_$#]+") || !role.matches("[A-Za-z0-9_$#]+")) {
            throw new SQLException("Invalid username or role format");
        }

        String sql = "GRANT \"" + role + "\" TO \"" + username + "\"";
        executeStatement(sql);
        System.out.println("Role " + role + " granted to user " + username);
    }

    private void grantObjectPrivilege(String grantType, String grantee, String privilege,
                                      String objectType, String objectName, boolean withGrantOption) throws SQLException {
        // Validate inputs to prevent SQL injection
        if (!grantee.matches("[A-Za-z0-9_$#]+") || !objectName.matches("[A-Za-z0-9_$#]+") ||
                !privilege.matches("[A-Za-z0-9_ ]+")) {
            throw new SQLException("Invalid input format");
        }

        StringBuilder sql = new StringBuilder();
        sql.append("GRANT ");

        if ("ALL".equals(privilege)) {
            sql.append("ALL PRIVILEGES");
        } else {
            sql.append(privilege);
        }

        sql.append(" ON \"").append(objectName).append("\" TO \"").append(grantee).append("\"");

        if (withGrantOption) {
            sql.append(" WITH GRANT OPTION");
        }

        executeStatement(sql.toString());
        System.out.println("Privilege " + privilege + " on " + objectName + " granted to " + grantee);
    }

    private void grantColumnPrivilege(String grantType, String grantee, String privilege,
                                      String objectName, List<String> columns, boolean withGrantOption) throws SQLException {
        // Validate inputs to prevent SQL injection
        if (!grantee.matches("[A-Za-z0-9_$#]+") || !objectName.matches("[A-Za-z0-9_$#]+") ||
                !privilege.matches("[A-Za-z0-9_ ]+")) {
            throw new SQLException("Invalid input format");
        }

        // Validate column names
        for (String column : columns) {
            if (!column.matches("[A-Za-z0-9_$#]+")) {
                throw new SQLException("Invalid column name format: " + column);
            }
        }

        StringBuilder sql = new StringBuilder();
        sql.append("GRANT ").append(privilege).append("(");

        // Join column names with commas
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("\"").append(columns.get(i)).append("\"");
        }

        sql.append(") ON \"").append(objectName).append("\" TO \"").append(grantee).append("\"");

        if (withGrantOption) {
            sql.append(" WITH GRANT OPTION");
        }

        executeStatement(sql.toString());
        System.out.println("Column-level " + privilege + " privilege on " + objectName + " granted to " + grantee);
    }

    private void loadGrantedPrivileges() {
        try {
            connect = DatabaseConnection.getConnection();
            ObservableList<PrivilegeData> privilegeList = FXCollections.observableArrayList();

            // Query for object privileges
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

            // Query for column privileges
            sql = """
                SELECT GRANTEE, PRIVILEGE, GRANTABLE, TABLE_NAME, COLUMN_NAME
                FROM DBA_COL_PRIVS
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
                String privilege = result.getString("PRIVILEGE") + " (Column)";
                String grantable = result.getString("GRANTABLE");
                String tableName = result.getString("TABLE_NAME") + "." + result.getString("COLUMN_NAME");

                privilegeList.add(new PrivilegeData(grantee, privilege,
                        "YES".equals(grantable) ? "Yes" : "No", tableName));
            }

            // Query for role grants
            sql = """
                SELECT GRANTEE, GRANTED_ROLE, ADMIN_OPTION
                FROM DBA_ROLE_PRIVS
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
                ORDER BY GRANTEE
                """;

            result = connect.prepareStatement(sql).executeQuery();

            while (result.next()) {
                String grantee = result.getString("GRANTEE");
                String privilege = "ROLE";
                String grantable = result.getString("ADMIN_OPTION");
                String roleName = result.getString("GRANTED_ROLE");

                privilegeList.add(new PrivilegeData(grantee, privilege,
                        "YES".equals(grantable) ? "Yes" : "No", roleName));
            }

            grantedPrivileges_tableView.setItems(privilegeList);

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
        comboGrantType.setItems(FXCollections.observableArrayList(
                "PRIVILEGE_TO_USER", "PRIVILEGE_TO_ROLE", "ROLE_TO_USER"
        ));
        comboGrantType.setValue("PRIVILEGE_TO_USER");

        // Initialize table columns
        col_grantee.setCellValueFactory(cellData -> cellData.getValue().granteeProperty());
        col_privilege.setCellValueFactory(cellData -> cellData.getValue().privilegeProperty());
        col_grantable.setCellValueFactory(cellData -> cellData.getValue().grantableProperty());
        col_object.setCellValueFactory(cellData -> cellData.getValue().objectNameProperty());

        // Load initial data
        loadGrantees("PRIVILEGE_TO_USER");
        loadPrivileges();
        loadObjectTypes();
        loadGrantedPrivileges();
    }
}
