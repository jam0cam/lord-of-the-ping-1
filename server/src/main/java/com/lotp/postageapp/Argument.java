package com.lotp.postageapp;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * User: jitse
 * Date: 7/8/13
 * Time: 3:37 PM
 */
public class Argument {

    private List<String> recipients;

    @JsonProperty("headers")
    private Header header;

    private String content;

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
