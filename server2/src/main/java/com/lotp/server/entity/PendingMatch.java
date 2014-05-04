package com.lotp.server.entity;

import javax.persistence.Entity;

/**
 * User: mruno
 * Date: 5/3/14
 * Time: 1:32 PM
 */
@Entity
public class PendingMatch extends Match {

    public PendingMatch() {
        setPending(true);
    }
}
