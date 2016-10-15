package com.vineSwipe.swipe.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.vineSwipe.swipe.data.Constants;
import com.vineSwipe.swipe.data.ImageData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by mbenallouch on 10/05/2016.
 */

public class IoOperations {

    private static List<String> fileIds = new ArrayList<String>();

    private static File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
            + "/Android/data/" + Constants.TAG
            + "/Files");

    public static void writeRecordsToFile(List<ImageData> imagesData, Context context, boolean isRightSwiped) {

        Log.d(Constants.TAG, "writeRecordsToFile : " + imagesData.size());
        for (int i = 0; i < imagesData.size(); i++) {

            File pictureFile = getOutputMediaFile(imagesData.get(i).getId(), context, isRightSwiped);
            if (pictureFile == null) {
                Log.d(Constants.TAG,
                        "Error creating media file, check storage permissions: ");// e.getMessage());
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                imagesData.get(i).getFirstFrame().compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(Constants.TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(Constants.TAG, "Error accessing file: " + e.getMessage());
            }

        }

    }

    public static void writeLeftSwipedTofile(Context context, boolean isRightSwiped, ArrayList<String> fileIds) {

        Log.d(Constants.TAG, "writeLeftSwipedTofile : ");

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(getOutputMediaFile("LeftSwiped", context, isRightSwiped), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }

        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
        try {
            for (int i = 0; i < fileIds.size(); i++) {
                myOutWriter.append(fileIds.get(i));
                myOutWriter.append("\n\r");
            }
            myOutWriter.close();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static ArrayList<String> readLeftSwipedIds(Context context) {

        Log.d(Constants.TAG, "readLeftSwipedIds : ");

        ArrayList<String> leftSwipedIds = new ArrayList<String>();
        File file = getOutputMediaFile("LeftSwiped", context, false);
        if (file.exists())
            try {
                Scanner scanner = new Scanner(file);
                scanner.useDelimiter("\n\r");

                while (scanner.hasNext()) {
                    String line = scanner.next();
                    Log.d(Constants.TAG, "readLeftSwipedIds id : " + line);
                    leftSwipedIds.add(line);

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        return leftSwipedIds;

    }

    public static File[] getFiles() {

        Log.d(Constants.TAG, "readIdsFromFile : ");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File[] files = mediaStorageDir.listFiles();

        Log.e(Constants.TAG, " " + files);


        return files;
    }

    public static List<String> getIdsFromFiles(boolean IsRightFiles) {

        Log.d(Constants.TAG, "getIdsFromFiles : ");

        // List<Bitmap> bitmaps = new ArrayList<Bitmap>();

        if (getFiles() != null && getFiles().length > 0) {
            for (int i = 0; i < getFiles().length; i++) {
                if (IsRightFiles) {
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inSampleSize = 8;
//                    final Bitmap bitmap = BitmapFactory.decodeFile(getFiles()[i].getName(), options);
//                    fileIds.add(getFiles()[i].getName().replace(".jpg", ""));
//                    bitmaps.add(bitmap);
                } else {
                    fileIds.add(getFiles()[i].getName().replace(".rmv", ""));
                }


            }
        }
        Log.d(Constants.TAG, "fileIds size : " + fileIds.size());

        return fileIds;
    }


    public static List<String> getLeftSwiped() {

        Log.d(Constants.TAG, "getLefSwiped : ");


        return getIdsFromFiles(false);

    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(String imageName, Context context, boolean isRightSwiped) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.


        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist

        Log.d(Constants.TAG, "getOutputMediaFile : ");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName = imageName + "rmv";

        if (isRightSwiped)
            mImageName = imageName + ".jpg";


        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

}