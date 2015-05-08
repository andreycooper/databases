package com.weezlabs.databases.db;

import android.database.Cursor;

import com.weezlabs.databases.model.Book;

import java.util.List;

/**
 * Created by Andrey Bondarenko on 06.05.15.
 */
public interface DatabaseHandler {
    long addBook(Book book);

    Book getBook(int id);

    List<Book> getAllBooks();

    Cursor getAllBooksCursor();

    int getBooksCount();

    int updateBook(Book book);

    int deleteBook(Book book);

    void deleteAll();

    Cursor getUsersWhoTakeBook(Book bookId);

}