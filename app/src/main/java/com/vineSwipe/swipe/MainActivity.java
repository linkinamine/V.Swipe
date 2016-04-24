package com.vineSwipe.swipe;

import android.content.Context;
import android.os.Bundle;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.vineSwipe.swipe.data.Constants;
import com.vineSwipe.swipe.data.ImageData;
import com.vineSwipe.swipe.helpers.StubHelper;
import com.vineSwipe.swipe.net.NetworkManager;
import com.vineSwipe.swipe.net.giphy.TrendingRequest;
import com.vineSwipe.swipe.net.giphy.model.GiphyImage;
import com.vineSwipe.swipe.net.giphy.model.GiphyResponse;
import com.vineSwipe.swipe.views.tindercard.FlingCardListener;
import com.vineSwipe.swipe.views.tindercard.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FlingCardListener.ActionDownInterface {

    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;
    private static int headCardCounter = 0;
    private static boolean triggerRecents = false;

    private ArrayList<ImageData> imageDataList;
    private SwipeFlingAdapterView flingContainer;
    private List<GiphyImage> giphyImages;

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
        loadRecent();

    }

    private void setupElements() {
        Log.d(Constants.TAG, " setupElements");
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        imageDataList = new ArrayList<>();
        giphyImages = new ArrayList<>();
        myAppAdapter = new MyAppAdapter(imageDataList, MainActivity.this);
        flingContainer.setAdapter(myAppAdapter);
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
                imageDataList.remove(0);
                myAppAdapter.notifyDataSetChanged();
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Log.d(Constants.TAG, " onRightCardExit");
                imageDataList.remove(0);
                myAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                Log.d(Constants.TAG, " onAdapterAboutToEmpty " + itemsInAdapter);

                if (headCardCounter == Constants.NUMBEROFCARDS) {
                    Log.d(Constants.TAG, " headCardCounter == Constants.NUMBEROFCARDS " + headCardCounter + " = " + Constants.NUMBEROFCARDS);
                    resetAllCounters();
                    //TODO ADD RELOADING ANIMATION
                    loadRecent();
                }
                if (itemsInAdapter == 2) {
                    headCardCounter += Constants.NUMBEROFCARDSOFFSET;
                    Log.d(Constants.TAG, "  getting next " + headCardCounter + " items");
                    fillCardView(giphyImages);
                }


            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                Log.d(Constants.TAG, "onScroll");
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

    private void resetAllCounters() {
        Log.d(Constants.TAG, "resetAllCounters");

        headCardCounter = 0;
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
        Log.d(Constants.TAG, " fillCardView ");
        if (headCardCounter + Constants.NUMBEROFCARDSOFFSET < Constants.NUMBEROFCARDS) {
            images = images.subList(headCardCounter, headCardCounter + Constants.NUMBEROFCARDSOFFSET);
            for (int i = 0; i < images.size(); i++) {
                Log.e(Constants.TAG, "url :" + images.get(i).getUrl());
                Log.e(Constants.TAG, "downsampled url :" + images.get(i).getDownSampledUrl());

                imageDataList.add(new ImageData(images.get(i).getUrl(), images.get(i).getDownSampledUrl(), ""));
            }
        } else {
            Log.d(Constants.TAG, " fillCardView Call LoadRecent ");

        }

        myAppAdapter.notifyDataSetChanged();

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

            Glide.with(context.getApplicationContext()).load(parkingList.get(position).getImagePathThumbnail()).asGif().thumbnail(Glide.with(context.getApplicationContext()).load(R.raw.loading).asGif()).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .error(R.drawable.error).dontAnimate().listener(new RequestListener<String, GifDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                    if (e != null)
                        Log.e(Constants.TAG, "onException RequestListener : " + e.getMessage());
                    return false;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    Log.d(Constants.TAG, "onResourceReady RequestListener");

                    //Glide.with(context.getApplicationContext()).load(parkingList.get(position).getImagePathFull()).asGif();
                    return false;
                }

            }).into(viewHolder.cardImage);
            //
            return rowView;
        }

    }

}
