package swd.team11.coviddatabase.user.application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import swd.team11.coviddatabase.VaccinationInfo;
import swd.team11.coviddatabase.user.application.Account;
import swd.team11.coviddatabase.utils.Packet;
import swd.team11.coviddatabase.utils.PacketType;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public class UserViewVaccineController {

    @FXML
    private VBox vaccineInfoBox;

    @FXML
    private Button addVaccineButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Label alertLabel;

    @FXML
    private Button lookupGuidelinesButton;

    @FXML
    void addVaccineInfo(ActionEvent event) {
        showAddVaccineScene();
    }

    @FXML
    void refreshVaccineData(ActionEvent event) {
        refreshVaccineInfoBox();
    }

    @FXML
    void lookupGuidelines(ActionEvent event) {
        //lookup organization guidelines
    }

    public void setup() {
        refreshVaccineInfoBox();
    }

    public void showAddVaccineScene() {
        Stage stage = (Stage)addVaccineButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/swd/team11/coviddatabase/user/application/scenes/UserAddVaccine.fxml"));

            Scene userAddVaccine = new Scene(loader.load());
            stage.setScene(userAddVaccine);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshVaccineInfoBox() {
        Account account = (Account)((Stage)refreshButton.getScene().getWindow()).getUserData();
        Packet result = account.attemptGetVacInfos();
        if (result.getType().equals(PacketType.ERROR)) {
            displayAlert((String)result.getData());
        } else if (result.getType().equals(PacketType.SEND_VACCINATION_INFO)) {
            vaccineInfoBox.getChildren().clear();

            ArrayList<VaccinationInfo> vacData = (ArrayList<VaccinationInfo>) result.getData();
            for (VaccinationInfo vaccinationInfo : vacData) {
                vaccineInfoBox.getChildren().add(new Label(vaccinationInfo.toString()));
            }

        }
    }

    public void displayAlert(String msg) {
        alertLabel.setVisible(true);
        alertLabel.setText(msg);
    }
}
