package swd.team11.coviddatabase.user.application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import swd.team11.coviddatabase.VaccinationInfo;
import swd.team11.coviddatabase.user.application.Account;
import swd.team11.coviddatabase.utils.Packet;
import swd.team11.coviddatabase.utils.PacketType;

import java.io.IOException;
import java.sql.Date;

public class AddVaccineController {

    @FXML
    private TextField vaccineNameField;

    @FXML
    private TextField vaccineDateField;

    @FXML
    private TextField vaccineDistributorField;

    @FXML
    private Label alertLabel;

    @FXML
    private Button addVaccineButton;

    @FXML
    private Button backButton;

    @FXML
    void addVaccine(ActionEvent event) {
        Account account = (Account)((Stage)addVaccineButton.getScene().getWindow()).getUserData();
        VaccinationInfo vacInfo = new VaccinationInfo(account.getUserAccount().getUsername(), Date.valueOf(vaccineDateField.getText()), vaccineNameField.getText(), vaccineDistributorField.getText());
        try {
            account.getClient().sendPacket(new Packet(PacketType.SEND_VACCINATION_INFO, vacInfo));
            Packet result = account.getClient().awaitPacket();
            if (result.getType().equals(PacketType.ERROR)) {
                displayAlert((String)result.getData());
            } else if (result.getType().equals(PacketType.SUCCESS)) {
                switchSceneToViewVaccine();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        switchSceneToViewVaccine();
    }

    public void switchSceneToViewVaccine() {
        Stage stage = (Stage)backButton.getScene().getWindow();
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

    public void displayAlert(String msg) {
        alertLabel.setVisible(true);
        alertLabel.setText(msg);
    }
}
