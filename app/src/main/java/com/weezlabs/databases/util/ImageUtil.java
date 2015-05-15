package com.weezlabs.databases.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;

import com.weezlabs.databases.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;


public class ImageUtil {

    public static final String COVERS_FOLDER = "BooksCovers";
    public static final String IMAGE_FILE_EXTENSION = ".png";
    public static final String IMAGE_FILE_NAME_DELIMITER = "_";
    public static final int COMPRESS_QUALITY = 100;
    public static final String FILE_BEGIN = "file://";
    public static final int INCORRECT_MEASURE = -1;

    public static Bitmap getThumbnail(Context context, Uri uri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;  //optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;  //optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == INCORRECT_MEASURE)
                || (onlyBoundsOptions.outHeight == INCORRECT_MEASURE)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth)
                ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > context.getResources().getDimension(R.dimen.image_book_cover_height))
                ? (originalSize / context.getResources().getDimension(R.dimen.image_book_cover_height)) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;  //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;  //optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    public static boolean deleteOldCover(String coverPath) {
        if (!isExternalStorageWritable()) {
            return false;
        }
        File file = new File(coverPath.substring(FILE_BEGIN.length()));
        return file.exists() && file.delete();
    }

    public static String saveBitmapToFile(String author, String title, Bitmap coverBitmap) throws
            FileNotFoundException, IOException {
        String coverPath = null;
        if (!isExternalStorageWritable()) {
            return null;
        }
        File folder = new File(Environment.getExternalStorageDirectory(), COVERS_FOLDER);
        File coverFile;
        if (folder.mkdirs() || folder.isDirectory()) {
            String timeStamp = getTimeStamp();
            coverFile = new File(folder, author
                    + IMAGE_FILE_NAME_DELIMITER
                    + title
                    + IMAGE_FILE_NAME_DELIMITER
                    + timeStamp
                    + IMAGE_FILE_EXTENSION);
            if (coverFile.exists() && coverFile.delete()) {
                coverFile = new File(folder, author
                        + IMAGE_FILE_NAME_DELIMITER
                        + title
                        + IMAGE_FILE_NAME_DELIMITER
                        + timeStamp
                        + IMAGE_FILE_EXTENSION);
            }
            FileOutputStream outputStream = new FileOutputStream(coverFile);
            coverBitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, outputStream);
            outputStream.flush();
            outputStream.close();
            coverPath = FILE_BEGIN + coverFile.getAbsolutePath();
        }
        return coverPath;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) {
            return 1;
        } else {
            return k;
        }
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getTimeStamp() {
        CharSequence timeStamp = DateFormat.format("dd-MM-yyyy hh:mm:ss", new Date());
        return timeStamp.toString();
    }

}
