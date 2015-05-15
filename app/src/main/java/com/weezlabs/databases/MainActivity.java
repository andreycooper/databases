package com.weezlabs.databases;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.weezlabs.databases.model.Book;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BOOKS_LOADER = 15052015;
    private static final int REQUEST_BOOK_ACTIVITY = 113;
    private static final int REQUEST_USERS_ACTIVITY = 331;

    private BookCursorAdapter mBookCursorAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerPlaceHolder;

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
