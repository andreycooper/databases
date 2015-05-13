package com.weezlabs.databases;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.weezlabs.databases.db.DatabaseHandler;
import com.weezlabs.databases.model.Book;


public class BookDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int USER_WHO_TAKE_BOOK_LOADER = 2112004;

    private Book mBook;

    private UserCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            mBook = extra.getParcelable(BookActivity.BOOK_KEY);
        }

        mCursorAdapter = new UserCursorAdapter(getApplicationContext(), null, true, R.layout.user_row_book_detail);

        if (mBook != null) {
            iniViews();
        } else {
            finish();
        }

        getLoaderManager().initLoader(USER_WHO_TAKE_BOOK_LOADER, null, this);

    }

    private void iniViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_book_details, mBook.getTitle()));
        }
        ImageView coverImageView = (ImageView) findViewById(R.id.cover_image_view);
        Picasso.with(this)
                .load(mBook.getCoverPath())
                .placeholder(R.drawable.ic_book)
                .error(R.drawable.ic_book)
                .into(coverImageView);
        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        titleTextView.setText(getString(R.string.label_book_details_title, mBook.getTitle()));
        TextView authorTextView = (TextView) findViewById(R.id.author_text_view);
        authorTextView.setText(getString(R.string.label_book_details_author, mBook.getAuthor()));
        TextView descriptionTextView = (TextView) findViewById(R.id.description_text_view);
        if (TextUtils.isEmpty(mBook.getDescription())) {
            descriptionTextView.setText(getString(R.string.label_description_text_empty));
        } else {
            descriptionTextView.setText(mBook.getDescription());
        }
        descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        ListView usersListView = (ListView) findViewById(R.id.user_list_view);
        usersListView.setEmptyView(findViewById(R.id.user_list_empty));
        usersListView.setAdapter(mCursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case USER_WHO_TAKE_BOOK_LOADER:
                return new UsersTakeBookCursorLoader(this, mBook.getId());
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case USER_WHO_TAKE_BOOK_LOADER:
                mCursorAdapter.changeCursor(data);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case USER_WHO_TAKE_BOOK_LOADER:
                mCursorAdapter.changeCursor(null);
                break;
            default:
                break;
        }
    }

    private static class UsersTakeBookCursorLoader extends CursorLoader {
        DatabaseHandler mDatabaseHandler;
        int mBookId;

        public UsersTakeBookCursorLoader(Context context, int bookId) {
            super(context.getApplicationContext());
            mDatabaseHandler = new DatabaseHandler(context.getApplicationContext());
            mBookId = bookId;
        }

        @Override
        public Cursor loadInBackground() {
            return mDatabaseHandler.getUsersWhoTakeBook(mBookId);
        }
    }
}
