package com.weezlabs.databases;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;
import com.weezlabs.databases.model.Book;
import com.weezlabs.databases.task.InsertOrUpdateBookTask;
import com.weezlabs.databases.util.ImageUtil;

import java.io.FileNotFoundException;
import java.io.IOException;


public class BookActivity extends AppCompatActivity {

    public static final int REQUEST_CAMERA_CODE = 0;
    public static final int REQUEST_GALLERY_CODE = 1;
    private static final String LOG_TAG = BookActivity.class.getSimpleName();
    public static final String BOOK_KEY = "com.weezlabs.databases.BOOK";

    private ImageView mCoverImageView;
    private EditText mAuthorEditText;
    private EditText mTitleEditText;
    private EditText mDescriptionEditText;

    private Book mBook;
    private Bitmap mCoverBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            mBook = extra.getParcelable(BOOK_KEY);
        }

        mAuthorEditText = (EditText) findViewById(R.id.author_edit);
        mTitleEditText = (EditText) findViewById(R.id.title_edit);
        mDescriptionEditText = (EditText) findViewById(R.id.description_edit);
        mCoverImageView = (ImageView) findViewById(R.id.cover_image);

        if (mBook != null) {
            setTitle(getString(R.string.label_title_activity_edit_book));
            fillViews(mBook);
        }
    }

    private void fillViews(Book book) {
        mAuthorEditText.setText(book.getAuthor());
        mTitleEditText.setText(book.getTitle());
        Picasso.with(this)
                .load(book.getCoverPath())
                .placeholder(R.drawable.ic_book)
                .error(R.drawable.ic_book)
                .into(mCoverImageView);
        mDescriptionEditText.setText(book.getDescription());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA_CODE:
                    Bundle extras = data.getExtras();
                    mCoverBitmap = (Bitmap) extras.get("data");
                    mCoverImageView.setImageBitmap(mCoverBitmap);
                    break;
                case REQUEST_GALLERY_CODE:
                    Uri imageUri = data.getData();
                    try {
                        mCoverBitmap = ImageUtil.getThumbnail(getApplicationContext(), imageUri);
                        mCoverImageView.setImageBitmap(mCoverBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, "Can't load cover!");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void onCancelButtonClick(View view) {
        onBackPressed();
    }

    public void onOkButtonClick(View view) {
        String author = mAuthorEditText.getText().toString();
        String title = mTitleEditText.getText().toString();
        if (TextUtils.isEmpty(author)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_author_empty),
                    Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(title)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_title_empty),
                    Toast.LENGTH_SHORT).show();
        } else {
            String description = TextUtils.isEmpty(mDescriptionEditText.getText().toString())
                    ? null : mDescriptionEditText.getText().toString();
            String coverPath = null;
            try {
                if (mCoverBitmap != null) {
                    coverPath = ImageUtil.saveBitmapToFile(author, title, mCoverBitmap);
                }
            } catch (FileNotFoundException e) {
                // TODO: maybe alert the user?
                Log.d(LOG_TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                // TODO: maybe alert the user?
                Log.d(LOG_TAG, "Error accessing file: " + e.getMessage());
            }
            if (mBook == null) {
                mBook = new Book(author, title, coverPath, description);
                InsertOrUpdateBookTask insertBookTask = new InsertOrUpdateBookTask(this, false);
                insertBookTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mBook);
            } else {
                mBook.setAuthor(author);
                mBook.setTitle(title);
                mBook.setCoverPath(coverPath);
                mBook.setDescription(description);
                InsertOrUpdateBookTask updateBookTask = new InsertOrUpdateBookTask(this, true);
                updateBookTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mBook);
            }
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    public void onCoverImageViewClick(View view) {
        final CharSequence[] items = {getString(R.string.label_dialog_add_book_choice_camera),
                getString(R.string.label_dialog_add_book_choice_gallery)};
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle(getString(R.string.label_dialog_add_cover_title));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dispatchTakePhotoIntent();
                        break;
                    case 1:
                        dispatchPickPhotoIntent();
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        }).create().show();

    }

    private void dispatchPickPhotoIntent() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (pickPhotoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPhotoIntent, REQUEST_GALLERY_CODE);
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_gallery_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA_CODE);
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_camera_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
