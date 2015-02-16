package com.lordoftheping.android.model;

import java.io.Serializable;

/**
 * Created by mattkranzler on 12/4/13.
 */
public class LeaderboardItem implements Serializable {
    Player player;
    int matchWins;
    int matchLosses;
    double winningPercentage;

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
}
