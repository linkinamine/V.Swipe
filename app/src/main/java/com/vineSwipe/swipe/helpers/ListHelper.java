package com.vineSwipe.swipe.helpers;

import android.util.Log;

import com.vineSwipe.swipe.data.Constants;
import com.vineSwipe.swipe.net.giphy.model.GiphyImage;

import java.util.List;

/**
 * Created by mac on 7/7/16.
 */
public class ListHelper {

    public static List<GiphyImage> filterAlreadySwiped(List<GiphyImage> images, List<String> localImageIds) {
        Log.d(Constants.TAG, " filterAlreadySwiped  ");

        Log.d(Constants.TAG, " images size before  " + images.size());

        for (int i = 0; i < localImageIds.size(); i++) {
            for (int j = 0; j < images.size(); j++) {
                Log.d(Constants.TAG, "local images:   " + localImageIds.get(i));
                Log.d(Constants.TAG, " images:   " + images.get(j).getId());

                if (localImageIds.get(i).equals(images.get(j).getId())) {
                    Log.d(Constants.TAG, "removing cards");
                    images.remove(j);
                }
            }
        }
        Log.d(Constants.TAG, " images size after  " + images.size());

        return images;

    }

    public static Boolean isListSetup(List<?> myList) {
        Log.d(Constants.TAG, " isListSetup  " + myList.size());

        return (myList != null && myList.size() > 0);
    }

}
