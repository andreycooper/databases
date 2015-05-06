package com.weezlabs.databases;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.weezlabs.databases.util.ImageUtil;

import java.io.IOException;


public class AddBookActivity extends AppCompatActivity {

    public static final int REQUEST_CAMERA_CODE = 0;
    public static final int REQUEST_GALLERY_CODE = 1;
    private static final String LOG_TAG = AddBookActivity.class.getSimpleName();

    private ImageView mCoverImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        }

        mCoverImageView = (ImageView) findViewById(R.id.cover_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA_CODE:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mCoverImageView.setImageBitmap(imageBitmap);
                    break;
                case REQUEST_GALLERY_CODE:
                    Uri imageUri = data.getData();
                    try {
                        mCoverImageView.setImageBitmap(ImageUtil.getThumbnail(getApplicationContext(), imageUri));
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
                    "Can't open app for choose cover!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA_CODE);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Can't start camera app!",
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
