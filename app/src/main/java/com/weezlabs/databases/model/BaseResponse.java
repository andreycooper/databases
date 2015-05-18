package com.weezlabs.databases.model;

/**
 * Created by Andrey Bondarenko on 18.05.15.
 */
public abstract class BaseResponse {

    private String error = null;

    public String getError() {
        return error;
    }
}