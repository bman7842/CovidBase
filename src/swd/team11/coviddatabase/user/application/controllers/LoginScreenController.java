package swd.team11.coviddatabase.user.application.controllers;

import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import swd.team11.coviddatabase.AccountType;
import swd.team11.coviddatabase.UserAccount;
import swd.team11.coviddatabase.user.application.Account;
import swd.team11.coviddatabase.utils.Packet;
import swd.team11.coviddatabase.utils.PacketType;

import java.io.IOException;

public class LoginScreenController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button createAccountButton;

    @FXML
    private Label alertLabel;

    @FXML
    // creates account for user with input
    void createAccount(ActionEvent event) {
        Stage stage = (Stage)loginButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/swd/team11/coviddatabase/user/application/scenes/CreateAccount.fxml"));

            Scene createAccount = new Scene(loader.load());
            stage.setScene(createAccount);
            stage.show();

            CreateAccountController controller = loader.<CreateAccountController>getController();
            controller.setup();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void login(ActionEvent event) {
        Account account = (Account)((Stage)loginButton.getScene().getWindow()).getUserData();
        Packet result = account.attemptLogin(usernameField.getText(), passwordField.getText());

        if (result.getType().equals(PacketType.SEND_USER_ACCOUNT)) {
            System.out.println("Login for " + usernameField.getText() + " was successful!");
            account.setUserAccount((UserAccount) result.getData());
            switchScene(account);
        } else if (result.getType().equals(PacketType.ERROR)) {
            displayAlert((String)result.getData());
        }
    }

    public void displayAlert(String msg) {
        alertLabel.setVisible(true);
        alertLabel.setText(msg);
    }

    public void switchScene(Account account) {
        if (account.getUserAccount().getType().equals(AccountType.INDIVIDUAL)) {
            switchToIndividualScene();
        } else if (account.getUserAccount().getType().equals(AccountType.ORGANIZATION)) {
            switchToOrganizationScene();
        }
    }

    public void switchToOrganizationScene() {
        Stage stage = (Stage)loginButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/swd/team11/coviddatabase/user/application/scenes/OrganizationPage.fxml"));

            Scene organizationScene = new Scene(loader.load());
            stage.setScene(organizationScene);
            stage.show();

            OrganizationPageController controller = loader.<OrganizationPageController>getController();
            controller.setup();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToIndividualScene() {
        Stage stage = (Stage)loginButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/swd/team11/coviddatabase/user/application/scenes/UserViewVaccines.fxml"));

            Scene userViewVaccine = new Scene(loader.load());
            stage.setScene(userViewVaccine);
            stage.show();

            UserViewVaccineController controller = loader.<UserViewVaccineController>getController();
            controller.setup();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
