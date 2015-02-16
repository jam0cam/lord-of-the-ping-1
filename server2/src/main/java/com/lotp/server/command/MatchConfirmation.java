package com.lotp.server.command;


/**
 * User: jitse
 * Date: 12/5/13
 * Time: 4:51 PM
 */
public class MatchConfirmation {

    private long pendingId;

    private boolean confirmed;

    public long getPendingId() {
        return pendingId;
    }

    public void setPendingId(long pendingId) {
        this.pendingId = pendingId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
