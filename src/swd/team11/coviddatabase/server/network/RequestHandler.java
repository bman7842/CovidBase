package swd.team11.coviddatabase.server.network;

import swd.team11.coviddatabase.AccountType;
import swd.team11.coviddatabase.GuidelinesInfo;
import swd.team11.coviddatabase.VaccinationInfo;
import swd.team11.coviddatabase.events.MySQLQueryEvent;
import swd.team11.coviddatabase.UserAccount;
import swd.team11.coviddatabase.server.mysql.Table;
import swd.team11.coviddatabase.utils.ArgsParser;
import swd.team11.coviddatabase.utils.Packet;
import swd.team11.coviddatabase.utils.PacketType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for handling requests packets received from clients
 */
public class RequestHandler {

    /**
     * Request packet
     */
    private Packet request;
    /**
     * Reference to database server for sending MySQL queries
     */
    private DatabaseServer dbs;
    /**
     * Reference to ClientHandler request was received from
     */
    private ClientHandler client;

    /**
     * Creates new request handler
     * @param client    ClientHandler which received request
     * @param request   Request packet received
     * @param dbs       Database server
     */
    public RequestHandler(ClientHandler client, Packet request, DatabaseServer dbs) {
        this.request = request;
        this.dbs = dbs;
        this.client = client;
        if (!request.getType().equals(PacketType.REQUEST)) {
            request = null;
        }
    }

    /**
     * Reads request data identifier, ensures valid request packet and determines what data the client
     * wants back then runs the necessary methods to obtain it.
     */
    public void processRequest() {
        if (request == null) {
            return;
        }

        ArgsParser args = new ArgsParser((String) request.getData());
        Packet packet = null;
        if (args.getIdentifier().equals("login")) {
            searchUserAccount(args.get("username"), args.get("password"));
        } else if (args.getIdentifier().equals("create_account")) {
            createAccount(args);
        } else if (args.getIdentifier().equals("get_vac_info")) {
            getVaccinationInfo(args);
        } else if (args.getIdentifier().equals("get_guidelines")) {
            getGuidelines(args);
        } else {
            System.out.println("Unable to process request, unknown identifier '"+args.getIdentifier()+"'");
            client.sendPacket(new Packet(PacketType.ERROR, "Unable to process request, unknown identifier"));
        }

    }

    public void getVaccinationInfo(ArgsParser args) {
        if (args.has("username")) {
            Table vaccinationTable = dbs.getTable("user_vaccination_dates");
            try {
                vaccinationTable.obtainResultSet(new MySQLQueryEvent() {
                    @Override
                    public void querySentEvent(Packet response) {

                    }

                    @Override
                    public void resultReceivedEvent(ResultSet rs) {
                        try {
                            ArrayList<VaccinationInfo> vacInfos = new ArrayList<>();
                            while (rs.next()) {
                                VaccinationInfo vacInfo = new VaccinationInfo(rs.getString("username"), rs.getDate("date_received"), rs.getString("vaccination_name"), rs.getString("location"));
                                vacInfos.add(vacInfo);
                            }
                            client.sendPacket(new Packet(PacketType.SEND_VACCINATION_INFO, vacInfos));
                        } catch (NullPointerException | SQLException e) {
                            client.sendPacket(new Packet(PacketType.ERROR, "Unable to find results for this username"));
                        }
                    }
                }, dbs.getQueryBuffer(), args.get("username"));
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.sendPacket(new Packet(PacketType.ERROR, "Unable to process query to database"));
            }
        } else {
            client.sendPacket(new Packet(PacketType.ERROR, "search username missing in arguments"));
        }
    }

    public void getGuidelines(ArgsParser args) {
        if (args.has("username")) {
            Table guidelinesTable = dbs.getTable("organization_guidelines");
            try {
                guidelinesTable.obtainResultSet(new MySQLQueryEvent() {
                    @Override
                    public void querySentEvent(Packet response) {
                    }

                    @Override
                    public void resultReceivedEvent(ResultSet rs) {
                        try {
                            if (rs.next()) {
                                client.sendPacket(new Packet(PacketType.UPDATE_GUIDELINES, new GuidelinesInfo(rs.getString("username"), rs.getString("policy"))));
                            } else {
                                client.sendPacket(new Packet(PacketType.ERROR, "No guidelines found in search"));
                            }
                        } catch (NullPointerException | SQLException e) {
                            client.sendPacket(new Packet(PacketType.ERROR, "No guidelines found in search"));
                        }
                    }
                }, dbs.getQueryBuffer(), args.get("username"));
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.sendPacket(new Packet(PacketType.ERROR, "Unable to process request at this time"));
            }
        } else {
            client.sendPacket(new Packet(PacketType.ERROR, "search username missing in arguments"));
        }
    }

    /**
     * Check if account exists, if it does, send data Packet of success back to Client and continue waiting to receive account data
     * so you can create account, if not send detailed ERROR message packet to user.
     * @param args  Arguments passed in request message
     */
    public void createAccount(ArgsParser args) {
        Table accountTable = dbs.getTable("user_accounts");
        if (args.has("username") && args.has("password")) {
            client.sendPacket(new Packet(PacketType.SUCCESS, null));
            ObjectInputStream ois = client.getOIS();
            try {
                Packet accountPacket = (Packet) ois.readObject();
                if (accountPacket.getType().equals(PacketType.SEND_USER_ACCOUNT)) {
                    UserAccount userAccount = (UserAccount) accountPacket.getData();

                    accountTable.obtainResultSet(new MySQLQueryEvent() {
                        @Override
                        public void querySentEvent(Packet response) {

                        }

                        @Override
                        public void resultReceivedEvent(ResultSet rs) {
                            try {
                                if (rs.next()) {
                                    client.sendPacket(new Packet(PacketType.ERROR, "Account with that username already exists!"));
                                } else {
                                    addAccount(userAccount, args);
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                                client.sendPacket(new Packet(PacketType.ERROR, "Unable to process user account"));
                            } catch (NullPointerException e) {
                                addAccount(userAccount, args);
                            }
                        }
                    }, dbs.getQueryBuffer(), userAccount.getUsername());
                } else {
                    client.sendPacket(new Packet(PacketType.ERROR, "Excepted SEND_USER_ACCOUNT, but received something else"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            client.sendPacket(new Packet(PacketType.ERROR, "Invalid format of create account request, needs password and username args"));
        }
    }

    /**
     * Attempt to add account to user_accounts database, if the account already exists or other part false it will send a error packet back,
     * otherwise it will send a success packet. Client must send request to make account, then method will listen for packet containing user account data
     * and try to add it to the MySQL table.
     * @param userAccount   User account to create in database
     * @param args          Args passed in original request message
     */
    public void addAccount(UserAccount userAccount, ArgsParser args) {
        Table accountTable = dbs.getTable("user_accounts");
        HashMap<String, String> columnValues = new HashMap<String, String>();
        columnValues.put("account_type", "'"+userAccount.getType().toString().toLowerCase()+"'");
        columnValues.put("password", "'"+args.get("password")+"'");
        columnValues.put("full_name", "'"+userAccount.getFullname()+"'");
        columnValues.put("dob_or_founded", "STR_TO_DATE('"+userAccount.getBirthday().toString()+"', '%Y-%m-%d')"); //Might be wrong

        try {
            accountTable.addRow(new MySQLQueryEvent() {
                @Override
                public void querySentEvent(Packet response) {
                    client.sendPacket(new Packet(PacketType.SUCCESS, null));
                }

                @Override
                public void resultReceivedEvent(ResultSet rs) {

                }
            }, dbs.getQueryBuffer(), userAccount.getUsername(), columnValues);
        } catch (InterruptedException e) {
            e.printStackTrace();
            client.sendPacket(new Packet(PacketType.ERROR, "Unable to create account in database"));
        }
    }

    /**
     * Search to see if a user account exists, if it does send this account packet back to the client that requested it.
     * This is used to handle login requests sent by the client.
     * @param username  Username of the account
     * @param password  Associated account password
     */
    public void searchUserAccount(String username, String password) {
        Table accountTable = dbs.getTable("user_accounts");
        try {
            accountTable.obtainResultSet(new MySQLQueryEvent() {
                @Override
                public void querySentEvent(Packet response) {

                }

                @Override
                public void resultReceivedEvent(ResultSet rs) {
                    try {
                        if (rs.next()) {
                            if (rs.getString("password").equals(password)) {
                                UserAccount user = new UserAccount(AccountType.valueOf(rs.getString("account_type").toUpperCase()), username, rs.getString("full_name"), rs.getDate("dob_or_founded")); //Add it so it loads vaccination records or guideline as well, pass Table for it to reference along with connection
                                client.sendPacket(new Packet(PacketType.SEND_USER_ACCOUNT, user));
                            } else {
                                client.sendPacket(new Packet(PacketType.ERROR, "The username/password entered is incorrect"));
                            }
                        } else {
                            client.sendPacket(new Packet(PacketType.ERROR, "No accounts found with that username!"));
                        }
                    } catch (NullPointerException | SQLException e) {
                        System.out.println("Unable to find username");
                        client.sendPacket(new Packet(PacketType.ERROR, "No accounts found with that username!"));
                    }
                }
            }, dbs.getQueryBuffer(), username);
        } catch (InterruptedException e) {
            e.printStackTrace();
            client.sendPacket(new Packet(PacketType.ERROR, "Unable to connect to database!"));
        }

    }

}
