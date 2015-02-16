package com.lotp.postageapp;

import java.util.Map;

/**
 * User: jitse
 * Date: 7/8/13
 * Time: 4:24 PM
 */
public class Response {

    private Map<String, String> response;
    private ResponseMessageMap data;

    public Map<String, String> getResponse() {
        return response;
    }

    public void setResponse(Map<String, String> response) {
        this.response = response;
    }

    public ResponseMessageMap getData() {
        return data;
    }

    public void setData(ResponseMessageMap data) {
        this.data = data;
    }
}
