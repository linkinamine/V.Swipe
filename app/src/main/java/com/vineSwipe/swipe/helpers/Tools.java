package com.vineSwipe.swipe.helpers;

import android.content.Context;
import android.net.Uri;

/**
 * Created by mbenallouch on 19/04/2016.
 */
public class Tools {

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";

    public static Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
    }
}
