package com.vineSwipe.swipe;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;
import com.vineSwipe.swipe.data.Constants;
import com.vineSwipe.swipe.data.ImageData;
import com.vineSwipe.swipe.data.dataBase.dbHelper.SetupDatabase;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements FlingCardListener.ActionDownInterface {


    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;

    private ArrayList<ImageData> imageDataList, rightImages, leftImages;
    private SwipeFlingAdapterView flingContainer;
    private List<GiphyImage> giphyImages, fileterdCards;
    private GlideBuilder glideBuilder;
    private File sdCard;

    private AccountHeader headerResult = null;
    private Drawer result = null;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;


    public static void removeBackground() {
        ViewHolder.background.setVisibility(View.GONE);
        myAppAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(Constants.TAG, " onCreate " + getClass().getCanonicalName());

        //setupDatabase();
        setupDrawer(savedInstanceState);
        setupElements();
        setupListeners();

        Log.e("Glide.getPhotoCacheDir ", "" + Glide.getPhotoCacheDir(getApplicationContext()).getAbsolutePath());

        //   LoadLocalGifs();
        loadRecent();


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ListHelper.isListSetup(rightImages)) {
            writeRightGifs(rightImages, true);
        }

        if (ListHelper.isListSetup(leftImages)) {
            writeLeftGifs(false, leftImages);
        }
        // resetAllData();


    }

    //**************************DB Setup********************************************
    public void setupDatabase() {
        try {
            SetupDatabase.setupDatabase(MainActivity.this);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //**************************END  OF  DB Setup********************************************

    //**************************Layout and listeners Setup********************************************

    private void setupDrawer(Bundle savedInstanceState) {

        Log.d(Constants.TAG, " setupDrawer");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.drawer_item_crossfade_drawer_layout_drawer);

        setupProfiles(savedInstanceState);

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withDrawerLayout(R.layout.crossfade_drawer)
                .withDrawerWidthDp(72)
                .withGenerateMiniDrawer(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("drawer_item_compact_header").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName("drawer_item_action_bar_drawer").withIcon(GoogleMaterial.Icon.gmd_home).withBadge("22").withBadgeStyle(new BadgeStyle(Color.RED, Color.RED)).withIdentifier(2).withSelectable(false),
                        new PrimaryDrawerItem().withName("drawer_item_multi_drawer").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(3),
                        new PrimaryDrawerItem().withName("R.string.drawer_item_non_translucent_status_drawer").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(4),
                        new PrimaryDrawerItem().withDescription("A more complex sample").withName("R.string.drawer_item_advanced_drawer").withIcon(GoogleMaterial.Icon.gmd_adb).withIdentifier(5),
                        new SectionDrawerItem().withName("R.string.drawer_item_section_header"),
                        new SecondaryDrawerItem().withName("R.string.drawer_item_open_source").withIcon(GoogleMaterial.Icon.gmd_home),
                        new SecondaryDrawerItem().withName("R.string.drawer_item_contact").withIcon(GoogleMaterial.Icon.gmd_format_color_fill).withTag("Bullhorn")
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            makeText(MainActivity.this, ((Nameable) drawerItem).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();
                        }
                        //we do not consume the event and want the Drawer to continue with the event chain
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();


        //get the CrossfadeDrawerLayout which will be used as alternative DrawerLayout for the Drawer
        //the CrossfadeDrawerLayout library can be found here: https://github.com/mikepenz/CrossfadeDrawerLayout
        crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();

        //define maxDrawerWidth
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));
        //add second view (which is the miniDrawer)
        final MiniDrawer miniResult = result.getMiniDrawer();
        //build the view for the MiniDrawer
        View view = miniResult.build(this);
        //set the background of the MiniDrawer as this would be transparent
        view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));
        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(400);

                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    result.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });


    }

    private void setupProfiles(Bundle savedInstanceState) {


    }

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

                rightImages.add(imageDataList.get(0));

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
        fileterdCards = images;
        // ListHelper.filterAlreadySwiped(images, imageIds);

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
                viewHolder.background = (FrameLayout) rowView.findViewById(R.id.background);
                viewHolder.cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

//            viewHolder.dataText.setText(parkingList.get(position).getDescription() + "");

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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
