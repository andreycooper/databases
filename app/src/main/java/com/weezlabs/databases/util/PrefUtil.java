package com.weezlabs.databases.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import oauth.signpost.OAuth;

/**
 * Created by Andrey Bondarenko on 18.05.15.
 */
public class PrefUtil {

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
