package com.weezlabs.databases.model;

import android.content.ContentValues;

/**
 * Created by Andrey Bondarenko on 14.05.15.
 */
public class UserBookLink {
    public static final String TABLE = "user_book";
    public static final String ID = "_id";
    public static final String USER_ID = "user_id";
    public static final String BOOK_ID = "book_id";

    public static String getTableColumn(String column) {
        return TABLE + "." + column;
    }

    public static final class Builder {
        private final ContentValues values = new ContentValues();

        public Builder id(int id) {
            values.put(ID, id);
            return this;
        }

        public Builder userId(int userId) {
            values.put(USER_ID, userId);
            return this;
        }

        public Builder bookId(int bookId) {
            values.put(BOOK_ID, bookId);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }
}
