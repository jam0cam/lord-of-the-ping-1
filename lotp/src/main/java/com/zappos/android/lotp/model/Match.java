package com.zappos.android.lotp.model;

import java.io.Serializable;

/**
 * Created by mattkranzler on 12/4/13.
 */
public class Match implements Serializable {
    private String id;
    private Player p1;
    private Player p2;
    private int p1Score;
    private int p2Score;
    private long date;
    private String dateString;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Player getP1() {
        return p1;
    }

    public void setP1(Player p1) {
        this.p1 = p1;
    }

    public Player getP2() {
        return p2;
    }

    public void setP2(Player p2) {
        this.p2 = p2;
    }

    public int getP1Score() {
        return p1Score;
    }

    public void setP1Score(int p1Score) {
        this.p1Score = p1Score;
    }

    public int getP2Score() {
        return p2Score;
    }

    public void setP2Score(int p2Score) {
        this.p2Score = p2Score;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }
}
