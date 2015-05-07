package com.weezlabs.databases.task;

import android.content.Context;

import com.weezlabs.databases.model.Book;

/**
 * Created by Andrey Bondarenko on 07.05.15.
 */
public class DeleteBookTask extends BaseBookTask {
    public DeleteBookTask(Context context) {
        super(context);
    }

    public DeleteBookTask(Context context, OnTaskCompletedListener listener) {
        super(context, listener);
    }

    @Override
    protected Long doInBackground(Book... params) {
        return null;
    }
}
