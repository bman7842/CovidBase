package swd.team11.coviddatabase.user.application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import swd.team11.coviddatabase.AccountType;
import swd.team11.coviddatabase.UserAccount;
import swd.team11.coviddatabase.user.application.Account;
import swd.team11.coviddatabase.utils.Packet;
import swd.team11.coviddatabase.utils.PacketType;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;

public class CreateAccountController {

    @FXML
    private ChoiceBox<AccountType> accountTypeSelector;

    @FXML
    private Button createButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField fullnameField;

    @FXML
    private TextField birthdayField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField confirmPasswordField;

    @FXML
    private Label alertLabel;





    public void setup() {
        accountTypeSelector.getItems().setAll(AccountType.values());
    }

    @FXML
    // creates account for user with username, full name, birthday, password, confirm password
    public void createAccount(ActionEvent event) throws ParseException {
        Account account = (Account)((Stage)createButton.getScene().getWindow()).getUserData();
        String outputMessage = "Problems with validation:";
        String format = "^\\d{4}-\\d{2}-\\d{2}$";
        boolean validAccountData = true;
        if (usernameField.getText().length() > UserAccount.maxUserNameLength) {
            outputMessage = outputMessage + "\n" + "Your username has exceeded the maximum length of 20.";
            validAccountData = false;
        }
        if (fullnameField.getText().length() > UserAccount.maxNameLength) {
            outputMessage = outputMessage + "\n" + "Your name has exceeded the maximum length of 40.";
            validAccountData = false;
        }
        if (passwordField.getText().length() > UserAccount.maxPasswordLength) {
            outputMessage = outputMessage + "\n" + "Your password has exceeded the maximum length of 20.";
            validAccountData = false;
        }
        if (!birthdayField.getText().matches(format)) {
            outputMessage = outputMessage + "\n" + "Your date must be in YYYY-MM-DD format.";
            validAccountData = false;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())){
            outputMessage = outputMessage + "\n" + "Your passwords must match.";
            validAccountData = false;

        }if (!validAccountData) {
            displayAlert(outputMessage);
        }
        if (validAccountData) {
            UserAccount userAccount = new UserAccount(accountTypeSelector.getValue(), usernameField.getText(), fullnameField.getText(), Date.valueOf(birthdayField.getText()));
            Packet packet = account.attemptCreateAccount(userAccount, passwordField.getText());
            if (packet.getType().equals(PacketType.ERROR)) {
                displayAlert((String)packet.getData());
            } else if (packet.getType().equals(PacketType.SUCCESS)) {

                System.out.println("account created");
                account.setUserAccount(userAccount);
                switchScene(account);

            }

        }
    }

    public void switchScene(Account account) {
        if (account.getUserAccount().getType().equals(AccountType.INDIVIDUAL)) {
            switchToIndividualScene();
        } else if (account.getUserAccount().getType().equals(AccountType.ORGANIZATION)) {
            switchToOrganizationScene();
        }
    }

    public void switchToOrganizationScene() {
        Stage stage = (Stage)createButton.getScene().getWindow();
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
        Stage stage = (Stage)createButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/swd/team11/coviddatabase/user/application/scenes/UserViewVaccines.fxml"));

            Scene userViewVaccine = new Scene(loader.load());
            stage.setScene(userViewVaccine);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayAlert(String msg) {
        alertLabel.setVisible(true);
        alertLabel.setText(msg);
    }


}
