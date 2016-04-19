package com.vineSwipe.swipe.net.giphy;

import com.android.volley.Response;
import com.vineSwipe.swipe.net.giphy.model.GiphyResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * A request to the Giphy API for trending GIFs
 *
 * Created by alex on 05/10/15.
 */
public class TrendingRequest extends GiphyRequest {
    public TrendingRequest(String limit,Response.Listener<GiphyResponse> listener, Response.ErrorListener errorListener) {
        super("gifs/trending", buildQuery(limit), listener, errorListener);
    }

    private static Map<String, String> buildQuery(String limit) {
        HashMap<String, String> query = new HashMap<>();
        query.put("limit", limit);
        return query;
    }
}
