package com.weezlabs.databases;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.weezlabs.databases.db.DatabaseHandler;
import com.weezlabs.databases.model.Book;
import com.weezlabs.databases.model.User;
import com.weezlabs.databases.model.UserBookLink;

public class BookCatalogProvider extends ContentProvider {
    public static final String AUTHORITY = "com.weezlabs.databases.provider";
    private static final String SCHEME = "content://";
    private static final String UNKNOWN_URI = "Unknown URI";

    private static final String BOOKS_PATH = "books";
    private static final String COUNT_PATH = "count";
    private static final String AVAILABLE_PATH = "available";
    private static final String USERS_PATH = "users";
    private static final String GIVE_BOOK_PATH = "give";

    private static final int BOOKS = 10;
    private static final int BOOK_ID = 11;
    private static final int BOOKS_WITH_COUNT = 12;
    private static final int AVAILABLE_BOOKS = 13;

    private static final int USERS = 40;
    private static final int USER_ID = 41;
    private static final int USERS_WITH_BOOK_ID = 42;
    private static final int GIVE_BOOK_TO_USER = 43;

    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    public static final Uri BOOKS_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(BOOKS_PATH).build();
    public static final Uri USERS_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(USERS_PATH).build();

    private final static UriMatcher sUriMatcher = buildUriMatcher();

    private DatabaseHandler mDbHandler;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AUTHORITY;

        matcher.addURI(authority, "books", BOOKS);
        matcher.addURI(authority, "books/#", BOOK_ID);
        matcher.addURI(authority, "books/count", BOOKS_WITH_COUNT);
        matcher.addURI(authority, "books/available", AVAILABLE_BOOKS);
        matcher.addURI(authority, "users", USERS);
        matcher.addURI(authority, "users/#", USER_ID);
        matcher.addURI(authority, "users/books/#", USERS_WITH_BOOK_ID);
        matcher.addURI(authority, "users/books/give", GIVE_BOOK_TO_USER);


        return matcher;
    }

    public BookCatalogProvider() {
    }

    public static Uri buildBookIdUri(int bookId) {
        return BOOKS_CONTENT_URI.buildUpon().appendPath(String.valueOf(bookId)).build();
    }

    public static Uri buildBooksWithCountUri() {
        return BOOKS_CONTENT_URI.buildUpon().appendPath(COUNT_PATH).build();
    }

    public static Uri buildAvailableBooksUri() {
        return BOOKS_CONTENT_URI.buildUpon().appendPath(AVAILABLE_PATH).build();
    }

    public static Uri buildUserIdUri(int userId) {
        return USERS_CONTENT_URI.buildUpon().appendPath(String.valueOf(userId)).build();
    }

    public static Uri buildUsersWithBookUri(int bookId) {
        return USERS_CONTENT_URI.buildUpon()
                .appendPath(BOOKS_PATH)
                .appendPath(String.valueOf(bookId)).build();
    }

    public static Uri buildGiveBookToUserUri() {
        return USERS_CONTENT_URI.buildUpon()
                .appendPath(BOOKS_PATH)
                .appendPath(GIVE_BOOK_PATH).build();
    }

    @Override
    public boolean onCreate() {
        mDbHandler = new DatabaseHandler(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        Uri resultUri;
        long rowId;
        switch (match) {
            case BOOKS:
                rowId = mDbHandler.getWritableDatabase().insert(Book.TABLE, null, values);
                resultUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(resultUri, null);
                break;
            case USERS:
                rowId = mDbHandler.getWritableDatabase().insert(User.TABLE, null, values);
                resultUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(resultUri, null);
                break;
            case GIVE_BOOK_TO_USER:
                rowId = mDbHandler.getWritableDatabase().insert(UserBookLink.TABLE, null, values);
                resultUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(resultUri, null);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI);
        }
        return resultUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case BOOKS:
                count = mDbHandler.getWritableDatabase().update(Book.TABLE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case USERS:
                count = mDbHandler.getWritableDatabase().update(User.TABLE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case BOOK_ID:
                selection = getBookSelection(uri, selection);
                count = mDbHandler.getWritableDatabase().update(Book.TABLE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case USER_ID:
                selection = getUserSelection(uri, selection);
                count = mDbHandler.getWritableDatabase().update(User.TABLE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI);
        }
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int countRows;
        switch (match) {
            case BOOK_ID:
                selection = getBookSelection(uri, selection);
                countRows = mDbHandler.getWritableDatabase().delete(Book.TABLE, selection, selectionArgs);
                mDbHandler.getWritableDatabase().delete(UserBookLink.TABLE, selection, selectionArgs);
                // clean table of links
                String bookId = uri.getLastPathSegment();
                mDbHandler.getWritableDatabase().delete(UserBookLink.TABLE,
                        UserBookLink.BOOK_ID + "=" + bookId, selectionArgs);

                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case USER_ID:
                selection = getUserSelection(uri, selection);
                countRows = mDbHandler.getWritableDatabase().delete(User.TABLE, selection, selectionArgs);
                // clean table of links
                String userId = uri.getLastPathSegment();
                mDbHandler.getWritableDatabase().delete(UserBookLink.TABLE,
                        UserBookLink.USER_ID + "=" + userId, selectionArgs);

                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI);
        }
        return countRows;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        final int match = sUriMatcher.match(uri);
        String groupByString = "";
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (match) {
            case BOOKS:
                queryBuilder.setTables(Book.TABLE);
                break;
            case BOOK_ID:
                queryBuilder.setTables(Book.TABLE);
                queryBuilder.appendWhere(Book.ID + "=" + uri.getLastPathSegment());
                break;
            case BOOKS_WITH_COUNT:
                // TODO: set the queryBuilder
                cursor = mDbHandler.getReadableDatabase().rawQuery(createBooksWithCountRequest(), selectionArgs);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case AVAILABLE_BOOKS:
                // TODO: set the queryBuilder
                cursor = mDbHandler.getReadableDatabase().rawQuery(createBooksWithCountRequest()
                        + " WHERE " + Book.AMOUNT_ALIAS + ">0", selectionArgs);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case USERS:
                queryBuilder.setTables(User.TABLE);
                break;
            case USER_ID:
                queryBuilder.setTables(User.TABLE);
                queryBuilder.appendWhere(User.ID + "=" + uri.getLastPathSegment());
                break;
            case USERS_WITH_BOOK_ID:
                String bookId = uri.getLastPathSegment();
                groupByString = User.getTableColumn(User.ID);
                queryBuilder.setTables(User.TABLE + " INNER JOIN " + UserBookLink.TABLE
                        + " ON " + User.getTableColumn(User.ID)
                        + "=" + UserBookLink.getTableColumn(UserBookLink.USER_ID));
                queryBuilder.appendWhere(UserBookLink.getTableColumn(UserBookLink.BOOK_ID) + " = " + bookId);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI);

        }

        cursor = queryBuilder.query(mDbHandler.getReadableDatabase(), projection, selection,
                selectionArgs, groupByString, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    private String createBooksWithCountRequest() {
        return "SELECT *, "
                + Book.getTableColumn(Book.TOTAL_AMOUNT)
                + " - " +
                "(SELECT count(" + UserBookLink.getTableColumn(UserBookLink.ID)
                + ") FROM " + UserBookLink.TABLE
                + " WHERE " + UserBookLink.getTableColumn(UserBookLink.BOOK_ID)
                + " = " + Book.getTableColumn(Book.ID) + ") AS " + Book.AMOUNT_ALIAS
                + " FROM " + Book.TABLE;
    }

    private String getBookSelection(Uri uri, String selection) {
        String bookId = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
            selection = Book.ID + "=" + bookId;
        } else {
            selection = selection + " AND " + Book.ID + "=" + bookId;
        }
        return selection;
    }

    private String getUserSelection(Uri uri, String selection) {
        String userId = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
            selection = User.ID + "=" + userId;
        } else {
            selection = selection + " AND " + User.ID + "=" + userId;
        }
        return selection;
    }
}
