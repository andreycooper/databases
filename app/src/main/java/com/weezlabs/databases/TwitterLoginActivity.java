package com.weezlabs.databases;

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

import com.weezlabs.databases.service.OAuthTask;
import com.weezlabs.databases.service.OnOAuthCallBackListener;
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


public class TwitterLoginActivity extends AppCompatActivity implements OnOAuthCallBackListener {
    private final static String TAG = TwitterLoginActivity.class.getSimpleName();

    private static final String CALLBACK_SCHEME = "oauth";
    private static final String CALLBACK_HOST = "twitter";
    private static final String CALLBACK_URL = CALLBACK_SCHEME + "://" + CALLBACK_HOST;
    private static final String REQUEST_TOKEN_ENDPOINT_URL = "https://api.twitter.com/oauth/request_token";
    private static final String ACCESS_TOKEN_ENDPOINT_URL = "https://api.twitter.com/oauth/access_token";
    private static final String AUTHORIZATION_WEBSITE_URL = "https://api.twitter.com/oauth/authorize";

    private WebView mWebView;
    private String mAuthUrl;

    private OAuthProvider mProvider;
    private OAuthConsumer mConsumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mWebView = new WebView(this);
        RelativeLayout webViewLayout = (RelativeLayout) findViewById(R.id.webview_layout);
        webViewLayout.addView(mWebView);

        WebViewClient webViewClient = new OAuthWebViewClient(this);
        mWebView.setWebViewClient(webViewClient);

        initTwitter();

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
        mConsumer = new DefaultOAuthConsumer(
                Config.CONSUMER_TWEET_API_KEY,
                Config.CONSUMER_TWEET_API_SECRET);

        mProvider = new DefaultOAuthProvider(
                REQUEST_TOKEN_ENDPOINT_URL,
                ACCESS_TOKEN_ENDPOINT_URL,
                AUTHORIZATION_WEBSITE_URL);

        OAuthTask task = new OAuthTask(this) {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mAuthUrl = mProvider.retrieveRequestToken(mConsumer, CALLBACK_URL);
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

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (!TextUtils.isEmpty(mAuthUrl)) {
                    mWebView.loadUrl(mAuthUrl);
                }
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onOAuthCallback(final Uri uri) {

        OAuthTask task = new OAuthTask(this) {
            @Override
            protected Void doInBackground(Void... params) {
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

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                finish();
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class OAuthWebViewClient extends WebViewClient {
        OnOAuthCallBackListener mCallBackListener;

        public OAuthWebViewClient(OnOAuthCallBackListener listener) {
            mCallBackListener = listener;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if (uri.getScheme().equals(CALLBACK_SCHEME)) {
                mCallBackListener.onOAuthCallback(uri);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

}
