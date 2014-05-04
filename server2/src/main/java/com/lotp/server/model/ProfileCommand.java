package com.lotp.server.model;

import com.lotp.server.entity.Match;
import com.lotp.server.entity.Player;

import java.util.List;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 2:16 PM
 */
public class ProfileCommand {

    private Player player;
    private Stats stats;
    private boolean ownProfile;

    public boolean isOwnProfile() {
        return ownProfile;
    }

    public void setOwnProfile(boolean ownProfile) {
        this.ownProfile = ownProfile;
    }

    /**
     * matches are setup in a way that p1 is always the passed in player.
     */
    private List<Match> matches;


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }
}
