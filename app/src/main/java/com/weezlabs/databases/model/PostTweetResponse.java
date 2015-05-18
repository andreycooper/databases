package com.weezlabs.databases.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Andrey Bondarenko on 18.05.15.
 */
public class PostTweetResponse {
    @SerializedName("id_str")
    private String mId;
    @SerializedName("user")
    private TwitterUser mUser;
    @SerializedName("text")
    private String mText;
    @SerializedName("source")
    private String mSource;
}
