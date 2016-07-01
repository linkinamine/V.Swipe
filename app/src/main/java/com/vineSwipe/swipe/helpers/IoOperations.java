package com.vineSwipe.swipe.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.vineSwipe.swipe.data.Constants;
import com.vineSwipe.swipe.data.ImageData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mbenallouch on 10/05/2016.
 */

public class IoOperations {

    private static List<String> fileIds = new ArrayList<String>();

    private static File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
            + "/Android/data/" + Constants.TAG
            + "/Files");

    public static void writeRecordsToFile(ImageData imageData, Context context) {
        File pictureFile = getOutputMediaFile(imageData.getId(), context);
        if (pictureFile == null) {
            Log.d(Constants.TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            imageData.getFirstFrame().compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(Constants.TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(Constants.TAG, "Error accessing file: " + e.getMessage());
        }
    }

    public static List<Bitmap> readRecordsFromFile(Context context) {

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File[] files = mediaStorageDir.listFiles();
        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                final Bitmap bitmap = BitmapFactory.decodeFile(files[i].getName(), options);
                fileIds.add(files[i].getName().replace(".jpg", ""));
                bitmaps.add(bitmap);

            }
        }

        Log.e(Constants.TAG, " " + bitmaps.size());


        return bitmaps;
    }

    public static List<String> getFileIds() {

        return fileIds;

    }


    public static void leftSwipedFilesIds(String fileId) {
        fileIds.add(fileId);
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(String imageName, Context context) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.


        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName = imageName + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

}