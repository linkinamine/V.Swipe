package com.vineSwipe.swipe.data;

import java.io.Serializable;

/**
 * Created by nirav on 05/10/15.
 */
public class ImageData implements Serializable {


    private String description;
    private String imagePathFull;
    private String imagePathThumbnail;
    private String id;
   // private byte[] firstFrameBytes;


    public ImageData(String id, String imagePathFull, String imagePathThumbnail, String description, byte[] firstFrameBytes) {
        this.id = id;
        this.imagePathFull = imagePathFull;
        this.description = description;
        this.imagePathThumbnail = imagePathThumbnail;
      //  this.firstFrameBytes = firstFrameBytes;
    }

    public ImageData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePathThumbnail() {
        return imagePathThumbnail;
    }

    public void setImagePathThumbnail(String imagePathThumbnail) {
        this.imagePathThumbnail = imagePathThumbnail;
    }

    public String getImagePathFull() {
        return imagePathFull;
    }

    public void setImagePathFull(String imagePathFull) {
        this.imagePathFull = imagePathFull;
    }


//    public byte[] getFirstFrameBytes() {
//        return firstFrameBytes;
//    }
//
//    public void setFirstFrameBytes(byte[] firstFrameBytes) {
//        this.firstFrameBytes = firstFrameBytes;
//    }
}

