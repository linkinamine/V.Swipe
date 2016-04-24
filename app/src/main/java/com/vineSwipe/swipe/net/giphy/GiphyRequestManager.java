package com.vineSwipe.swipe.net.giphy;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vineSwipe.swipe.data.Constants;
import com.vineSwipe.swipe.helpers.StubHelper;
import com.vineSwipe.swipe.net.NetworkManager;
import com.vineSwipe.swipe.net.giphy.model.GiphyResponse;

/**
 * Created by mac on 4/23/16.
 */
public class GiphyRequestManager {

    private static GiphyResponse giphyResponse = null;

    public static GiphyResponse loadRecent(final Context context) {

        Log.d(Constants.TAG, " loadRecent ");
        Request trendingRequest = new TrendingRequest(String.valueOf(Constants.NUMBEROFCARDS).toString(), new Response.Listener<GiphyResponse>() {
            @Override
            public void onResponse(GiphyResponse response) {
                // mAdapter.showResults("Trending", response.getImages());
                Log.d(Constants.TAG, " onResponse :" + response);
                giphyResponse = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Constants.TAG, "Unhandled error! " + error);

                StubHelper.showYouBrokeItDialog(
                        "Couldn't load search results. Are you connected to the internet?",
                        new Runnable() {
                            @Override
                            public void run() {
                                loadRecent(context);
                            }
                        },
                        context);
            }
        });

        NetworkManager.getInstance(context).addToRequestQueue(trendingRequest);

        Log.d(Constants.TAG, " returning :" + giphyResponse);
        return giphyResponse;
    }
}
