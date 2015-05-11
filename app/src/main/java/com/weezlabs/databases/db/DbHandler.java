package com.weezlabs.databases.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.weezlabs.databases.model.Book;
import com.weezlabs.databases.model.User;

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

    private static final String CREATE_TABLE_USERS = "" +
            "CREATE TABLE " + User.TABLE + "(" +
            User.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            User.USER_NAME + " TEXT" +
            ")";

    private static final String CREATE_TABLE_USER_BOOK_LINKS = "" +
            "CREATE TABLE " + UserBookLink.TABLE + "(" +
            UserBookLink.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserBookLink.USER_ID + " INTEGER NOT NULL," +
            UserBookLink.BOOK_ID + " INTEGER NOT NULL" +
            ")";

    private static final String UPGRADE_TABLE_BOOKS = "" +
            "DROP TABLE IF EXISTS " + Book.TABLE;
    private static final String UPGRADE_TABLE_USERS = "" +
            "DROP TABLE IF EXISTS " + User.TABLE;
    private static final String UPGRADE_TABLE_USER_BOOK_LINKS = "" +
            "DROP TABLE IF EXISTS " + UserBookLink.TABLE;

    public interface UserBookLink {
        String TABLE = "user_book";
        String ID = "_id";
        String USER_ID = "user_id";
        String BOOK_ID = "book_id";
    }

    public interface Queries {
        String SELECT_ALL_BOOKS = "SELECT * FROM " + Book.TABLE;
    }

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOKS);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_USER_BOOK_LINKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UPGRADE_TABLE_BOOKS);
        db.execSQL(UPGRADE_TABLE_USERS);
        db.execSQL(UPGRADE_TABLE_USER_BOOK_LINKS);
        onCreate(db);
    }

    @Override
    public long addBook(Book book) {
        long rowId;
        SQLiteDatabase db = this.getWritableDatabase();
        rowId = db.insert(Book.TABLE, null, new Book.ValuesBuilder()
                .author(book.getAuthor())
                .title(book.getTitle())
                .coverPath(book.getCoverPath())
                .descriptuon(book.getDescription())
                .build());
        db.close();
        return rowId;
    }

    @Override
    public Book getBook(int id) {
        Book book = null;
        String[] columns = new String[]{Book.ID, Book.AUTHOR, Book.TITLE, Book.COVER_PATH, Book.DESCRIPTION};
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(Book.TABLE, columns, Book.ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            book = Book.getBookFromCursor(cursor);
        }
        closeCursor(cursor);

        return book;
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        Book book;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(Queries.SELECT_ALL_BOOKS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                book = Book.getBookFromCursor(cursor);
                bookList.add(book);
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return bookList;
    }

    @Override
    public Cursor getAllBooksCursor() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(Queries.SELECT_ALL_BOOKS, null);
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

    @Override
    public Cursor getUsersWhoTakeBook(Book bookId) {
        // TODO: implement select from users table using links table
        return null;
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

}
