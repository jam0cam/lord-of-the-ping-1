package com.zappos.android.pingpong.model;

import java.io.Serializable;

/**
 * Created by mattkranzler on 12/4/13.
 */
public class Stats implements Serializable {

    int matchWins;
    int matchLosses;
    int gameWins;
    int gameLosses;

    public int getMatchWins() {
        return matchWins;
    }

    public int getMatchLosses() {
        return matchLosses;
    }

    public int getGameWins() {
        return gameWins;
    }

    public int getGameLosses() {
        return gameLosses;
    }
}
