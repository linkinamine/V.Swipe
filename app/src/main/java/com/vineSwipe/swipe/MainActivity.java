package com.vineSwipe.swipe;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.vineSwipe.swipe.data.Constants;
import com.vineSwipe.swipe.data.ImageData;
import com.vineSwipe.swipe.helpers.IoOperations;
import com.vineSwipe.swipe.helpers.ListHelper;
import com.vineSwipe.swipe.helpers.StubHelper;
import com.vineSwipe.swipe.net.NetworkManager;
import com.vineSwipe.swipe.net.giphy.TrendingRequest;
import com.vineSwipe.swipe.net.giphy.model.GiphyImage;
import com.vineSwipe.swipe.net.giphy.model.GiphyResponse;
import com.vineSwipe.swipe.views.tindercard.FlingCardListener;
import com.vineSwipe.swipe.views.tindercard.SwipeFlingAdapterView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FlingCardListener.ActionDownInterface {

    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;

    private ArrayList<ImageData> imageDataList, rightImages, leftImages;
    private SwipeFlingAdapterView flingContainer;
    private List<GiphyImage> giphyImages, fileterdCards;
    private GlideBuilder glideBuilder;
    private File sdCard;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    public static void removeBackground() {
        ViewHolder.background.setVisibility(View.GONE);
        myAppAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(Constants.TAG, " onCreate " + getClass().getCanonicalName());

        setupElements();
        setupListeners();

        Log.e("Glide.getPhotoCacheDir ", "" + Glide.getPhotoCacheDir(getApplicationContext()).getAbsolutePath());

        //   LoadLocalGifs();
        loadRecent();


    }

    @Override
    protected void onStop() {
        super.onStop();
        //  if (ListHelper.isListSetup(rightImages)) {
        //  writeGifs(rightImages);

        //}

        if (ListHelper.isListSetup(leftImages)) {


            writeLeftGifs(false, leftImages);
        }
        // resetAllData();


    }

    //**************************Layout and listeners Setup********************************************


    private void setupElements() {
        Log.d(Constants.TAG, " setupElements");
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        imageDataList = new ArrayList<ImageData>();
        giphyImages = new ArrayList<GiphyImage>();
        fileterdCards = new ArrayList<GiphyImage>();
        myAppAdapter = new MyAppAdapter(imageDataList, MainActivity.this);
        flingContainer.setAdapter(myAppAdapter);
        rightImages = new ArrayList<ImageData>();
        leftImages = new ArrayList<ImageData>();
        glideBuilder = new GlideBuilder(getApplicationContext());
        glideBuilder.setDiskCache(
                new InternalCacheDiskCacheFactory(getApplicationContext(), "gifSwipe", DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE));
        sdCard = Environment.getExternalStorageDirectory();


    }


    private void setupListeners() {
        Log.d(Constants.TAG, " setupListeners");
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Log.d(Constants.TAG, " onLeftCardExit");
                Log.e(Constants.TAG, imageDataList.get(0).getImagePathThumbnail());

                if (imageDataList.get(0).getId() != null) {
                    Log.d(Constants.TAG, "  IoOperations.leftSwipedFilesIds " + imageDataList.get(0).getId());

                    leftImages.add(imageDataList.get(0));
                }

                imageDataList.remove(0);
                myAppAdapter.notifyDataSetChanged();


            }

            @Override
            public void onRightCardExit(Object dataObject) throws IOException {
                Log.d(Constants.TAG, " onRightCardExit");
                Log.e(Constants.TAG, imageDataList.get(0).getImagePathThumbnail());

                if (imageDataList.get(0).getId() != null)
                    Log.d(Constants.TAG, "image id before saving" + imageDataList.get(0).getId().toString());

                //rightImages.add(imageDataList.get(0));

                imageDataList.remove(0);
                myAppAdapter.notifyDataSetChanged();

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                Log.d(Constants.TAG, " onAdapterAboutToEmpty " + itemsInAdapter);

//                if (itemsInAdapter == 1) {
//                    resetAllData();
//                    Glide.with(getApplicationContext()).load(R.raw.loading_small).asGif().error(R.drawable.error);
//                    loadRecent();
//                }

            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                //     Log.d(Constants.TAG, "onScroll");
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Log.d(Constants.TAG, "onItemClicked");
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                myAppAdapter.notifyDataSetChanged();
            }
        });
    }


    private void resetAllData() {
        Log.d(Constants.TAG, "resetAllData");

        imageDataList.clear();
        giphyImages.clear();

    }

    //**************************Layout and listeners Setup********************************************


    //**************************Giphy requests operations********************************************


    private void loadRecent() {
        Log.d(Constants.TAG, " loadRecent ");
        Request trendingRequest = new TrendingRequest(String.valueOf(Constants.NUMBEROFCARDS).toString(), new Response.Listener<GiphyResponse>() {
            @Override
            public void onResponse(GiphyResponse response) {
                Log.d(Constants.TAG, " onResponse ");
                giphyImages = response.getImages();
                fillCardView(giphyImages);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Constants.TAG, "Unhandled error! " + error);
                StubHelper.showYouBrokeItDialog(
                        "Couldn't load search results. Are you connected to the internet?",
                        new Runnable() {
                            @Override
                            public void run() {
                                loadRecent();
                            }
                        },
                        MainActivity.this);
            }
        });

        NetworkManager.getInstance(MainActivity.this).addToRequestQueue(trendingRequest);
    }


    private void fillCardView(List<GiphyImage> images) {
        Log.d(Constants.TAG, " fillCardView  ");

        ArrayList<String> imageIds = new ArrayList<String>();
        imageIds = IoOperations.readLeftSwipedIds(MainActivity.this);
        fileterdCards = ListHelper.filterAlreadySwiped(images, imageIds);

        for (int i = 0; i < fileterdCards.size(); i++) {
            imageDataList.add(new ImageData(fileterdCards.get(i).getId(), fileterdCards.get(i).getUrl(), fileterdCards.get(i).getDownSampledUrl(), "", null));
        }

        myAppAdapter.notifyDataSetChanged();

    }

    //**************************End of Giphy requests operations********************************************


    //**************************Disk operations********************************************

    /**
     * @param giphyImagesData
     */
    private void writeRightGifs(List<ImageData> giphyImagesData, boolean isRightSwiped) {

        IoOperations.writeRecordsToFile(giphyImagesData, getApplicationContext(), isRightSwiped);
    }


    private void writeLeftGifs(boolean isRightSwiped, ArrayList<ImageData> imageDatas) {

        ArrayList<String> filesIds = new ArrayList<String>();


        for (ImageData fIds : imageDatas) {
            filesIds.add(fIds.getId());

        }
        IoOperations.writeLeftSwipedTofile(MainActivity.this, isRightSwiped, filesIds);
    }

    /**
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void LoadLocalGifs() {

        // IoOperations.readRecordsFromFile(getApplicationContext());
    }

    //**************************End of Disk operations********************************************
    @Override
    public void onActionDownPerform() {
        Log.e("action", "bingo");
    }

    //**************************Adapter operations********************************************


    public static class ViewHolder {
        public static FrameLayout background;
        public TextView dataText;
        public ImageView cardImage;

    }

    public class MyAppAdapter extends BaseAdapter {

        public List<ImageData> parkingList;
        public Context context;

        private MyAppAdapter(List<ImageData> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
        }

        @Override
        public int getCount() {
            return parkingList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Log.d(Constants.TAG, "getView MyAppAdapter");
            Log.d(Constants.TAG, "Glide list count " + parkingList.size());

            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.item, parent, false);
                // configure view holder
                viewHolder = new ViewHolder();
                viewHolder.dataText = (TextView) rowView.findViewById(R.id.bookText);
                viewHolder.background = (FrameLayout) rowView.findViewById(R.id.background);
                viewHolder.cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.dataText.setText(parkingList.get(position).getDescription() + "");

            Glide.with(context.getApplicationContext()).load(parkingList.get(position).getImagePathThumbnail()).asGif().thumbnail(Glide.with(context.getApplicationContext()).load(R.raw.loading_big).asGif())
                    .error(R.drawable.error).diskCacheStrategy(DiskCacheStrategy.RESULT).listener(new RequestListener<String, GifDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                    if (e != null)
                        Log.e(Constants.TAG, "onException RequestListener : " + e.getMessage());
                    return true;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    Log.d(Constants.TAG, "onResourceReady RequestListener " + resource.toString());
                    //Glide.with(context.getApplicationContext()).load(parkingList.get(position).getImagePathFull()).asGif();
                    //  imageDataList.get(position).setFirstFrameBytes(null);
                    if (ListHelper.isListSetup(imageDataList))
                        imageDataList.get(0).setFirstFrame(resource.getFirstFrame());

                    return false;
                }

            }).into(viewHolder.cardImage);
            //
            return rowView;
        }

        //**************************End of Adapter operations********************************************


    }

}
