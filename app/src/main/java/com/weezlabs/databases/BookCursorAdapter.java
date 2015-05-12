package com.weezlabs.databases;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.weezlabs.databases.model.Book;

/**
 * Created by Andrey Bondarenko on 08.05.15.
 */
public class BookCursorAdapter extends CursorAdapter {
    private SparseArray<Boolean> mOpenDescriptions;

    public BookCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
        mOpenDescriptions = new SparseArray<>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.book_row, parent, false);
        ViewHolder holder = new ViewHolder(rowView);
        rowView.setTag(holder);
        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        final Book book = Book.getBookFromCursor(cursor);
        holder.title.setText(context.getString(R.string.label_book_row_title, book.getTitle()));
        holder.author.setText(context.getString(R.string.label_book_row_author, book.getAuthor()));
        Picasso.with(context)
                .load(book.getCoverPath())
                .placeholder(R.drawable.ic_book)
                .error(R.drawable.ic_book)
                .centerInside()
                .fit()
                .into(holder.cover);

        holder.totalAmount.setText(context.getString(R.string.label_book_row_total_amount, book.getTotalAmount()));
        holder.availableAmount.setText(context.getString(R.string.label_book_row_available_amount,
                cursor.getInt(cursor.getColumnIndex(Book.AMOUNT_ALIAS))));

        if (TextUtils.isEmpty(book.getDescription())) {
            holder.description.setText(context.getString(R.string.label_description_text_empty));
        } else {
            holder.description.setText(book.getDescription());
        }
        setDescriptionVisibility(holder, book.getId());

    }

    public void setDescriptionVisibility(ViewHolder holder, int bookId) {
        if (isDescriptionOpened(bookId)) {
            holder.descriptionView.setVisibility(View.VISIBLE);
        } else {
            holder.descriptionView.setVisibility(View.GONE);
        }
    }

    public Book getBook(int clickedPosition) {
        return Book.getBookFromCursor((Cursor) getItem(clickedPosition));
    }

    public void setDescriptionOpened(int bookId) {
        Boolean isOpened = mOpenDescriptions.get(bookId);
        if (isOpened == null) {
            mOpenDescriptions.put(bookId, true);
        } else {
            mOpenDescriptions.put(bookId, !isOpened);
        }
    }

    public boolean isDescriptionOpened(int bookId) {
        Boolean isOpened = mOpenDescriptions.get(bookId);
        if (isOpened == null) {
            return false;
        }
        return isOpened;
    }

    public static class ViewHolder {
        ImageView cover;
        TextView title;
        TextView author;
        TextView description;
        TextView totalAmount;
        TextView availableAmount;
        View divider;
        View descriptionView;

        public ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.cover_image_view);
            title = (TextView) view.findViewById(R.id.title_text_view);
            author = (TextView) view.findViewById(R.id.author_text_view);
            description = (TextView) view.findViewById(R.id.description_text_view);
            totalAmount = (TextView) view.findViewById(R.id.total_amount_text_view);
            availableAmount = (TextView) view.findViewById(R.id.available_amount_text_view);
            divider = view.findViewById(R.id.amount_divider);
            descriptionView = view.findViewById(R.id.description_layout);
        }
    }


}
