package swd.team11.coviddatabase;

import java.io.Serializable;
import java.util.Date;

/**
 * Object for representing a row of the vaccination status MySQL table
 */
public class VaccinationInfo implements Serializable {

    /**
     * Username containing this data
     */
    private String username;
    /**
     * Max length of a vaccination name
     */
    public static int maxVacNameLength = 200;
    /**
     *  Max length of the distributor location string
     */
    public static int maxLocationlength = 200;

    /**
     * Date the vaccination was received
     */
    private Date dateReceived;
    /**
     * Name of vaccination received
     */
    private String name;
    /**
     * Location vaccination was distributed
     */
    private String location;

    /**
     * Vaccination record object constructor
     * @param dateReceived  Date vaccination received
     * @param name          Name of vaccination
     * @param location      Location vaccination received
     */
    public VaccinationInfo(String username, Date dateReceived, String name, String location) {
        this.username = username;
        this.dateReceived = dateReceived;
        this.name = name;
        this.location = location;
    }

    /**
     * Sets name of the vaccination
     * @param name  name of vaccination
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets location vaccination was received
     * @param location  location of vaccination
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Set date vaccination was received
     * @param date  date received
     */
    public void setDateReceived(Date date) {
        this.dateReceived = date;
    }

    /**
     * Get the name of the vaccination
     * @return  name of vaccination
     */
    public String getName() {
        return name;
    }

    /**
     * Get location vaccination received
     * @return  location of vaccination
     */
    public String getLocation() {
        return location;
    }

    /**
     * Get date vaccination received
     * @return  date vaccination received
     */
    public Date getDateReceived() {
        return dateReceived;
    }

    /**
     * return the accounts username
     * @return  username
     */
    public String getUsername() {
        return username;
    }

    public VaccinationInfo(String name, String location, Date dateRecieved){
        this.dateReceived = dateRecieved;
        this.name = name;
        this.location = location;
    }

    public String toString() {
        return "Name: " +name + ", received on: " + dateReceived.toString() + " at: " + location;
    }

}
