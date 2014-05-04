package com.lotp.server.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 10:56 AM
 */
@Entity
public class LeaderboardItem extends AbstractPersistable<Long> implements Comparable<LeaderboardItem> {

    @OneToOne
    private Player player;

    @Column
    private Integer matchWins = 0;

    @Column
    private Integer matchLosses = 0;

    @Column
    private double winningPercentage;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getMatchWins() {
        return matchWins;
    }

    public void setMatchWins(int matchWins) {
        this.matchWins = matchWins;
    }

    public int getMatchLosses() {
        return matchLosses;
    }

    public void setMatchLosses(int matchLosses) {
        this.matchLosses = matchLosses;
    }

    public double getWinningPercentage() {
        return winningPercentage;
    }

    public void setWinningPercentage(double winningPercentage) {
        this.winningPercentage = winningPercentage;
    }

    @Override
    public int compareTo(LeaderboardItem leaderboardItem) {
        return leaderboardItem.getPlayer().getRanking() - player.getRanking();
    }
}
