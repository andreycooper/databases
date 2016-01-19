package com.weezlabs.databases.task;

import android.content.Context;
import android.os.AsyncTask;

import com.weezlabs.databases.model.Book;

/**
 * Created by Andrey Bondarenko on 07.05.15.
 */
abstract public class BaseBookTask extends AsyncTask<Book, Void, Long> {
    protected Context mContext;
    protected OnTaskCompletedListener mListener;

    protected BaseBookTask(Context context) {
        mContext = context.getApplicationContext();
        mListener = null;
    }

    protected BaseBookTask(Context context, OnTaskCompletedListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        if (mListener != null) {
            mListener.onTaskCompleted();
        }
    }
}
