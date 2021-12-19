package swd.team11.coviddatabase;

import java.io.Serializable;

public class GuidelinesInfo implements Serializable {

    private String username;
    private String guideline;

    public GuidelinesInfo(String username, String guideline) {
        this.username = username;
        this.guideline = guideline;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGuideline(String guideline) {
        this.guideline = guideline;
    }

    public String getUsername() {
        return username;
    }

    public String getGuideline() {
        return guideline;
    }

}
