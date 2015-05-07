package com.weezlabs.databases;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.SparseArray;

import com.weezlabs.databases.db.DbHandler;
import com.weezlabs.databases.model.Book;

import java.util.List;

/**
 * Created by Andrey Bondarenko on 07.05.15.
 */
public class BookListLoader extends AsyncTaskLoader<SparseArray<Book>> {
    public BookListLoader(Context context) {
        super(context);
    }

    @Override
    public SparseArray<Book> loadInBackground() {
        DbHandler dbHandler = new DbHandler(getContext());
        List<Book> bookList = dbHandler.getAllBooks();
        SparseArray<Book> bookSparseArray = new SparseArray<>(bookList.size());
        for (int i = 0; i < bookList.size(); i++) {
            bookSparseArray.append(i, bookList.get(i));
        }
        return bookSparseArray;
    }
}
