package com.lotp.server.entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 10:24 AM
 */
@Entity
@Table(name = "matches")
public class Match extends AbstractPersistable<Long> {

    @OneToOne(optional = false)
    private Player playerOne;

    @OneToOne(optional = false)
    private Player playerTwo;

    @Column
    private Integer p1Score;

    @Column
    private Integer p2Score;

    @Column
    private Date date = new Date();

    @Column
    private String dateString;

    @Column
    private String status;

    @Column
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

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
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
        Player temp = playerTwo;
        playerTwo = playerOne;
        playerOne = temp;

        int score = p2Score;
        p2Score = p1Score;
        p1Score = score;
    }
}
