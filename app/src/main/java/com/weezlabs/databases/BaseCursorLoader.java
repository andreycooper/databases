package com.weezlabs.databases;

import android.content.Context;
import android.content.CursorLoader;

import com.weezlabs.databases.db.DatabaseHandler;

/**
 * Created by Andrey Bondarenko on 13.05.15.
 */
abstract public class BaseCursorLoader extends CursorLoader {
    protected DatabaseHandler mDatabaseHandler;

    public BaseCursorLoader(Context context) {
        super(context);
        mDatabaseHandler = new DatabaseHandler(context.getApplicationContext());
    }

}
