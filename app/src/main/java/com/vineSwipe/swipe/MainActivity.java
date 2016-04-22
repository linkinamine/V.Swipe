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
import com.vineSwipe.swipe.tindercard.FlingCardListener;
import com.vineSwipe.swipe.tindercard.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FlingCardListener.ActionDownInterface {

    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;
    private ArrayList<ImageData> al;
    private SwipeFlingAdapterView flingContainer;
    private String TAG = "com.vineSwipe.swipe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, " onCreate" + getClass().getCanonicalName());
        setupElements();
        setupListeners();
        loadRecent();
    }


    public static void removeBackground() {
        viewHolder.background.setVisibility(View.GONE);
        myAppAdapter.notifyDataSetChanged();
    }

    private void setupElements() {
        Log.d(TAG, " setupElements");
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        al = new ArrayList<>();
        myAppAdapter = new MyAppAdapter(al, MainActivity.this);
        al.add(new ImageData("http://media2.giphy.com/media/A4xYBEm9vD7wc/giphy.gif", "http://media2.giphy.com/media/A4xYBEm9vD7wc/200w_d.gif", ""));
        flingContainer.setAdapter(myAppAdapter);
    }

    private void setupListeners() {
        Log.d(TAG, " setupListeners");
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Log.d(TAG, " onLeftCardExit");
                al.remove(0);
                myAppAdapter.notifyDataSetChanged();
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Log.d(TAG, " onRightCardExit");
                al.remove(0);
                myAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                Log.d(TAG, " onAdapterAboutToEmpty");

                //TODO:  Refresh : get new cards
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                Log.d(TAG, "onScroll");
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
                Log.d(TAG, "onItemClicked");
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                myAppAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadRecent() {
        Log.d(TAG, " loadRecent ");
        Request trendingRequest = new TrendingRequest(String.valueOf(Constants.NUMBEROFCARDS).toString(), new Response.Listener<GiphyResponse>() {
            @Override
            public void onResponse(GiphyResponse response) {
                // mAdapter.showResults("Trending", response.getImages());
                Log.d(TAG, " onResponse ");
                fillCardView(response.getImages());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Unhandled error! " + error);
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
        Log.d(TAG, " fillCardView ");
        for (int i = 0; i < Constants.NUMBEROFCARDS; i++) {
            Log.e(TAG, "url :" + images.get(i).getUrl());
            Log.e(TAG, "downsampled url :" + images.get(i).getDownSampledUrl());

            al.add(new ImageData(images.get(i).getUrl(), images.get(i).getDownSampledUrl(), ""));
        }
        myAppAdapter.notifyDataSetChanged();

    }


    @Override
    public void onActionDownPerform() {
        Log.e("action", "bingo");
    }

    public static class ViewHolder {
        public static FrameLayout background;
        public TextView DataText;
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

            Log.d(TAG, "getView MyAppAdapter");
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.item, parent, false);
                // configure view holder
                viewHolder = new ViewHolder();
                viewHolder.DataText = (TextView) rowView.findViewById(R.id.bookText);
                viewHolder.background = (FrameLayout) rowView.findViewById(R.id.background);
                viewHolder.cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
                viewHolder.cardImage.setImageDrawable(getDrawable(R.drawable.loading));
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.DataText.setText(parkingList.get(position).getDescription() + "");
            Glide.with(context).load(parkingList.get(position).getImagePathThumbnail()).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .error(R.drawable.error).listener(new RequestListener<String, GifDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                    Log.e(TAG, "onException RequestListener");
                    return false;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    Log.d(TAG, "onResourceReady RequestListener");
                    Glide.with(context).load(parkingList.get(position).getImagePathFull()).asGif();
                    return false;
                }

            }).into(viewHolder.cardImage);
            //
            return rowView;
        }

    }
}
