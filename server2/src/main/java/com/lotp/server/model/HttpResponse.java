package com.lotp.server.model;

/**
 * User: jitse
 * Date: 12/5/13
 * Time: 9:43 AM
 */
public class HttpResponse {
    private int code;
    private String message;

    public HttpResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public HttpResponse(){}

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
