package com.weezlabs.databases.db;

import com.weezlabs.databases.model.Book;

import java.util.List;

/**
 * Created by Andrey Bondarenko on 06.05.15.
 */
public interface DatabaseHandler {
    void addBook(Book book);

    Book getBook(int id);

    List<Book> getAllBooks();

    int getBooksCount();

    int updateBook(Book book);

    int deleteBook(Book book);

    void deleteAll();
}