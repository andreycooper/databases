package com.weezlabs.databases.task;

import android.content.Context;
import android.os.AsyncTask;

import com.weezlabs.databases.model.User;

/**
 * Created by Andrey Bondarenko on 12.05.15.
 */
abstract public class BaseUserTask extends AsyncTask<User, Void, Long> {
    protected Context mContext;
    protected OnTaskCompletedListener mListener;

    public BaseUserTask(Context context) {
        mContext = context.getApplicationContext();
        mListener = null;
    }

    public BaseUserTask(Context context, OnTaskCompletedListener listener) {
        this(context);
        mListener = listener;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        if (mListener != null) {
            mListener.onTaskCompleted();
            mListener = null;
        }
    }
}
