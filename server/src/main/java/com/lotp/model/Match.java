package com.lotp.model;

import java.util.Date;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 10:24 AM
 */
public class Match extends BaseObject {
    private Player p1;
    private Player p2;
    private int p1Score;
    private int p2Score;
    private Date date = new Date();
    private String dateString;
    private String status;
    private boolean pending;

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public void swapPlayer() {
        Player temp = p2;
        p2 = p1;
        p1 = temp;

        int score = p2Score;
        p2Score = p1Score;
        p1Score = score;
    }
}
