package com.weezlabs.databases;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.weezlabs.databases.service.RetrofitHttpOAuthConsumer;
import com.weezlabs.databases.util.PrefUtil;

import dmax.dialog.SpotsDialog;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;


public class TwitterLoginActivity extends AppCompatActivity {
    private final static String TAG = TwitterLoginActivity.class.getSimpleName();

    private final static String CALLBACK = "oauth://twitter";
    public static final String REQUEST_TOKEN_ENDPOINT_URL = "https://api.twitter.com/oauth/request_token";
    public static final String ACCESS_TOKEN_ENDPOINT_URL = "https://api.twitter.com/oauth/access_token";
    public static final String AUTHORIZATION_WEBSITE_URL = "https://api.twitter.com/oauth/authorize";

    private WebView mWebView;
    private String mAuthUrl;
    private AlertDialog mLoginDialog;
    private WebViewClient mWebViewClient;

    private OAuthProvider mProvider;
    private OAuthConsumer mConsumer;
    private RelativeLayout mWebViewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mLoginDialog = new SpotsDialog(this);
        mWebViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(CALLBACK)) {
                    Uri uri = Uri.parse(url);
                    onOAuthCallback(uri);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        };

        mWebView = new WebView(getApplicationContext());
        mWebViewLayout = (RelativeLayout) findViewById(R.id.webview_layout);
        mWebViewLayout.addView(mWebView);

        initTwitter();

        mWebView.setWebViewClient(mWebViewClient);

        if (!TextUtils.isEmpty(mAuthUrl)) {
            mWebView.loadUrl(mAuthUrl);
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
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initTwitter() {
        mConsumer = new RetrofitHttpOAuthConsumer(
                Config.CONSUMER_TWEET_API_KEY,
                Config.CONSUMER_TWEET_API_SECRET);

        mProvider = new DefaultOAuthProvider(
                REQUEST_TOKEN_ENDPOINT_URL,
                ACCESS_TOKEN_ENDPOINT_URL,
                AUTHORIZATION_WEBSITE_URL);


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                mLoginDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    mAuthUrl = mProvider.retrieveRequestToken(mConsumer, CALLBACK);
                    Log.d(TAG, "mAuthUrl " + mAuthUrl);

                } catch (final OAuthMessageSignerException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                }

                return null;
            }

            private void showErrorToUser(final OAuthException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TwitterLoginActivity.this,
                                getString(R.string.toast_error_login, e.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            protected void onPostExecute(Void v) {
                mLoginDialog.dismiss();
                if (!TextUtils.isEmpty(mAuthUrl)) {
                    mWebView.loadUrl(mAuthUrl);
                }
            }
        }.execute();
    }

    private void onOAuthCallback(final Uri uri) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                mLoginDialog.show();
            }

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
                    showErrorToUser(e);
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                }
                return null;
            }

            private void showErrorToUser(final OAuthException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TwitterLoginActivity.this,
                                getString(R.string.toast_error_login, e.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mLoginDialog.dismiss();
                finish();
            }
        }.execute();

    }

    @Override
    public void finish() {
        // prevent leak
        mWebViewLayout.removeAllViews();
        if (mWebView != null) {
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.loadUrl("about:blank");
            mWebView.pauseTimers();
            mWebView.setWebViewClient(null);
            mWebView = null;
        }
        super.finish();
    }
}
