package com.weezlabs.databases.model;

import android.content.ContentValues;
import android.database.Cursor;

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

    public static User getUserFromCursor(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndex(ID)));
        user.setName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
        return user;
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

    public static final class ValuesBuilder {
        private final ContentValues values = new ContentValues();

        public ValuesBuilder id(int id) {
            values.put(ID, id);
            return this;
        }

        public ValuesBuilder userName(String userName) {
            values.put(USER_NAME, userName);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }
}
