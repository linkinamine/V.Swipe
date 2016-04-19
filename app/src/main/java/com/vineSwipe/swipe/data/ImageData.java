package com.vineSwipe.swipe.data;

/**
 * Created by nirav on 05/10/15.
 */
public class ImageData {


    private String description;

    private String imagePath;

    public ImageData(String imagePath, String description) {
        this.imagePath = imagePath;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


}
