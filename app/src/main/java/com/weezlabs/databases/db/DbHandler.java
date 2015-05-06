package com.weezlabs.databases.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.weezlabs.databases.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey Bondarenko on 06.05.15.
 */
public class DbHandler extends SQLiteOpenHelper implements DatabaseHandler {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "books_db";

    private static final String CREATE_TABLE_BOOKS = "" +
            "CREATE TABLE " + Book.TABLE + "(" +
            Book.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Book.AUTHOR + " TEXT NOT NULL," +
            Book.TITLE + " TEXT NOT NULL," +
            Book.COVER_PATH + " TEXT," +
            Book.DESCRIPTION + " TEXT" +
            ")";

    private static final String UPGRADE_TABLE_BOOKS = "" +
            "DROP TABLE IF EXISTS " + Book.TABLE;

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UPGRADE_TABLE_BOOKS);
        onCreate(db);
    }

    @Override
    public void addBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(Book.TABLE, null, new Book.ValuesBuilder()
                .author(book.getAuthor())
                .title(book.getTitle())
                .coverPath(book.getCoverPath())
                .descriptuon(book.getDescription())
                .build());
        db.close();
    }

    @Override
    public Book getBook(int id) {
        Book book = null;
        String[] columns = new String[]{Book.ID, Book.AUTHOR, Book.TITLE, Book.COVER_PATH, Book.DESCRIPTION};
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(Book.TABLE, columns, Book.ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            book = getBookFromCursor(cursor);
        }
        closeCursor(cursor);

        return book;
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        Book book;
        String selectAllQuery = "SELECT * FROM " + Book.TABLE;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectAllQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                book = getBookFromCursor(cursor);
                bookList.add(book);
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return bookList;
    }

    @Override
    public int getBooksCount() {
        return 0;
    }

    @Override
    public int updateBook(Book book) {
        SQLiteDatabase db = getWritableDatabase();
        int countRows = db.update(Book.TABLE, new Book.ValuesBuilder()
                .author(book.getAuthor())
                .title(book.getTitle())
                .coverPath(book.getCoverPath())
                .descriptuon(book.getDescription())
                .build(), Book.ID + "=?", new String[]{String.valueOf(book.getId())});

        db.close();
        return countRows;
    }

    @Override
    public int deleteBook(Book book) {
        SQLiteDatabase db = getWritableDatabase();
        int countRows = db.delete(Book.TABLE, Book.ID + "=?", new String[]{String.valueOf(book.getId())});
        db.close();
        return countRows;
    }

    @Override
    public void deleteAll() {

    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    private Book getBookFromCursor(Cursor cursor) {
        Book book = new Book();
        book.setId(cursor.getInt(cursor.getColumnIndex(Book.ID)));
        book.setAuthor(cursor.getString(cursor.getColumnIndex(Book.AUTHOR)));
        book.setTitle(cursor.getString(cursor.getColumnIndex(Book.TITLE)));
        book.setCoverPath(cursor.getString(cursor.getColumnIndex(Book.COVER_PATH)));
        book.setDescription(cursor.getString(cursor.getColumnIndex(Book.DESCRIPTION)));
        return book;
    }

}
