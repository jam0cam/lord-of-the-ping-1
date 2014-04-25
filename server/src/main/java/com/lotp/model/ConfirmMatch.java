package com.lotp.model;

/**
 * User: jitse
 * Date: 12/5/13
 * Time: 4:51 PM
 */
public class ConfirmMatch {
    private String pendingId;
    private boolean confirmed;

    public String getPendingId() {
        return pendingId;
    }

    public void setPendingId(String pendingId) {
        this.pendingId = pendingId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
