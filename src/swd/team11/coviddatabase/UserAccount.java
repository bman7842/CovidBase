package swd.team11.coviddatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class for representing a user's data.
 */
public class UserAccount implements Serializable {

    /**
     * Reference for max length of a username
     */
    public static int maxUserNameLength = 20;
    /**
     * Reference for max length of a name
     */
    public static int maxNameLength = 40;
    /**
     * Reference for max length of a password
     */
    public static int maxPasswordLength = 20;
    /**
     * Reference for max length of a policy
     */
    public static int maxPolicyLength = 1000;

    /**
     * Type of account (organization or individual)
     */
    private AccountType type;
    /**
     * Username of account
     */
    private String username;
    /**
     * Full name of user or organization
     */
    private String fullname;
    /**
     * Birth date of user or date organiztion was founded
     */
    private Date birthday;
    /**
     * List of vaccination records (user only)
     */
    private ArrayList<VaccinationInfo> vaccinationRecords; //Do this or the policy in constructor based on account type.
    /**
     * organizations vaccination policy
     */
    private String policy;

    /**
     * Construct a user account
     * @param type      Type of user account
     * @param username  Username of user account
     * @param fullname  Fullname of user account
     * @param birthday  Birthday of user account
     */
    public UserAccount(AccountType type, String username, String fullname, Date birthday) {
        this.type = type;
        this.username = username;
        this.fullname = fullname;
        this.birthday = birthday;
    }

    /**
     * Get the type of account
     * @return  associated account type
     */
    public AccountType getType() {
        return type;
    }

    /**
     * Get the birthday of the account
     * @return  birthday of user
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * Get the full name of user/organization
     * @return  full name
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * Returns the full list of vaccination records to manipulate
     * @return  List of vaccination records
     */
    public ArrayList<VaccinationInfo> getVaccinationRecords() {
        return vaccinationRecords;
    }

    /**
     * Gets the username of account
     * @return  username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the type of the account
     * @param type  account type
     */
    public void setType(AccountType type) {
        this.type = type;
    }

    /**
     * Sets the username of the account
     * @param username  new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the full name of the account
     * @param fullname  new full name
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     * Sets the birthday of the account
     * @param date  new birth day date
     */
    public void setBirthday(Date date) {
        this.birthday = date;
    }

}
