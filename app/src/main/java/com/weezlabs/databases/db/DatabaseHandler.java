package com.weezlabs.databases.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.weezlabs.databases.model.Book;
import com.weezlabs.databases.model.User;
import com.weezlabs.databases.model.UserBookLink;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey Bondarenko on 06.05.15.
 */
public class DatabaseHandler extends SQLiteOpenHelper implements BooksDbHandler, UsersDbHandler {
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

    public interface Queries {
        String SELECT_ALL_BOOKS = "SELECT * FROM " + Book.TABLE;
        String SELECT_ALL_USERS = "SELECT * FROM " + User.TABLE;
        String SELECT_COUNT_TAKEN_BOOKS = "SELECT count(" + UserBookLink.TABLE + "." + UserBookLink.ID + ") " +
                "FROM " + UserBookLink.TABLE + " " +
                "WHERE " + UserBookLink.TABLE + "." + UserBookLink.BOOK_ID + "=" + Book.TABLE + "." + Book.ID;
        String SELECT_BOOKS_WITH_AVAILABLE_AMOUNT = "SELECT *, " +
                Book.TOTAL_AMOUNT + "-" + "(" + SELECT_COUNT_TAKEN_BOOKS + ") AS " + Book.AMOUNT_ALIAS + " " +
                "FROM " + Book.TABLE;
        String SELECT_AVAILABLE_BOOKS = SELECT_BOOKS_WITH_AVAILABLE_AMOUNT + " " +
                "WHERE " + Book.AMOUNT_ALIAS + ">0";
        String SELECT_USERS_WHO_TAKE_BOOK = "SELECT " +
                User.TABLE + "." + User.ID + ", " + User.TABLE + "." + User.USER_NAME + " " +
                "FROM " + User.TABLE + " " +
                "INNER JOIN " + UserBookLink.TABLE + " ON " +
                User.TABLE + "." + User.ID + "=" + UserBookLink.TABLE + "." + UserBookLink.USER_ID + " " +
                "WHERE " + UserBookLink.TABLE + "." + UserBookLink.BOOK_ID + "=?" + " " +
                "GROUP BY " + User.TABLE + "." + User.ID;
    }

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

    @Override
    public long addBook(Book book) {
        long rowId;
        SQLiteDatabase db = this.getWritableDatabase();
        rowId = db.insert(Book.TABLE, null, new Book.Builder()
                .author(book.getAuthor())
                .title(book.getTitle())
                .coverPath(book.getCoverPath())
                .description(book.getDescription())
                .totalAmount(book.getTotalAmount())
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
    public List<Book> getBooksList() {
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
    public Cursor getBooksCursor() {
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
        int countRows = db.update(Book.TABLE, new Book.Builder()
                .author(book.getAuthor())
                .title(book.getTitle())
                .coverPath(book.getCoverPath())
                .description(book.getDescription())
                .totalAmount(book.getTotalAmount())
                .build(), Book.ID + "=?", new String[]{String.valueOf(book.getId())});

        db.close();
        return countRows;
    }

    @Override
    public int deleteBook(Book book) {
        SQLiteDatabase db = getWritableDatabase();
        int countRows = db.delete(Book.TABLE, Book.ID + "=?", new String[]{String.valueOf(book.getId())});
        db.delete(UserBookLink.TABLE, UserBookLink.BOOK_ID + "=?", new String[]{String.valueOf(book.getId())});
        db.close();
        return countRows;
    }

    @Override
    public void deleteAllBooks() {

    }

    @Override
    public Cursor getAvailableBooks() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(Queries.SELECT_AVAILABLE_BOOKS, null);
    }

    @Override
    public Cursor getBooksWithAvailableAmount() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(Queries.SELECT_BOOKS_WITH_AVAILABLE_AMOUNT, null);
    }

    // UserDbHandler implementation

    @Override
    public User getUser(int id) {
        User user = null;
        String[] columns = new String[]{User.ID, User.USER_NAME};
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(User.TABLE, columns, User.ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            user = User.getUserFromCursor(cursor);
        }
        closeCursor(cursor);

        return user;
    }

    @Override
    public long addUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        long rowId = db.insert(User.TABLE, null, new User.Builder()
                .userName(user.getName())
                .build());
        db.close();
        return rowId;
    }

    @Override
    public Cursor getUsersCursor() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(Queries.SELECT_ALL_USERS, null);
    }

    @Override
    public int updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        int countRows = db.update(User.TABLE, new User.Builder()
                .userName(user.getName())
                .build(), User.ID + "=?", new String[]{String.valueOf(user.getId())});
        db.close();
        return countRows;
    }

    @Override
    public int deleteUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        int countRows = db.delete(User.TABLE, User.ID + "=?", new String[]{String.valueOf(user.getId())});
        db.delete(UserBookLink.TABLE, UserBookLink.USER_ID + "=?", new String[]{String.valueOf(user.getId())});
        db.close();
        return countRows;
    }

    @Override
    public void giveBooksToUser(List<Integer> bookIdList, int userId) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            for (Integer id : bookIdList) {
                values.clear();
                values.put(UserBookLink.USER_ID, userId);
                values.put(UserBookLink.BOOK_ID, id);
                db.insert(UserBookLink.TABLE, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    @Override
    public Cursor getUsersWhoTakeBook(int bookId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(Queries.SELECT_USERS_WHO_TAKE_BOOK, new String[]{String.valueOf(bookId)});
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

}
