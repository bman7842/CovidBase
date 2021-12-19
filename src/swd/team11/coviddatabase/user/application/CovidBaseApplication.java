package swd.team11.coviddatabase.user.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import swd.team11.coviddatabase.user.Client;
import swd.team11.coviddatabase.events.ConnectEvent;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Main covid database user application and interface object.
 */
public class CovidBaseApplication extends Application implements ConnectEvent {

    /**
     * Database server address
     */
    private String addressName = "0.0.0.0";
    /**
     * Database server port
     */
    private int port = 23548;
    /**
     * Main app stage
     */
    private Stage appStage;

    /**
     * Launch script called when JavaFX applications starts. Loads connecting to server screen.
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        appStage = primaryStage;
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/swd/team11/coviddatabase/user/application/scenes/ServerWait.fxml"));

            Scene serverWait = new Scene(root);

            primaryStage.setTitle("Covid Database");
            primaryStage.setScene(serverWait);
            primaryStage.show();

            Client client = new Client(InetAddress.getByName(addressName), port);
            client.addConnectListener(this);
            client.start();

            //Platform.runLater(()->waitForConnection());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Listens for the client object to successfully connect to the server. Once the client object does, this listener
     * is called and the scene is switched to the sign in screen.
     * @param client    Reference to client that has connected.
     */
    @Override
    public void clientConnected(Client client) {
        System.out.println("Client conencted!");
        Platform.runLater(()->{
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/swd/team11/coviddatabase/user/application/scenes/LoginScreen.fxml"));

                Scene login = new Scene(root);
                appStage.setUserData(new Account(client)); //User's account stored when stages switch

                appStage.setScene(login);
                appStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
