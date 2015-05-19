package com.weezlabs.databases.service;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.weezlabs.databases.R;

import dmax.dialog.SpotsDialog;
import oauth.signpost.exception.OAuthException;

/**
 * Created by Andrey Bondarenko on 19.05.15.
 */
public abstract class OAuthTask extends AsyncTask<Void, Void, Void> {
    private AlertDialog mDialog;
    private Context mContext;
    private Handler mHandler;


    public OAuthTask(Context context) {
        mContext = context.getApplicationContext();
        mDialog = new SpotsDialog(context);
        mHandler = new Handler(context.getMainLooper());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mDialog.dismiss();
    }

    protected void showErrorToUser(final OAuthException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext,
                        mContext.getString(R.string.toast_error_login, e.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }


}
