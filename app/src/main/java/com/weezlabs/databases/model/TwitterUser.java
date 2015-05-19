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

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getScreenName() {
        return mScreenName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TwitterUser{");
        sb.append("mId='").append(mId).append('\'');
        sb.append(", mName='").append(mName).append('\'');
        sb.append(", mScreenName='").append(mScreenName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
