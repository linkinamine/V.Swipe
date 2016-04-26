package com.vineSwipe.swipe.net.giphy.model;

/**
 * An object describing a variation of an image on the Giphy service.
 * <p/>
 * The object exists for GSON's convenience. Actual access to these properties is via the
 * GiphyResponse object.
 * <p/>
 * Created by alex on 05/10/15.
 */
class ImageVariation {
    int width;
    int height;
    int size;


    /**
     * URL of GIF version of image
     */
    String url;

    /**
     * URL of WEBP version of image, which is smaller and Fresco totally supports it - yay!
     */
    String webp;
}
