package com.weezlabs.databases;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.weezlabs.databases.util.PrefUtil;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;


public class TwitterLoginActivity extends AppCompatActivity {
    private final static String TAG = TwitterLoginActivity.class.getSimpleName();
    private final static String CALLBACK = "oauth://twitter";

    private WebView mWebView;
    private String mAuthUrl;

    private OAuthProvider mProvider;
    private OAuthConsumer mConsumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_login);

        initTwitter();

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("oauth")) {
                    Uri uri = Uri.parse(url);
                    onOAuthCallback(uri);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        if (TextUtils.isEmpty(mAuthUrl)) {
            mWebView.loadUrl(mAuthUrl);
        }

    }

    private void initTwitter() {
        mConsumer = new DefaultOAuthConsumer(
                Config.TWEET_API_KEY,
                Config.TWEET_API_SECRET);

        mProvider = new DefaultOAuthProvider(
                "https://api.twitter.com/oauth/request_token",
                "https://api.twitter.com/oauth/access_token",
                "https://api.twitter.com/oauth/authorize");


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {


                try {
                    mAuthUrl = mProvider.retrieveRequestToken(mConsumer, CALLBACK);
                    Log.d(TAG, "mAuthUrl " + mAuthUrl);

                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {

                if (!TextUtils.isEmpty(mAuthUrl)) {
                    mWebView.loadUrl(mAuthUrl);
                }
            }
        }.execute();
    }

    private void onOAuthCallback(final Uri uri) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                String pinCode = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
                try {
                    mProvider.retrieveAccessToken(mConsumer, pinCode);

                    String token = mConsumer.getToken();
                    String tokenSecret = mConsumer.getTokenSecret();

                    PrefUtil.setTwitterToken(getApplicationContext(), token);
                    PrefUtil.setTwitterTokenSecret(getApplicationContext(), tokenSecret);

                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                finish();
            }
        }.execute();

    }


}