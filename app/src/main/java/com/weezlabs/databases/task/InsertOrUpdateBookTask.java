package com.weezlabs.databases.task;

import android.content.Context;
import android.widget.Toast;

import com.weezlabs.databases.db.DatabaseHandler;
import com.weezlabs.databases.model.Book;

/**
 * Created by Andrey Bondarenko on 07.05.15.
 */
public class InsertOrUpdateBookTask extends BaseBookTask {

    private volatile boolean mIsUpdate;

    public InsertOrUpdateBookTask(Context context, boolean isUpdate) {
        super(context);
        mIsUpdate = isUpdate;
    }

    public InsertOrUpdateBookTask(Context context, boolean isUpdate, OnTaskCompletedListener listener) {
        this(context, isUpdate);
        mListener = listener;
    }

    @Override
    protected Long doInBackground(Book... params) {
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        if (mIsUpdate) {
            return (long) databaseHandler.updateBook(params[0]);
        } else {
            return databaseHandler.addBook(params[0]);
        }
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
        if (mIsUpdate) {
            Toast.makeText(mContext, "Book updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Book inserted to Library", Toast.LENGTH_SHORT).show();
        }
    }
}
