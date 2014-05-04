package com.lotp.server.model;

/**
 * User: jitse
 * Date: 1/19/14
 * Time: 11:31 AM
 */
public class PasswordResetCommand {
    private String passwordHash;
    private String newPassword;
    private String email;

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
