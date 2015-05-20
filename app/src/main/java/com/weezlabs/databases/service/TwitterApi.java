package com.weezlabs.databases.service;

import com.weezlabs.databases.model.PostTweetResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by cooper on 18.5.15.
 */
public interface TwitterApi {
    @POST("/statuses/update.json")
    void postTweet(@Query("status") String tweet, @Body String body, Callback<PostTweetResponse> callback);
}
