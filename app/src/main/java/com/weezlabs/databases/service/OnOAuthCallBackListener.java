package com.weezlabs.databases.service;

import android.net.Uri;

/**
 * Created by Andrey Bondarenko on 19.05.15.
 */
public interface OnOAuthCallBackListener {
    void onOAuthCallback(Uri uri);
}
