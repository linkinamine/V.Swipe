package com.vineSwipe.swipe.helpers;

import android.content.Context;
import android.util.Log;

import com.vineSwipe.swipe.data.Constants;
import com.vineSwipe.swipe.data.ImageData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mbenallouch on 10/05/2016.
 */

public class IoOperations {


    public static boolean writeRecordsToFile(ImageData imageData, Context context) {
        FileOutputStream fos;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(imageData.getId(), Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(imageData);
            oos.close();
            Log.d(Constants.TAG, "Cant save records");

            return true;
        } catch (Exception e) {
            Log.e(Constants.TAG, "Cant save records" + e.getMessage());
            return false;
        } finally {
            if (oos != null)
                try {
                    oos.close();
                } catch (Exception e) {
                    Log.e(Constants.TAG, "Error while closing stream " + e.getMessage());
                }
        }
    }

    public static List<ImageData> readRecordsFromFile(Context context) {
        FileInputStream fin;
        ObjectInputStream ois;
        File[] files = context.getFilesDir().listFiles();
        ArrayList<ImageData> records = new ArrayList<>();
        Log.d(Constants.TAG, "dir AbsolutePath " + context.getFilesDir().getAbsolutePath());


        if (files != null) {
            Log.v(Constants.TAG, "files length " + files.length);

            // for (int i = 0; i < files.length; i++) {
            try {

                fin = context.openFileInput(files[0].getName());
                Log.d(Constants.TAG, "fin " + fin.toString());

                ois = new ObjectInputStream(fin);
                Log.d(Constants.TAG, "ois " + ois.toString());

                records.add((ImageData) ois.readObject());
                Log.d(Constants.TAG, "records " + records.get(0).getId());

                ois.close();
                Log.v(Constants.TAG, "Records read successfully");

            } catch (FileNotFoundException fne) {
                fne.printStackTrace();
            } catch (StreamCorruptedException sce) {
                sce.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (ClassNotFoundException cne) {
                cne.printStackTrace();
            }
//                finally {
//                    if (ois != null)
//                        try {
//                            ois.close();
//                        } catch (Exception e) {
//                            Log.e(Constants.TAG, "Error in closing stream while reading records" + e.getMessage());
//                        }
//                }
        }
        //  }
        Log.d(Constants.TAG, "records size " + records.size());
        return records;
    }
}