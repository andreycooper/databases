package com.weezlabs.databases.model;

/**
 * Created by Andrey Bondarenko on 08.05.15.
 */
public class User {
    public static final String TABLE = "users";

    public static final String ID = "_id";
    public static final String USER_NAME = "user_name";

    private int mId;
    private String mName;

    public User() {
    }

    public User(int id, String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("mId=").append(mId);
        sb.append(", mName='").append(mName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
