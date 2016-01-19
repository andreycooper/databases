package com.weezlabs.databases;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.weezlabs.databases.db.DbHandler;
import com.weezlabs.databases.model.Book;
import com.weezlabs.databases.task.DeleteBookTask;
import com.weezlabs.databases.task.OnTaskCompletedListener;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        OnTaskCompletedListener {
    private static final int BOOKS_LOADER = 15052015;
    private static final int REQUEST_BOOK_ACTIVITY = 113;

    private BookCursorAdapter mBookCursorAdapter;
    private ListView mBooksListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookCursorAdapter = new BookCursorAdapter(this, null, true);

        mBooksListView = (ListView) findViewById(R.id.books_listview);
        mBooksListView.setEmptyView(findViewById(R.id.empty_view));
        mBooksListView.setAdapter(mBookCursorAdapter);

        mBooksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int bookId = mBookCursorAdapter.getBook(position).getId();
                mBookCursorAdapter.setDescriptionOpened(bookId);

                BookCursorAdapter.ViewHolder holder = (BookCursorAdapter.ViewHolder) view.getTag();
                mBookCursorAdapter.setDescriptionVisibility(holder, bookId);
            }
        });

        final OnTaskCompletedListener taskCompletedListener = this;

        mBooksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Book book = mBookCursorAdapter.getBook(position);
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_book_context, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.action_edit_book:
                                startAddBookActivity(book);
                                break;
                            case R.id.action_delete_book:
                                DeleteBookTask deleteBookTask =
                                        new DeleteBookTask(getApplicationContext(), taskCompletedListener);
                                deleteBookTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, book);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });

        getLoaderManager().initLoader(BOOKS_LOADER, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BOOK_ACTIVITY && resultCode == RESULT_OK) {
            loadBookCursor();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_book) {
            startAddBookActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddBookClick(View view) {
        startAddBookActivity();
    }

    private void startAddBookActivity() {
        startAddBookActivity(null);
    }


    private void startAddBookActivity(Book book) {
        Intent intent = new Intent(getApplicationContext(), BookActivity.class);
        if (book != null) {
            intent.putExtra(BookActivity.BOOK_KEY, book);
        }
        startActivityForResult(intent, REQUEST_BOOK_ACTIVITY);
    }

    private void loadBookCursor() {
        Loader<Cursor> loader = getLoaderManager().getLoader(BOOKS_LOADER);
        if (loader == null) {
            loader = getLoaderManager().initLoader(BOOKS_LOADER, null, this);
        } else {
            loader = getLoaderManager().restartLoader(BOOKS_LOADER, null, this);
        }
        loader.forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case BOOKS_LOADER:
                return new BookCursorLoader(this);
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case BOOKS_LOADER:
                mBookCursorAdapter.changeCursor(data);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case BOOKS_LOADER:
                mBookCursorAdapter.changeCursor(null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onTaskCompleted() {
        loadBookCursor();
    }

    /**
     * Created by Andrey Bondarenko on 08.05.15.
     */
    public static class BookCursorLoader extends CursorLoader {
        DbHandler mDbHandler;

        public BookCursorLoader(Context context) {
            super(context);
            mDbHandler = new DbHandler(context);
        }

        @Override
        public Cursor loadInBackground() {
            return mDbHandler.getAllBooksCursor();
        }
    }
}
