package com.lotp.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 10:22 AM
 */
@Entity
public class Player extends AbstractPersistable<Long> {

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String avatarUrl;

    @Column
    private Integer ranking = 1200;

    @Column
    private Double sigma = 400.00;

    @Column
    private Boolean ownProfile = false;

    @OneToOne
    private PasswordReset passwordReset;

    public boolean isOwnProfile() {
        return ownProfile;
    }

    public void setOwnProfile(boolean ownProfile) {
        this.ownProfile = ownProfile;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public PasswordReset getPasswordReset() {
        return passwordReset;
    }

    public void setPasswordReset(PasswordReset passwordReset) {
        this.passwordReset = passwordReset;
    }
}
