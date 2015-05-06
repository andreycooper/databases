package com.weezlabs.databases.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andrey Bondarenko on 06.05.15.
 */
public class Book implements Parcelable {
    public static final String TABLE = "books";

    public static final String ID = "_id";
    public static final String AUTHOR = "author";
    public static final String TITLE = "title";
    public static final String COVER_PATH = "cover_path";
    public static final String DESCRIPTION = "description";

    private int mId;
    private String mAuthor;
    private String mTitle;
    private String mCoverPath;
    private String mDescription;

    public Book() {
    }

    private Book(Parcel in) {
        this.mId = in.readInt();
        this.mAuthor = in.readString();
        this.mTitle = in.readString();
        this.mCoverPath = in.readString();
        this.mDescription = in.readString();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getCoverPath() {
        return mCoverPath;
    }

    public void setCoverPath(String coverPath) {
        mCoverPath = coverPath;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Book{");
        sb.append("mId=").append(mId);
        sb.append(", mAuthor='").append(mAuthor).append('\'');
        sb.append(", mTitle='").append(mTitle).append('\'');
        sb.append(", mCoverPath='").append(mCoverPath).append('\'');
        sb.append(", mDescription='").append(mDescription).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mAuthor);
        dest.writeString(this.mTitle);
        dest.writeString(this.mCoverPath);
        dest.writeString(this.mDescription);
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public static final class ValuesBuilder {
        private final ContentValues values = new ContentValues();

        public ValuesBuilder id(int id) {
            values.put(ID, id);
            return this;
        }

        public ValuesBuilder author(String author) {
            values.put(AUTHOR, author);
            return this;
        }

        public ValuesBuilder title(String title) {
            values.put(TITLE, title);
            return this;
        }

        public ValuesBuilder coverPath(String coverPath) {
            values.put(COVER_PATH, coverPath);
            return this;
        }

        public ValuesBuilder descriptuon(String description) {
            values.put(DESCRIPTION, description);
            return this;
        }

        public ContentValues build() {
            return values;
        }

    }
}