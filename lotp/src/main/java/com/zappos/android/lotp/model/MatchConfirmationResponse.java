package com.zappos.android.lotp.model;

import java.io.Serializable;

/**
 * Created by mattkranzler on 12/7/13.
 */
public class MatchConfirmationResponse implements Serializable {

    String pendingId;
    boolean confirmed;

    public MatchConfirmationResponse(String pendingId, boolean confirmed) {
        this.pendingId = pendingId;
        this.confirmed = confirmed;
    }
}
