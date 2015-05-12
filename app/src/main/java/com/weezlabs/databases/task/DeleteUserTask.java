package com.weezlabs.databases.task;

import android.content.Context;

import com.weezlabs.databases.db.DatabaseHandler;
import com.weezlabs.databases.model.User;

/**
 * Created by Andrey Bondarenko on 12.05.15.
 */
public class DeleteUserTask extends BaseUserTask {
    public DeleteUserTask(Context context, OnTaskCompletedListener listener) {
        super(context, listener);
    }

    @Override
    protected Long doInBackground(User... params) {
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        return (long) databaseHandler.deleteUser(params[0]);
    }
}
