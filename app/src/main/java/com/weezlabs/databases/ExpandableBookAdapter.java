package com.weezlabs.databases;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.weezlabs.databases.model.Book;

/**
 * Created by Andrey Bondarenko on 07.05.15.
 */
public class ExpandableBookAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private SparseArray<Book> mBooks;

    public ExpandableBookAdapter(Context context, SparseArray<Book> books) {
        mContext = context.getApplicationContext();
        mBooks = books;
    }

    @Override
    public int getGroupCount() {
        return mBooks.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Book getGroup(int groupPosition) {
        return mBooks.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return mBooks.get(groupPosition).getDescription();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mBooks.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        BookViewHolder bookHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.book_row, parent, false);
            bookHolder = new BookViewHolder(convertView);
            convertView.setTag(bookHolder);
        } else {
            bookHolder = (BookViewHolder) convertView.getTag();
        }

        final Book book = getGroup(groupPosition);
        bookHolder.title.setText(mContext.getString(R.string.label_book_row_title, book.getTitle()));
        bookHolder.author.setText(mContext.getString(R.string.label_book_row_author, book.getAuthor()));
        Picasso.with(mContext)
                .load(book.getCoverPath())
                .placeholder(R.drawable.ic_book)
                .error(R.drawable.ic_book)
                .centerInside()
                .fit()
                .into(bookHolder.cover);
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        DescriptionViewHolder descriptionHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.book_row, parent, false);
            descriptionHolder = new DescriptionViewHolder(convertView);
            convertView.setTag(descriptionHolder);
        } else {
            descriptionHolder = (DescriptionViewHolder) convertView.getTag();
        }

        String description = getChild(groupPosition, childPosition);
        if (TextUtils.isEmpty(description)) {
            descriptionHolder.description.setText("No description");
        } else {
            descriptionHolder.description.setText(description);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private static class BookViewHolder {
        ImageView cover;
        TextView title;
        TextView author;

        public BookViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.cover_image_view);
            title = (TextView) view.findViewById(R.id.title_text_view);
            author = (TextView) view.findViewById(R.id.author_text_view);
        }
    }

    private static class DescriptionViewHolder {
        TextView description;

        public DescriptionViewHolder(View view) {
            description = (TextView) view.findViewById(R.id.description_text_view);
        }

    }
}
