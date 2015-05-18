package com.weezlabs.databases.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Andrey Bondarenko on 18.05.15.
 */
public class TwitterUser {
    @SerializedName("id_str")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("screen_name")
    private String mScreenName;
}
