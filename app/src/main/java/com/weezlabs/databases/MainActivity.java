package com.weezlabs.databases;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.weezlabs.databases.model.Book;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<SparseArray<Book>> {
    private static final int BOOK_LIST_LOADER = 15052015;

    private SparseArray<Book> mBookArray = new SparseArray<>();
    private ExpandableBookAdapter mExpandableBookAdapter;
    private ExpandableListView mBooksListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBooksListView = (ExpandableListView) findViewById(R.id.books_listview);
        mExpandableBookAdapter = new ExpandableBookAdapter(this, mBookArray);
        mBooksListView.setAdapter(mExpandableBookAdapter);
        registerForContextMenu(mBooksListView);
        mBooksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

//
//
//        View emptyView = getLayoutInflater().inflate(R.layout.empty_list_view, null);
//        addContentView(emptyView,
//                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                        RelativeLayout.LayoutParams.MATCH_PARENT));
        loadBookList();
    }

    @Override
    protected void onResume() {
//        loadBookList();
        super.onResume();
    }

    public void onAddBookClick(View view) {
        startAddBookActivity();
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

    private void startAddBookActivity() {
        startAddBookActivity(null);
    }


    private void startAddBookActivity(Book book) {
        Intent intent = new Intent(getApplicationContext(), BookActivity.class);
        if (book != null) {
            intent.putExtra(BookActivity.BOOK_KEY, book);
        }
        startActivity(intent);
    }

    private void loadBookList() {
        Loader<SparseArray<Book>> loader = getLoaderManager().getLoader(BOOK_LIST_LOADER);
        if (loader == null) {
            loader = getLoaderManager().initLoader(BOOK_LIST_LOADER, null, this);
        } else {
            loader = getLoaderManager().restartLoader(BOOK_LIST_LOADER, null, this);
        }
        loader.forceLoad();
    }

    @Override
    public Loader<SparseArray<Book>> onCreateLoader(int id, Bundle args) {
        BookListLoader loader = null;
        if (id == BOOK_LIST_LOADER) {
            loader = new BookListLoader(this);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<SparseArray<Book>> loader, SparseArray<Book> data) {
        if (loader.getId() == BOOK_LIST_LOADER) {
            mBookArray = data;
            mExpandableBookAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<SparseArray<Book>> loader) {

    }
}
