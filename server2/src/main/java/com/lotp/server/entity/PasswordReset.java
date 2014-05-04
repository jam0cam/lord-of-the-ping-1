package com.lotp.server.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * User: mruno
 * Date: 5/3/14
 * Time: 8:28 PM
 */
@Entity
public class PasswordReset extends AbstractPersistable<Long> {

    @Column
    private String resetHash;

    public String getResetHash() {
        return resetHash;
    }

    public void setResetHash(String resetHash) {
        this.resetHash = resetHash;
    }
}
