package com.lotp.model;

import java.io.Serializable;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 10:22 AM
 */
public class BaseObject implements Serializable {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
