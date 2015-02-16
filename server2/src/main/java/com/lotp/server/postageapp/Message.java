package com.lotp.server.postageapp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * User: jitse
 * Date: 7/8/13
 * Time: 12:03 PM
 */
public class Message {

    @JsonProperty("api_key")
    private String apiKey;
    private String uid = UUID.randomUUID().toString();

    @JsonProperty("arguments")
    private Argument argument;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Argument getArgument() {
        return argument;
    }

    public void setArgument(Argument argument) {
        this.argument = argument;
    }
}
