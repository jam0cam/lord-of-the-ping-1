package com.lotp.model;

import java.util.List;

/**
 * User: jitse
 * Date: 12/6/13
 * Time: 9:41 AM
 */
public class InboxCommand {

    private List<Match> pendingMatches;

    public List<Match> getPendingMatches() {
        return pendingMatches;
    }

    public void setPendingMatches(List<Match> pendingMatches) {
        this.pendingMatches = pendingMatches;
    }
}
