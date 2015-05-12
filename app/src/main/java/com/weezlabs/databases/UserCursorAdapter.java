package com.weezlabs.databases;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.weezlabs.databases.model.User;

/**
 * Created by Andrey Bondarenko on 11.05.15.
 */
public class UserCursorAdapter extends CursorAdapter {
    public UserCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.user_row, parent, false);
        ViewHolder holder = new ViewHolder(rowView);
        rowView.setTag(holder);
        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        final User user = User.getUserFromCursor(cursor);
        holder.userName.setText(user.getName());
    }

    public User getUser(int clickedPosition) {
        return User.getUserFromCursor((Cursor) getItem(clickedPosition));
    }

    public static class ViewHolder {
        TextView userName;

        public ViewHolder(View view) {
            this.userName = (TextView) view.findViewById(R.id.username_text_view);
        }
    }
}
