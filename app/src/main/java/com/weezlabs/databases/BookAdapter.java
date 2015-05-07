package com.weezlabs.databases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.weezlabs.databases.model.Book;

import java.util.List;

/**
 * Created by Andrey Bondarenko on 07.05.15.
 */
public class BookAdapter extends ArrayAdapter<Book> {
    private int mLayoutResource;

    public BookAdapter(Context context, int resource, List<Book> objects) {
        super(context, resource, objects);
        mLayoutResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mLayoutResource, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Book book = getItem(position);
        holder.title.setText(getContext().getString(R.string.label_book_row_title, book.getTitle()));
        holder.author.setText(getContext().getString(R.string.label_book_row_author, book.getAuthor()));
        Picasso.with(getContext())
                .load(book.getCoverPath())
                .placeholder(R.drawable.ic_book)
                .error(R.drawable.ic_book)
                .centerInside()
                .fit()
                .into(holder.cover);

        return convertView;
    }

    private static class ViewHolder {
        ImageView cover;
        TextView title;
        TextView author;

        public ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.cover_image_view);
            title = (TextView) view.findViewById(R.id.title_text_view);
            author = (TextView) view.findViewById(R.id.author_text_view);
        }
    }
}
