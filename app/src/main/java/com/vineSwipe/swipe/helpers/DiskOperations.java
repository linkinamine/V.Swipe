package com.vineSwipe.swipe.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.vineSwipe.swipe.data.Constants;
import com.vineSwipe.swipe.data.ImageData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mbenallouch on 10/05/2016.
 */
public class DiskOperations {


    public final static String APP_PATH_SD_CARD = "/gifs/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";

    public static boolean saveImageToExternalStorage(Context context, ImageData image) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, image.getId());
            file.createNewFile();

            fOut = new FileOutputStream(file);
            fOut.write(image.getImagePathThumbnail().getBytes(Charset.forName("UTF-8")));
            // 100 means no compression, the lower you go, the stronger the compression
            image.getGifDrawable().compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

            return true;

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            return false;
        }
    }

    public static boolean saveImageToInternalStorage(Context context, ImageData image, String id) {

        try {
            // Use the compress method on the Bitmap object to write image to
            // the OutputStream
            FileOutputStream fos = context.openFileOutput(id + ".png", Context.MODE_PRIVATE);

            // Writing the bitmap to the output stream
            image.getGifDrawable().compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }

    public static int isSdReadable() {
        int mExternalStorageAvailable = 0;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = 1;
            Log.i("isSdReadable isWritable", "External storage card is readable and writable.");
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            Log.i("isSdReadable", "External storage card is readable.");
            mExternalStorageAvailable = 2;
        } else {
            // Something else is wrong. It may be one of many other
            // states, but all we need to know is we can neither read nor write
            mExternalStorageAvailable = 0;
        }

        return mExternalStorageAvailable;
    }

    public static List<Bitmap> getThumbnails() throws FileNotFoundException, IOException, StreamCorruptedException, ClassNotFoundException {

        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
        List<Bitmap> thumbnails = new ArrayList<>();
        File f = null;


        Log.d("Files", "Path: " + fullPath);
        if (fullPath != null) {
            f = new File(fullPath);
        }
        if (f != null) {
            if (f.listFiles() != null) {
                Log.d("Files", "Size: " + f.listFiles().length);
                for (int i = 0; i < f.listFiles().length; i++) {
                    Log.d(Constants.TAG, "FileName:" + f.listFiles()[i].getName());
                    File file = f.listFiles()[i];

                    if (isSdReadable() != 0) {
                        FileInputStream fis = new FileInputStream(file);
                        Log.d(Constants.TAG, "FileInputStream:");

                        ObjectInputStream ois = new ObjectInputStream(fis);
                        Log.d(Constants.TAG, "ObjectInputStream:");

                        ImageData imageData = (ImageData) ois.readObject();
                        Log.d(Constants.TAG, "loaded from file " + imageData.getId());
                        Log.d(Constants.TAG, "loaded from file " + imageData.getImagePathFull());

                        thumbnails.add(imageData.getGifDrawable());

                        //   thumbnail = BitmapFactory.(imageData.getGifDrawable());
                    }


// If no file on external storage, look in internal storage
                  /*  if (thumbnail == null) {
                        try {
                            File filePath = context.getFileStreamPath(filename);
                            FileInputStream fi = new FileInputStream(filePath);
                            thumbnail = BitmapFactory.decodeStream(fi);
                        } catch (Exception ex) {
                            Log.e("getThumbnail() on internal storage", ex.getMessage());
                        }
                    }*/
                }

            }
        }
        return thumbnails;
    }
}
