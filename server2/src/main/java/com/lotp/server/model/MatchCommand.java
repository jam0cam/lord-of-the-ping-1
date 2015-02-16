package com.lotp.server.model;

import java.util.List;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 4:27 PM
 */
public class MatchCommand {

    private List<String> emailList;
    private String email;
    private String gamesWon;
    private String gamesLost;
    private String values[] = {"0", "1", "2", "3", "4", "5"};

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public List<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<String> emailList) {
        this.emailList = emailList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(String gamesWon) {
        this.gamesWon = gamesWon;
    }

    public String getGamesLost() {
        return gamesLost;
    }

    public void setGamesLost(String gamesLost) {
        this.gamesLost = gamesLost;
    }
}
