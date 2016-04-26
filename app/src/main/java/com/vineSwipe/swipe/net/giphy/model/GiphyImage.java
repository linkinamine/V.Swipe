package com.vineSwipe.swipe.net.giphy.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * A data object representing a single image on the Giphy image-hosting service.
 * GSON creates these objects when deserializing responses from the Giphy API.
 * <p/>
 * The object is Parcelable so that it can be packaged into a bundle and passed
 * to a Fragment. This is in preference to putting it into shared memory and fetching
 * it by key because fragments can effectively outlive the application process when
 * they are restored using persisted bundles.
 * <p/>
 * Created by alex on 05/10/15.
 */
public class GiphyImage implements Parcelable {
    Map<String, ImageVariation> images;
    String bitly_gif_url;
    String id;


    public GiphyImage() {
        // Empty constructor required because otherwise the Parcelable constructor
        // prevents GSON from being able to instantiate GiphyImages via reflection.
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private ImageVariation getPreferredVariation() {
        return images.get("original");
    }

    private ImageVariation getDownSampledVariation() {
        return images.get("fixed_width_downsampled");
    }


    /**
     * @return a URL for the web page the gif is hosted on, suitable for sharing.
     */
    public String getShareUrl() {
        return bitly_gif_url;
    }

    /**
     * @return a URL that can be used to load the image for display in the app.
     */
    public String getUrl() {
        return getPreferredVariation().url;
    }

    /**
     * @return a getDownSampledUrl that can be used to load the image for display in the app.
     */
    public String getDownSampledUrl() {
        return getDownSampledVariation().url;
    }

    public int getWidth() {
        return getPreferredVariation().width;
    }

    public int getHeight() {
        return getPreferredVariation().height;
    }

    /**
     * Get the height that the image should have when it is displayed at a given width, to preserve
     * aspect ratio.
     */
    public int getHeight(int width) {
        ImageVariation variation = getPreferredVariation();
        return Math.round(variation.height * (width / (float) variation.width));
    }

    /// PARCELABLE IMPLEMENTATION

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(bitly_gif_url);
        out.writeString(getUrl());
        out.writeString(getDownSampledUrl());
        out.writeInt(getWidth());
        out.writeInt(getHeight());
        out.writeString(getId());
    }

    private GiphyImage(Parcel in) {
        ImageVariation variation = new ImageVariation();
        bitly_gif_url = in.readString();
        variation.webp = in.readString();
        variation.width = in.readInt();
        variation.height = in.readInt();
        id = in.readString();
        images = new HashMap<>();
        images.put("fixed_width", variation);
    }

    public static final Creator<GiphyImage> CREATOR = new Creator<GiphyImage>() {
        @Override
        public GiphyImage createFromParcel(Parcel source) {
            return new GiphyImage(source);
        }

        @Override
        public GiphyImage[] newArray(int size) {
            return new GiphyImage[size];
        }
    };
}
