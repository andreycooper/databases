package com.weezlabs.databases.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.weezlabs.databases.model.Book;
import com.weezlabs.databases.model.User;
import com.weezlabs.databases.model.UserBookLink;

/**
 * Created by Andrey Bondarenko on 06.05.15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "books_db";

    private static final String CREATE_TABLE_BOOKS = "" +
            "CREATE TABLE " + Book.TABLE + "(" +
            Book.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Book.AUTHOR + " TEXT NOT NULL," +
            Book.TITLE + " TEXT NOT NULL," +
            Book.COVER_PATH + " TEXT," +
            Book.DESCRIPTION + " TEXT," +
            Book.TOTAL_AMOUNT + " INTEGER NOT NULL" +
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

    public DatabaseHandler(Context context) {
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

}
