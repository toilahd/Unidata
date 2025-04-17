package com.example.unidata.controller.AdminController;

import com.example.unidata.DatabaseConnection;
import com.example.unidata.util.RolePrivilege;
import com.example.unidata.util.UserPrivilege;

import javafx.beans.binding.ObjectExpression;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleIntegerProperty;


import javax.management.relation.Role;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ViewPrivileges implements Initializable {
    @FXML
    private Button btnGrantPrivileges;
    @FXML
    private Button btnRevokePrivileges;
    @FXML
    private Button btnSignOut;
    @FXML
    private Button btnUserRoleList;
    @FXML
    private Button btnUserRoleManagement;

    @FXML
    private ComboBox<String> comboUserRole;
    @FXML
    private TableView<UserPrivilege> userPrivilegeTable;
    @FXML
    private TableColumn<UserPrivilege, String> colUsername, colTableName, colPrivilege;
    @FXML
    private TableColumn<UserPrivilege, Integer> colNo;
    @FXML
    private TableView<RolePrivilege> rolePrivilegeTable;
    @FXML
    private TableColumn<RolePrivilege, String> colRolename, colTableName1, colPrivilege1;
    @FXML
    private TableColumn<RolePrivilege, Integer> colNo1;
    private ObservableList<UserPrivilege> userPrivileges = FXCollections.observableArrayList();
    private ObservableList<RolePrivilege> rolePrivileges = FXCollections.observableArrayList();

    Connection con;

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

    public void onRevokePrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe1/revoke_privileges.fxml", "Revoke Privileges - ADMIN");
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

    private void loadUserPrivileges() {
        userPrivileges.clear();

        String sql = """
                SELECT 
                    u.username,
                    p.table_name,
                    p.privilege
                FROM 
                    all_users u
                JOIN 
                    dba_role_privs r
                    ON u.username = r.grantee
                JOIN 
                    dba_tab_privs p
                    ON r.granted_role = p.grantee
                WHERE 
                    u.oracle_maintained = 'N'
                    AND r.granted_role LIKE 'RL_%'
                ORDER BY 
                    u.username, p.owner, p.table_name
                """;

        try {
            con = DatabaseConnection.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            int id = 1;
            while (rs.next()) {
                userPrivileges.add(new UserPrivilege(
                        id++,
                        rs.getString("username"),
                        rs.getString("table_name"),
                        rs.getString("privilege")
                ));
            }

            userPrivilegeTable.setItems(userPrivileges);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRolePrivileges() {
        rolePrivileges.clear();

        String sql = """
            SELECT 
                p.grantee AS role_name,
                p.table_name,
                p.privilege
            FROM 
                dba_tab_privs p
            WHERE 
                p.grantee LIKE 'RL_%'
            ORDER BY 
                p.grantee, p.owner, p.table_name
            """;

        try {
            con = DatabaseConnection.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int id = 1;
            while (rs.next()) {
                rolePrivileges.add(new RolePrivilege(
                        id++,
                        rs.getString("role_name"),
                        rs.getString("table_name"),
                        rs.getString("privilege")
                ));
            }

            rolePrivilegeTable.setItems(rolePrivileges);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboUserRole.getItems().addAll("User", "Role");

        // Numbering for User Privileges Table
        colNo.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().idProperty()).asObject());
        // Numbering for Role Privileges Table
        colNo1.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().idProperty()).asObject());


        comboUserRole.setOnAction(event -> {
            String selected = comboUserRole.getValue();
            if ("User".equals(selected)) {
                userPrivilegeTable.setVisible(true);
                rolePrivilegeTable.setVisible(false);
                loadUserPrivileges();
            } else if ("Role".equals(selected)) {
                userPrivilegeTable.setVisible(false);
                rolePrivilegeTable.setVisible(true);
                loadRolePrivileges();
            }
        });

        // default load
        comboUserRole.setValue("User");
        loadUserPrivileges();

        // Set up user privilege table columns
        colUsername.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        colTableName.setCellValueFactory(cellData -> cellData.getValue().tableNameProperty());
        colPrivilege.setCellValueFactory(cellData -> cellData.getValue().privilegeProperty());
        // role privilege
        colRolename.setCellValueFactory(cellData -> cellData.getValue().roleNameProperty());
        colTableName1.setCellValueFactory(cellData -> cellData.getValue().tableNameProperty());
        colPrivilege1.setCellValueFactory(cellData -> cellData.getValue().privilegeProperty());

    }

}
