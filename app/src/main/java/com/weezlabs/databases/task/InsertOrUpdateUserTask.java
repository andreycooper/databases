package com.weezlabs.databases.task;

import android.content.Context;

import com.weezlabs.databases.db.DatabaseHandler;
import com.weezlabs.databases.model.User;

/**
 * Created by Andrey Bondarenko on 12.05.15.
 */
public class InsertOrUpdateUserTask extends BaseUserTask {
    private volatile boolean mIsUpdate;

    public InsertOrUpdateUserTask(Context context, boolean isUpdate) {
        super(context);
        mIsUpdate = isUpdate;
    }

    public InsertOrUpdateUserTask(Context context, OnTaskCompletedListener listener, boolean isUpdate) {
        super(context, listener);
        mIsUpdate = isUpdate;
    }

    @Override
    protected Long doInBackground(User... params) {
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        if (mIsUpdate) {
            return (long) databaseHandler.updateUser(params[0]);
        } else {
            return databaseHandler.addUser(params[0]);
        }
    }
}
