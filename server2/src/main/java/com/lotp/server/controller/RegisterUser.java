package com.lotp.server.controller;

import com.lotp.server.entity.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * User: jitse
 * Date: 12/5/13
 * Time: 9:54 AM
 *
 * basically same as Player, but without the jsonignore stuff. whatevs.
 */
public class RegisterUser {

    private String email;
    private String password;
    private String name;
    private String avatarUrl;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public Player toPlayer() {
        Player player = new Player();
        player.setName(name);
        player.setEmail(email);
        player.setPassword(password);
        player.setAvatarUrl(avatarUrl);
        return player;
    }
}
