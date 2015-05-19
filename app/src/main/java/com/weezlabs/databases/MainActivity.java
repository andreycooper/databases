package com.weezlabs.databases;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weezlabs.databases.model.Book;
import com.weezlabs.databases.model.PostTweetResponse;
import com.weezlabs.databases.service.TwitterService;
import com.weezlabs.databases.util.PrefUtil;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BOOKS_LOADER = 15052015;
    private static final int REQUEST_BOOK_ACTIVITY = 113;
    private static final int REQUEST_USERS_ACTIVITY = 331;

    private static final int TWEET_MAX_CHARS = 140;
    public static final int UNAUTHORIZED_CODE = 401;

    private BookCursorAdapter mBookCursorAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerPlaceHolder;
    private int mCountCharacters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookCursorAdapter = new BookCursorAdapter(this, null, true);
        getLoaderManager().initLoader(BOOKS_LOADER, null, this);

        initListView();

        initDrawer();
    }

    private void initListView() {
        ListView booksListView = (ListView) findViewById(R.id.books_listview);
        booksListView.setEmptyView(findViewById(R.id.empty_view));
        booksListView.setAdapter(mBookCursorAdapter);

        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int bookId = mBookCursorAdapter.getBook(position).getId();
                mBookCursorAdapter.setDescriptionOpened(bookId);

                BookCursorAdapter.ViewHolder holder = (BookCursorAdapter.ViewHolder) view.getTag();
                mBookCursorAdapter.setDescriptionVisibility(holder, bookId);
            }
        });

        booksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Book book = mBookCursorAdapter.getBook(position);
                PopupMenu popupMenu = new PopupMenu(getActivity(), view, GravityCompat.END);
                popupMenu.getMenuInflater().inflate(R.menu.menu_book_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.action_book_details:
                                startBookDetailsActivity(book);
                                break;
                            case R.id.action_edit_book:
                                startBookActivity(book);
                                break;
                            case R.id.action_delete_book:
                                getContentResolver().delete(BookCatalogProvider.buildBookIdUri(book.getId()), null, null);
                                loadBookCursor();
                                break;
                            case R.id.action_share_book:
                                if (!PrefUtil.isAuthenticated(getApplicationContext())) {
                                    Intent intent = new Intent(getActivity(), TwitterLoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    showTweetDialog(book);
                                }
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
    }

    private void initDrawer() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mDrawerPlaceHolder = (RelativeLayout) findViewById(R.id.layout_drawer);

        String[] sectionTitles = getResources().getStringArray(R.array.drawer_sections);
        final ListView drawerList = (ListView) findViewById(R.id.list_drawer);
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_row, R.id.section_label, sectionTitles));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.title_activity_main_drawer));
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.title_activity_main));
                }
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mDrawerLayout.closeDrawer(mDrawerPlaceHolder);
                        break;
                    case 1:
                        mDrawerLayout.closeDrawer(mDrawerPlaceHolder);
                        startUsersActivity();
                        break;
                    case 2:
                        mDrawerLayout.closeDrawer(mDrawerPlaceHolder);
                        startBookActivity();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private Activity getActivity() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_BOOK_ACTIVITY || requestCode == REQUEST_USERS_ACTIVITY)
                && resultCode == RESULT_OK) {
            loadBookCursor();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        if (mDrawerLayout != null && mDrawerPlaceHolder != null) {
            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerPlaceHolder);
            menu.findItem(R.id.action_add_book).setVisible(!drawerOpen);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        if (id == R.id.action_add_book) {
            startBookActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showTweetDialog(Book book) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        View inflatedView = LayoutInflater.from(this).inflate(R.layout.dialog_post_tweet, null);

        final EditText tweetEdit = (EditText) inflatedView.findViewById(R.id.tweet_edit_text);
        final TextView tweetCountText = (TextView) inflatedView.findViewById(R.id.tweet_chars_count_text);

        tweetEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(TWEET_MAX_CHARS)});
        tweetEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCountCharacters = TWEET_MAX_CHARS - tweetEdit.getText().toString().length();
                tweetCountText.setText(getString(R.string.tweet_characters_left, mCountCharacters));
            }
        });
        tweetEdit.setText(getString(R.string.tweet_message, book.getTitle(), book.getAuthor()));
        tweetEdit.setSelection(tweetEdit.length());

        builder.setTitle(getString(R.string.title_dialog_tweet));
        builder.setView(inflatedView);
        builder.setPositiveButton(R.string.label_dialog_tweet_send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mCountCharacters >= 0) {
                    postTweet(tweetEdit.getText().toString());
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_tweet_too_long),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.label_dialog_tweet_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void postTweet(String tweet) {
        TwitterService service = new TwitterService();
        service.init(PrefUtil.getTwitterToken(getApplicationContext()),
                PrefUtil.getTwitterTokenSecret(getApplicationContext()));

        // Retrofit request with Callback executes async on THREAD_POOL_EXECUTOR
        // and Callback executes on UI Thread
        service.postTweet(tweet, new Callback<PostTweetResponse>() {
            @Override
            public void success(PostTweetResponse postTweetResponse, Response response) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_tweet_success,
                        postTweetResponse.getId()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_tweet_failure,
                        error.getResponse().getReason()), Toast.LENGTH_SHORT).show();
                if (error.getResponse().getStatus() == UNAUTHORIZED_CODE) {
                    PrefUtil.resetTwitterOAuth(getApplicationContext());
                }
            }
        });
    }

    public void onAddBookClick(View view) {
        startBookActivity();
    }

    private void startBookActivity() {
        startBookActivity(null);
    }

    private void startBookActivity(Book book) {
        Intent intent = new Intent(getApplicationContext(), BookActivity.class);
        if (book != null) {
            intent.putExtra(BookActivity.BOOK_KEY, book);
        }
        startActivityForResult(intent, REQUEST_BOOK_ACTIVITY);
    }

    private void startBookDetailsActivity(Book book) {
        Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
        if (book != null) {
            intent.putExtra(BookActivity.BOOK_KEY, book);
        }
        startActivity(intent);
    }

    private void startUsersActivity() {
        Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
        startActivityForResult(intent, REQUEST_USERS_ACTIVITY);
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
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case BOOKS_LOADER:
                return new CursorLoader(this, BookCatalogProvider.buildBooksWithCountUri(),
                        Book.PROJECTION_ALL, null, null, null);
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

}
