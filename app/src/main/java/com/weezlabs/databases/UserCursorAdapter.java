package com.weezlabs.databases;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Andrey Bondarenko on 11.05.15.
 */
public class UserCursorAdapter extends CursorAdapter {
    public UserCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    public static class ViewHolder {
        TextView userName;

        public ViewHolder(View view) {
            this.userName = (TextView) view.findViewById(R.id.username_text_view);
        }
    }
}
