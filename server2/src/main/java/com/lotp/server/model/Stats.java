package com.lotp.server.model;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 3:27 PM
 */
public class Stats {
    private int matchWins;
    private int matchLosses;
    private int matchWinPercentage;
    private int totalMatches;

    private int gameWins;
    private int gameLosses;
    private int gameWinPercentage;
    private int totalGames;

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
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

    public int getGameWins() {
        return gameWins;
    }

    public void setGameWins(int gameWins) {
        this.gameWins = gameWins;
    }

    public int getGameLosses() {
        return gameLosses;
    }

    public void setGameLosses(int gameLosses) {
        this.gameLosses = gameLosses;
    }

    public int getMatchWinPercentage() {
        return matchWinPercentage;
    }

    public void setMatchWinPercentage(int matchWinPercentage) {
        this.matchWinPercentage = matchWinPercentage;
    }

    public int getGameWinPercentage() {
        return gameWinPercentage;
    }

    public void setGameWinPercentage(int gameWinPercentage) {
        this.gameWinPercentage = gameWinPercentage;
    }
}
