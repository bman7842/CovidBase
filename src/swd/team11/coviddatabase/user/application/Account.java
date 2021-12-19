package swd.team11.coviddatabase.user.application;

import swd.team11.coviddatabase.AccountType;
import swd.team11.coviddatabase.GuidelinesInfo;
import swd.team11.coviddatabase.UserAccount;
import swd.team11.coviddatabase.VaccinationInfo;
import swd.team11.coviddatabase.user.Client;
import swd.team11.coviddatabase.utils.Packet;
import swd.team11.coviddatabase.utils.PacketType;

import java.io.IOException;

/**
 * Class for modeling a user account, including account data in UserAccount object and the client the
 * account is using to communicate with the server. Stored in user data of the JavaFX stages.
 */
public class Account {

    private Client client;
    private UserAccount userAccount;

    /**
     * Constructs an account object
     * @param client    client used to communicate with the server
     */
    public Account(Client client) {
        this.client = client;
        userAccount = null;
    }

    /**
     * Gets the client associated with this account
     * @return  client reference
     */
    public Client getClient() {
        return client;
    }

    /**
     * Attempt to login to the server
     * @param username  Username of account
     * @param password  Password of account
     * @return          Packet received from server
     */
    public Packet attemptLogin(String username, String password) {
        try {
            return client.requestData("login=username:" + username + ";password:"+password);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Packet attemptGetVacInfos() {
        try {
            return client.requestData("get_vac_info=username:"+userAccount.getUsername());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Packet(PacketType.ERROR, "Request failed");
    }

    public Packet attemptGetOrgGuidelines(String username) {
        try {
            Packet packet = getClient().requestData("get_guidelines=username:"+username);
            return packet;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Packet(PacketType.ERROR, "Unable to process request at this time");
    }

    public Packet attemptUpdateGuidelines(String username, String guideline) {
        try {
            getClient().sendPacket(new Packet(PacketType.UPDATE_GUIDELINES, new GuidelinesInfo(username, guideline)));
            Packet packet = getClient().awaitPacket();
            return packet;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Packet(PacketType.ERROR, "Unable to send guideline update at this time");
    }

    /**
     * attempt to create an account on the server
     * @param account   Account to create
     * @param password  Password of account
     * @return          Packet received from the server
     */
    public Packet attemptCreateAccount(UserAccount account, String password) {
        if (account.getType() == null) {
            account.setType(AccountType.INDIVIDUAL);
        }

        try {
            Packet packet = getClient().requestData("create_account=username:"+account.getUsername()+";password:"+password);
            if (packet.getType().equals(PacketType.ERROR)) {
                //DIsplay error message
                return packet;
            } else if (packet.getType().equals(PacketType.SUCCESS)) {
                System.out.println("SUCCESS");
                getClient().sendPacket(new Packet(PacketType.SEND_USER_ACCOUNT, account));
                this.userAccount = account;
                System.out.println("SENT ACCOUNT");
                return getClient().awaitPacket();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new Packet(PacketType.ERROR, "IOException");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new Packet(PacketType.ERROR, "ClassNotFoundException");
        }
        return new Packet(PacketType.ERROR, "Unable to create account");
    }

    /**
     * Sets the user account data associated with this account object
     * @param userAccount   user account data
     */
    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * Gets the user account data associated with this object
     * @return  user account data
     */
    public UserAccount getUserAccount() {
        return userAccount;
    }

}
