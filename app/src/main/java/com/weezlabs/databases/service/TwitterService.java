package com.weezlabs.databases.service;

import com.weezlabs.databases.Config;
import com.weezlabs.databases.model.PostTweetResponse;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Andrey Bondarenko on 18.05.15.
 */
public class TwitterService {
    public static final String BASE_URL = "https://api.twitter.com/1.1";
    public static final String EMPTY_BODY = "{}";
    private TwitterApi mApi;

    public void init(String token, String tokenSecret) {
        RetrofitHttpOAuthConsumer oAuthConsumer = new RetrofitHttpOAuthConsumer(Config.CONSUMER_TWEET_API_KEY, Config.CONSUMER_TWEET_API_SECRET);
        oAuthConsumer.setTokenWithSecret(token, tokenSecret);
        OkClient client = new SigningOkClient(oAuthConsumer);
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(client)
                .build();
        mApi = adapter.create(TwitterApi.class);
    }

    public void postTweet(String tweet, Callback<PostTweetResponse> callback) {
        mApi.postTweet(tweet, EMPTY_BODY, callback);
    }
}
