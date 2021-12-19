package swd.team11.coviddatabase.user.application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import swd.team11.coviddatabase.GuidelinesInfo;
import swd.team11.coviddatabase.user.application.Account;
import swd.team11.coviddatabase.utils.Packet;
import swd.team11.coviddatabase.utils.PacketType;

public class OrganizationPageController {

    @FXML
    private TextField orgGuidelinesField;

    @FXML
    private Button updateGuidelinesButton;

    @FXML
    private Button lookupRecordsButton;

    @FXML
    private Label alertLabel;

    @FXML
    void lookupRecords(ActionEvent event) {

    }

    @FXML
    void updateGuidelines(ActionEvent event) {
        Account account = (Account)((Stage)updateGuidelinesButton.getScene().getWindow()).getUserData();

        Packet result = account.attemptUpdateGuidelines(account.getUserAccount().getUsername(), orgGuidelinesField.getText());
        if (result.getType().equals(PacketType.ERROR)) {
            displayAlert((String)result.getData());
        } else if (result.getType().equals(PacketType.SUCCESS)) {
            hideAlert();
        }
    }

    public void setup() {
        loadGuidelines();
    }

    public void loadGuidelines() {
        Account account = (Account)((Stage)alertLabel.getScene().getWindow()).getUserData();
        Packet result = account.attemptGetOrgGuidelines(account.getUserAccount().getUsername());

        if (result.getType().equals(PacketType.ERROR)) {
            displayAlert((String)result.getData());
        } else if (result.getType().equals(PacketType.UPDATE_GUIDELINES)) {
            orgGuidelinesField.setText(((GuidelinesInfo)result.getData()).getGuideline());
        }
    }

    public void displayAlert(String msg) {
        alertLabel.setVisible(true);
        alertLabel.setText(msg);
    }

    public void hideAlert() {
        alertLabel.setVisible(false);
    }

}
