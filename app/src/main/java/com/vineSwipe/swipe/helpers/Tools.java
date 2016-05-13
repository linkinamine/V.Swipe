package com.vineSwipe.swipe.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.vineSwipe.swipe.data.Constants;

import java.io.ByteArrayOutputStream;

/**
 * Created by mbenallouch on 19/04/2016.
 */
public class Tools {

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";

    public static Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
    }

    public static byte[] convertBitmapToByteArray(Bitmap gifDrawable) {
        byte[] firstFrameBytes = null;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        boolean success = gifDrawable.compress(Bitmap.CompressFormat.PNG, Constants.COMPRESSION_RATE, byteStream);
        if (success) {
            firstFrameBytes = byteStream.toByteArray();
        }
        return firstFrameBytes;
    }

}
