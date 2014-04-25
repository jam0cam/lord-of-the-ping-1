package com.zappos.android.lotp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mattkranzler on 12/4/13.
 */
public class Profile implements Serializable {

    Player player;
    Stats stats;
    List<Match> matches;

    public Player getPlayer() {
        return player;
    }

    public Stats getStats() {
        return stats;
    }

    public List<Match> getMatches() {
        return matches;
    }
}
