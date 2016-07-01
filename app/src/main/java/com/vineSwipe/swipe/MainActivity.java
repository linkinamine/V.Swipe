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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements FlingCardListener.ActionDownInterface {

    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;

    private ArrayList<ImageData> imageDataList;
    private SwipeFlingAdapterView flingContainer;
    private List<GiphyImage> giphyImages, fileterdCards;
    private Map<String, GiphyImage> imagesToSave;
    private GlideBuilder glideBuilder;
    private GiphyImage giphyImage;
    private File sdCard, dir;
    List<String> localImageIds;
    private int cardPosition;

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
        Log.e("Glide.getPhotoCacheDir(context) : ", "" + Glide.getPhotoCacheDir(getApplicationContext()).listFiles());

        setupElements();
        setupListeners();
        loadRecent();
        try {
            LoadLocalGifs();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setupElements() {
        Log.d(Constants.TAG, " setupElements");
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        imageDataList = new ArrayList<ImageData>();
        giphyImages = new ArrayList<GiphyImage>();
        fileterdCards = new ArrayList<GiphyImage>();
        myAppAdapter = new MyAppAdapter(imageDataList, MainActivity.this);
        flingContainer.setAdapter(myAppAdapter);
        imagesToSave = new HashMap<String, GiphyImage>();
        glideBuilder = new GlideBuilder(getApplicationContext());
        glideBuilder.setDiskCache(
                new InternalCacheDiskCacheFactory(getApplicationContext(), "gifSwipe", DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE));
        sdCard = Environment.getExternalStorageDirectory();
        dir = new File(sdCard.getAbsolutePath() + "/gifs");
        localImageIds = new ArrayList<String>();
        cardPosition = 0;

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
                IoOperations.leftSwipedFilesIds(imageDataList.get(0).getId());
                imageDataList.remove(0);
                myAppAdapter.notifyDataSetChanged();

                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

            }

            @Override
            public void onRightCardExit(Object dataObject) throws IOException {
                Log.d(Constants.TAG, " onRightCardExit");
                Log.e(Constants.TAG, imageDataList.get(0).getImagePathThumbnail());
                if (imageDataList.get(0).getId() != null)
                    Log.d(Constants.TAG, "image id before saving" + imageDataList.get(0).getId().toString());
                //   if (imageDataList.get(0).getFirstFrameBytes() != null)
                //     Log.d(Constants.TAG, "image resource id before saving" + imageDataList.get(0).getFirstFrameBytes().toString());

                writeGifs(imageDataList.get(0));

                imageDataList.remove(0);
                myAppAdapter.notifyDataSetChanged();

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                Log.d(Constants.TAG, " onAdapterAboutToEmpty " + itemsInAdapter);

                if (itemsInAdapter == 1) {
                    resetAllCounters();
                    Glide.with(getApplicationContext()).load(R.raw.loading_small).asGif().error(R.drawable.error);
                    loadRecent();
                }
                              /*if (headCardCounter < Constants.NUMBEROFCARDS) {
                    if (itemsInAdapter == Constants.NUMBEROFCARDTHRESHOLD) {
                        headCardCounter = (headCardCounter + Constants.NUMBEROFCARDSOFFSET) - itemsInAdapter;
                        Log.d(Constants.TAG, "  getting next " + headCardCounter + " items");
                        fillCardView(giphyImages);
                    }
                } else {
                    Log.d(Constants.TAG, " headCardCounter == Constants.NUMBEROFCARDS " + headCardCounter + " = " + Constants.NUMBEROFCARDS);
                    resetAllCounters();
                    Glide.with(getApplicationContext()).load(R.raw.loading_small).asGif().error(R.drawable.error);
                    loadRecent();
                }*/


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

    //**************************Disk operations********************************************

    /**
     * @param giphyImageData
     */

    private void writeGifs(ImageData giphyImageData) {

        IoOperations.writeRecordsToFile(giphyImageData, getApplicationContext());
    }

    /**
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void LoadLocalGifs() throws ClassNotFoundException, IOException {

        IoOperations.readRecordsFromFile(getApplicationContext());
    }
    //**************************End of Disk operations********************************************

    private void resetAllCounters() {
        Log.d(Constants.TAG, "resetAllCounters");

        imageDataList.clear();
        giphyImages.clear();

    }

    /**
     * Giphy requests
     */

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

        fileterdCards = filterAlreadySwiped(images);
        for (int i = 0; i < fileterdCards.size(); i++) {
            imageDataList.add(new ImageData(fileterdCards.get(i).getId(), fileterdCards.get(i).getUrl(), fileterdCards.get(i).getDownSampledUrl(), "", null));
        }

        myAppAdapter.notifyDataSetChanged();

    }

    private List<GiphyImage> filterAlreadySwiped(List<GiphyImage> images) {
        Log.d(Constants.TAG, " filterAlreadySwiped  ");

        Log.d(Constants.TAG, " images size before  " + images.size());

        localImageIds = IoOperations.getFileIds();
        for (int i = 0; i < localImageIds.size(); i++) {
            for (int j = 0; j < images.size(); j++) {
                Log.d(Constants.TAG, "local images:   " + localImageIds.get(i));
                Log.d(Constants.TAG, " images:   " + images.get(i).getId());

                if (localImageIds.get(i) == images.get(j).getId()) {
                    Log.d(Constants.TAG, "removing cards");
                    images.remove(j);
                }
            }
        }
        Log.d(Constants.TAG, " images size after  " + images.size());

        return images;


    }


    @Override
    public void onActionDownPerform() {
        Log.e("action", "bingo");
    }


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
                    return false;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    Log.d(Constants.TAG, "onResourceReady RequestListener " + resource.toString());
                    //Glide.with(context.getApplicationContext()).load(parkingList.get(position).getImagePathFull()).asGif();
                    //  imageDataList.get(position).setFirstFrameBytes(null);
                    cardPosition = position;
                    imageDataList.get(0).setFirstFrame(resource.getFirstFrame());

                    return false;
                }

            }).into(viewHolder.cardImage);
            //
            return rowView;
        }

    }

}
