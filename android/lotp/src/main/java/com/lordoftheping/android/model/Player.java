package com.lordoftheping.android.model;

import java.io.Serializable;

/**
 * Created by mattkranzler on 12/4/13.
 */
public class Player implements Serializable {
    String id;
    String email;
    String password;
    String name;
    String avatarUrl;

    public Player() {}

    public Player(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Player(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Player(String name, String email, String password, String url) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatarUrl = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (!id.equals(player.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
