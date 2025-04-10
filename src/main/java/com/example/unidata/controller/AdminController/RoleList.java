package com.example.unidata.controller.AdminController;

import com.example.unidata.DatabaseConnection;
import com.example.unidata.util.AccountData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class RoleList implements Initializable {
    @FXML
    private Button btnGrantPrivileges;

    @FXML
    private Button btnRevokePrivileges;

    @FXML
    private Button btnSignOut;

    @FXML
    private Button btnUserRoleManagement;

    @FXML
    private Button btnViewPrivileges;

    @FXML
    private TableView<AccountData> addAccounts_tableView;

    @FXML
    private TableColumn<AccountData, Integer> addAccounts_col_id;

    @FXML
    private TableColumn<AccountData, String> addAccounts_col_username;

    @FXML
    private TableColumn<AccountData, String> addAccounts_col_password;

    @FXML
    private TableColumn<AccountData, String> addAccounts_col_role;


    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;


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

    public void onUserRoleManagement(ActionEvent event) {
        loadScene("/com/example/unidata/user_role_management.fxml", "User/Role Management - ADMIN");
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


    public ObservableList<AccountData> addAccountListData() {

        ObservableList<AccountData> listAccounts = FXCollections.observableArrayList();

        String sql = "SELECT * FROM ACCOUNT";

        connect = DatabaseConnection.connectDb();

        try {
            AccountData AccountA;
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                AccountA = new AccountData(result.getInt("ID"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("VAITRO"));

                listAccounts.add(AccountA);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listAccounts;
    }

    private ObservableList<AccountData> addAccountsListA;

    public void addAccountsShowListData() {
        addAccountsListA = addAccountListData();

        addAccounts_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        addAccounts_col_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        addAccounts_col_password.setCellValueFactory(new PropertyValueFactory<>("password"));
        addAccounts_col_role.setCellValueFactory(new PropertyValueFactory<>("role"));

        addAccounts_tableView.setItems(addAccountsListA);

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addAccountsShowListData();
    }
}
