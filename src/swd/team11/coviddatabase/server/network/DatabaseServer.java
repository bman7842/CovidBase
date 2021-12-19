package swd.team11.coviddatabase.server.network;

import javafx.util.Pair;
import swd.team11.coviddatabase.UserAccount;
import swd.team11.coviddatabase.VaccinationInfo;
import swd.team11.coviddatabase.events.MySQLQueryEvent;
import swd.team11.coviddatabase.server.mysql.MySQLConnection;
import swd.team11.coviddatabase.server.mysql.QueryProcessor;
import swd.team11.coviddatabase.server.mysql.Table;
import swd.team11.coviddatabase.utils.CircularBuffer;
import swd.team11.coviddatabase.utils.RequestType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseServer extends Thread {

    private int port;
    private Socket socket;
    private boolean active;
    private ArrayList<ClientHandler> connectedClients;
    private String mySqlURL;
    private Properties mySQLProperties;
    private CircularBuffer<Pair<MySQLQueryEvent, Pair<RequestType, String>>> queryBuffer;
    private ExecutorService queryProcessors;

    /**
     * Stores the table name with it's associated table object.
     */
    private HashMap<String, Table> tables;

    public DatabaseServer(int port) {
        this.port = port;
        socket = null;
        active = false;
        connectedClients = new ArrayList<>();
        tables = new HashMap<>();
        mySqlURL = "jdbc:mysql://s-l112.engr.uiowa.edu:3306/swd_db011";
        mySQLProperties = createMySQLProperties();
        queryBuffer = new CircularBuffer<>(50);
        queryProcessors = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        ServerSocket ss = null;
        active = true;

        System.out.println("Creating MySQL query processor!");
        queryProcessors.execute(new QueryProcessor(queryBuffer, this));

        try {
            initializeTables(); //Setup the two tables
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InetAddress address = InetAddress.getLocalHost();
            ss = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Opening socket, waiting on clients!");
        while (active) {
            Socket s = null;

            try {
                s = ss.accept();

                ClientHandler cl = new ClientHandler(this, s);
                cl.start();

                connectedClients.add(cl);

                System.out.println("A client has connected: " + s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            ss.close();
            for (ClientHandler client : connectedClients) {
                client.disconnect();
                connectedClients.remove(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Initializes the required tables in our MySQL database.
     * @throws Exception    thrown when connection fails.
     */
    private void initializeTables() throws Exception {
        MySQLConnection connection = new MySQLConnection(mySqlURL, mySQLProperties);

        LinkedHashMap<String, String> userAccountsColumns = new LinkedHashMap<>();
        //userAccountsColumns.put("account_type", "ENUM('individual', 'organization')");
        userAccountsColumns.put("account_type", "VARCHAR(20)");
        userAccountsColumns.put("username", "VARCHAR("+ UserAccount.maxUserNameLength+")");
        userAccountsColumns.put("password", "VARCHAR("+ UserAccount.maxPasswordLength+")");
        userAccountsColumns.put("full_name", "VARCHAR("+ UserAccount.maxNameLength+")");
        userAccountsColumns.put("dob_or_founded", "DATE");
        Table userAccounts = new Table("user_accounts", userAccountsColumns);
        userAccounts.initializeTable(connection);
        tables.put(userAccounts.getName(), userAccounts);

        LinkedHashMap<String, String> userVaccinationDatesColumns = new LinkedHashMap<>();
        userVaccinationDatesColumns.put("username", "VARCHAR("+ UserAccount.maxUserNameLength+")");
        userVaccinationDatesColumns.put("vaccination_name", "VARCHAR("+ VaccinationInfo.maxVacNameLength+")");
        userVaccinationDatesColumns.put("date_received", "DATE");
        userVaccinationDatesColumns.put("location", "VARCHAR("+ VaccinationInfo.maxLocationlength+")");
        Table userVaccinationDates = new Table("user_vaccination_dates", userVaccinationDatesColumns);
        userVaccinationDates.initializeTable(connection);
        tables.put(userVaccinationDates.getName(), userVaccinationDates);

        LinkedHashMap<String, String> organizationGuidelinesColumns = new LinkedHashMap<>();
        organizationGuidelinesColumns.put("username", "VARCHAR("+ UserAccount.maxUserNameLength+") UNIQUE");
        organizationGuidelinesColumns.put("policy", "VARCHAR("+ UserAccount.maxPolicyLength+")");
        Table organizationGuidelines = new Table("organization_guidelines", organizationGuidelinesColumns);
        organizationGuidelines.initializeTable(connection);
        tables.put(organizationGuidelines.getName(), organizationGuidelines);

        connection.disconnect();
    }

    private Properties createMySQLProperties() {
        Properties properties = new Properties();
        properties.put("user", "swd_group011");
        properties.put("password", "password");
        return properties;
    }

    public MySQLConnection getDatabaseConnection() throws SQLException {
        return new MySQLConnection(mySqlURL, mySQLProperties);
    }

    public Table getTable(String name) {
        return tables.get(name);
    }

    public void disconnectClient(ClientHandler client) {
        System.out.println("Client disconnecting!");
        if (connectedClients.contains(client)) {
            //client.disconnect();
            connectedClients.remove(client);
        } else {
            System.out.println("Could not find a client to disconnect!");
        }
    }

    /**
     * Stops the main server loop, shutdown process ensues and client connections
     * will close.
     */
    public void shutdown() {
        active = false;
    }

    /**
     * Whether the server has created an active socket users can connect to
     * @return  true if socket present, false is not
     */
    public boolean isOnline() {
        return (socket != null);
    }

    /**
     * Returns a reference to the CircularBuffer storing queries for the MySQL server
     * @return
     */
    public CircularBuffer<Pair<MySQLQueryEvent, Pair<RequestType, String>>> getQueryBuffer() {
        return queryBuffer;
    }

}
