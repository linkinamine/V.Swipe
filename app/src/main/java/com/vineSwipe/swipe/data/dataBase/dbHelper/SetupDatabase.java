package com.vineSwipe.swipe.data.dataBase.dbHelper;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.vineSwipe.swipe.data.Constants;
import com.vineSwipe.swipe.data.dataBase.dbModel.Gif;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Mohamed El Amine on 10/17/2016.
 */
public class SetupDatabase {

    public static void setupDatabase(Context context) throws java.sql.SQLException {
        Log.d(Constants.TAG, "setupDatabase ");
        GifOpenDatabaseHelper gifOpenDatabaseHelper = OpenHelperManager.getHelper(context,
                GifOpenDatabaseHelper.class);

        Dao<Gif, String> gifDao = gifOpenDatabaseHelper.getDao();

        Date currDateTime = new Date(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currDateTime);
        calendar.add(Calendar.DATE, 14);

        Date dueDate = calendar.getTime();

        gifDao.create(new Gif("QSDFG2345", "Todo Example 1", "Todo Example 1 Description", currDateTime));
        gifDao.create(new Gif("DFGH4567", "Todo Example 2", "Todo Example 2 Description", currDateTime));
        gifDao.create(new Gif("FGGH56789", "Todo Example 3", "Todo Example 3 Description", currDateTime));

        List<Gif> gifs = gifDao.queryForAll();
        for (Gif gif : gifs) {
            Log.e(Constants.TAG, "gifs ids " + gif.getId());


        }

    }

}
