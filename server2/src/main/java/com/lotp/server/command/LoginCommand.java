package com.lotp.server.command;


import com.lotp.server.entity.Player;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 12:29 PM
 */
public class LoginCommand extends Player {
    private String error;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

