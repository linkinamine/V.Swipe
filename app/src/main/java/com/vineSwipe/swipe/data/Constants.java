package com.vineSwipe.swipe.data;

import android.net.Uri;

public final class Constants {
    public static final Uri GIPHY_API_STUB = Uri.parse("http://api.giphy.com/v1/");
    /**
     * Giphy's public beta key. Not special or secret in any way.
     * See: https://github.com/Giphy/GiphyAPI#public-beta-key
     */
    public static final String GIPHY_KEY = "dc6zaTOxFJmzC";

    public static int NUMBEROFCARDS = 4;
    public static int NUMBEROFCARDSOFFSET = 5;
    public static int NUMBEROFCARDTHRESHOLD = 2;
    public static int CARD_WIDTH = 320;
    public static int CARD_HEIGHT = 300;

    public static int COMPRESSION_RATE = 10;

    public static String TAG = "com.vineSwipe.swipe";

    public static final boolean LOADRECENT = true;


}
