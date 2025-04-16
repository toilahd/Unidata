package com.example.unidata.controller.AdminController;

import com.example.unidata.DatabaseConnection;
import com.example.unidata.util.UserData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class UserManagement implements Initializable {
    @FXML
    private Button btnGrantPrivileges;

    @FXML
    private Button btnRevokePrivileges;

    @FXML
    private Button btnSignOut;

    @FXML
    private Button btnUserRoleList;

    @FXML
    private Button btnCreateUser;

    @FXML
    private Button btnChangePassword;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    // db connection
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;

    @FXML
    private TableView<UserData> addAccounts_tableView;
    @FXML
    private TableColumn<UserData, String> addAccounts_col_id;
    @FXML
    private TableColumn<UserData, String> addAccounts_col_username;
    @FXML
    private TableColumn<UserData, Void> addAccounts_col_role;

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

    public void onUserRoleList(ActionEvent event) {
        loadScene("/com/example/unidata/user_role_list.fxml", "User/Role List - ADMIN");
    }

    public void onGrantPrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/grant_privileges.fxml", "Grant Privileges - ADMIN");
    }

    public void onRevokePrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/revoke_privileges.fxml", "Revoke Privileges - ADMIN");
    }

    public void onViewPrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/view_privileges.fxml", "View Privileges - ADMIN");
    }

    public void onRoleManagement(ActionEvent event) {
        loadScene("/com/example/unidata/role_management.fxml", "User/Role Management - ADMIN");
    }
    // Create user action
    @FXML
    private void onCreateUser(ActionEvent event) {
        Alert alert;
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        if (username.isEmpty() || password.isEmpty()) {

            alert.setContentText("Username or new password is empty.");
            alert.showAndWait();
            return;
        }
        try {
            createUser(username, password);
            alert.setContentText("Create " + username + " successfully!");
            alert.showAndWait();
            loadUsersCreatedByDBA(); // refresh table
        } catch (SQLException e) {
            alert.setContentText("User " + username + " is existing!");
            alert.showAndWait();
            e.printStackTrace();
        }

        txtUsername.clear();
        txtPassword.clear();
    }
    // Change password action
    @FXML
    private void onChangePassword(ActionEvent event) {
        Alert alert;
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);

        String username = txtUsername.getText().trim();
        String newPassword = txtPassword.getText();

        if (username.isEmpty() || newPassword.isEmpty()) {
            alert.setContentText("Username or new password is empty.");
            alert.showAndWait();
            return;
        }
        try {
            alterUserPassword(username, newPassword);
            alert.setContentText("Change password for " + username + " successfully!");
            alert.showAndWait();
        } catch (SQLException e) {
            alert.setContentText("Maybe user " + username + " does not exist!");
            alert.showAndWait();
            e.printStackTrace();
        }
        txtUsername.clear();
        txtPassword.clear();
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

    private void addDeleteButtonToTable() {
        addAccounts_col_role.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6;");
                deleteButton.setOnAction(event -> {
                    UserData user = getTableView().getItems().get(getIndex());
                    try {
                        // gọi hàm xóa user từ controller
                        Alert alert;
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Message");
                        alert.setHeaderText(null);
                        dropUser(user.getUsername());
                        alert.setContentText("Delete  " + user.getUsername() + " successfully!");
                        alert.showAndWait();
                        loadUsersCreatedByDBA(); // cập nhật UI
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    public void loadUsersCreatedByDBA() {
        try {
            connect = DatabaseConnection.getConnection();
            ObservableList<UserData> userList = FXCollections.observableArrayList();

            // Get all users except default/system ones
            String sql = """
                SELECT username 
                FROM dba_users 
                WHERE account_status = 'OPEN'
                AND username NOT IN (
                    'SYS', 'SYSTEM', 'OUTLN', 'XS$NULL', 'MDSYS', 'ORDSYS',
                    'CTXSYS', 'DBSNMP', 'SYSMAN', 'APEX_PUBLIC_USER',
                    'ANONYMOUS', 'XDB', 'DBA_MANAGER', 'SYSRAC'
                )
                ORDER BY username
                """;
            connect = DatabaseConnection.getConnection();
            result = connect.prepareStatement(sql).executeQuery();

            int id = 1;
            while (result.next()) {
                String username = result.getString("username");
                userList.add(new UserData(String.valueOf(id++), username));
            }

            addAccounts_tableView.setItems(userList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a new user
    public void createUser(String username, String password) throws SQLException {
        String sql = "CREATE USER " + username + " IDENTIFIED BY " + password;
        executeStatement(sql);
        System.out.println("User " + username + " created successfully");
    }

    // Delete a user
    public void dropUser(String username) throws SQLException {
        String sql = "DROP USER " + username + " CASCADE";
        executeStatement(sql);
        System.out.println("User " + username + " dropped successfully");
    }

    // Alter user password
    public void alterUserPassword(String username, String newPassword) throws SQLException {
        String sql = "ALTER USER " + username + " IDENTIFIED BY " + newPassword;
        executeStatement(sql);
        System.out.println("Password for user " + username + " changed successfully");
    }

//    // Create a new role
//    public void createRole(String roleName) throws SQLException {
//        String sql = "CREATE ROLE " + roleName;
//        executeStatement(sql);
//        System.out.println("Role " + roleName + " created successfully");
//    }

//    // Delete a role
//    public void dropRole(String roleName) throws SQLException {
//        String sql = "DROP ROLE " + roleName;
//        executeStatement(sql);
//        System.out.println("Role " + roleName + " dropped successfully");
//    }

    // method to execute SQL statements
    private void executeStatement(String sql) throws SQLException {
        connect = DatabaseConnection.getConnection();
        try (Statement stmt = connect.createStatement()) {
            stmt.execute(sql);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addAccounts_col_id.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        addAccounts_col_username.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

        addDeleteButtonToTable();
        loadUsersCreatedByDBA();
    }

}
