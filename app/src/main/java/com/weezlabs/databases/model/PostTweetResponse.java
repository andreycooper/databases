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

    public String getId() {
        return mId;
    }

    public TwitterUser getUser() {
        return mUser;
    }

    public String getText() {
        return mText;
    }

    public String getSource() {
        return mSource;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PostTweetResponse{");
        sb.append("mId='").append(mId).append('\'');
        sb.append(", mUser=").append(mUser);
        sb.append(", mText='").append(mText).append('\'');
        sb.append(", mSource='").append(mSource).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
