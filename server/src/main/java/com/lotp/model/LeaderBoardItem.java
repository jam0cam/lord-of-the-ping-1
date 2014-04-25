package com.lotp.model;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 10:56 AM
 */
public class LeaderBoardItem {
    private Player player;
    private int matchWins;
    private int matchLosses;
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
}
