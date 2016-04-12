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
import com.vineSwipe.swipe.data.ImageData;
import com.vineSwipe.swipe.helpers.StubHelper;
import com.vineSwipe.swipe.net.NetworkManager;
import com.vineSwipe.swipe.net.giphy.TrendingRequest;
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
    private String TAG = "swipeX";

    public static void removeBackground() {


        viewHolder.background.setVisibility(View.GONE);
        myAppAdapter.notifyDataSetChanged();

    }

//    private static String createQuery(String type, String keyword, String limit) {
//
//        String moreData = "";
//
//        //type= search , random ,trending
//
//        // http://api.giphy.com/v1/gifs/search?q=funny+cat&api_key=dc6zaTOxFJmzC
//        // http://api.giphy.com/v1/gifs/trending?api_key=dc6zaTOxFJmzC&limit=5
//
//        switch (type) {
//
//            case "search":
//                type = type + "?q=" + keyword;
//                break;
//            case "random":
//                break;
//            case "trending":
//                moreData = "limit=" + limit;
//                break;
//
//        }
//
//
//
//        return query;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        al = new ArrayList<>();


        getData();


        myAppAdapter = new MyAppAdapter(al, MainActivity.this);
        flingContainer.setAdapter(myAppAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                al.remove(0);
                myAppAdapter.notifyDataSetChanged();
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

            }

            @Override
            public void onRightCardExit(Object dataObject) {

                al.remove(0);
                myAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

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

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);

                myAppAdapter.notifyDataSetChanged();
            }
        });

    }


    private void loadRecent() {
        Request trendingRequest = new TrendingRequest(new Response.Listener<GiphyResponse>() {
            @Override
            public void onResponse(GiphyResponse response) {
                // mAdapter.showResults("Trending", response.getImages());

                Log.e(TAG, "response imqges! " + response.getImages());

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


    private void getData() {

        loadRecent();

        al.add(new ImageData("https://media.giphy.com/media/3o7abqPKlcPasOJGBG/giphy.gif", "Inspiration Feed"));
        al.add(new ImageData("https://media.giphy.com/media/uPD6M9fj1elG/giphy.gif", "Deadpool"));
        al.add(new ImageData("https://media.giphy.com/media/3o7abIn8H8TTzmQrcc/giphy.gif", "Cameron Diaz"));
        al.add(new ImageData("https://media.giphy.com/media/xT9DPDoWMicL4nU3NC/giphy.gif", "Julianne Moore"));


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

            View rowView = convertView;


            if (rowView == null) {

                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.item, parent, false);
                // configure view holder
                viewHolder = new ViewHolder();
                viewHolder.DataText = (TextView) rowView.findViewById(R.id.bookText);
                viewHolder.background = (FrameLayout) rowView.findViewById(R.id.background);
                viewHolder.cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.DataText.setText(parkingList.get(position).getDescription() + "");


            Glide.with(MainActivity.this).load(parkingList.get(position).getImagePath()).asGif().error(R.drawable.nope).into(viewHolder.cardImage);

            Log.i("Image Path", parkingList.get(position).getImagePath());
            return rowView;
        }
    }
}
