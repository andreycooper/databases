package com.weezlabs.databases.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import oauth.signpost.OAuth;

/**
 * Created by Andrey Bondarenko on 18.05.15.
 */
public class PrefUtil {

    private static final String TAG = PrefUtil.class.getSimpleName();

    public static boolean isAuthenticated(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String token = prefs.getString(OAuth.OAUTH_TOKEN, null);
        String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, null);
        Log.d(TAG, "token = " + token);
        Log.d(TAG, "secret = " + secret);
        return !TextUtils.isEmpty(token) && !TextUtils.isEmpty(secret);
    }

    public static void setTwitterToken(Context context, String token) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(OAuth.OAUTH_TOKEN, token);
        editor.apply();
    }

    public static void setTwitterTokenSecret(Context context, String tokenSecret) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(OAuth.OAUTH_TOKEN_SECRET, tokenSecret);
        editor.apply();
    }

    public static String getTwitterToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(OAuth.OAUTH_TOKEN, null);
    }

    public static String getTwitterTokenSecret(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(OAuth.OAUTH_TOKEN_SECRET, null);
    }
}
