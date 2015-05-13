package com.weezlabs.databases.db;

import android.database.Cursor;

import com.weezlabs.databases.model.User;

import java.util.List;

/**
 * Created by Andrey Bondarenko on 12.05.15.
 */
public interface UsersDbHandler {
    User getUser(int id);

    long addUser(User user);

    Cursor getUsersCursor();

    int updateUser(User user);

    int deleteUser(User user);

    void giveBooksToUser(List<Integer> bookIdList, int userId);

    Cursor getUsersWhoTakeBook(int bookId);
}
