package com.vineSwipe.swipe.tests;

import com.vineSwipe.swipe.helpers.ListHelper;
import com.vineSwipe.swipe.net.giphy.model.GiphyImage;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mac on 7/7/16.
 */
public class ListHelperTest extends TestCase {

    private int imagesSizeBefore = 0;
    private List<GiphyImage> images;
    private List<String> localImageIds;
    private List<GiphyImage> remainingImages;

    protected void setUp() throws Exception {
        super.setUp();
        images = new ArrayList<GiphyImage>(10);
        localImageIds = new ArrayList<String>(2);

        remainingImages = new ArrayList<GiphyImage>();

        for (int i = 0; i < 10; i++) {
            images.add(new GiphyImage("" + i));
            if (i == 4 || i == 9) {
                localImageIds.add("" + i);
            }

        }
        imagesSizeBefore = images.size();


    }

    public void testFilterAlreadySwiped() throws Exception {

        remainingImages = ListHelper.filterAlreadySwiped(images, localImageIds);
        System.out.println("remaining images :" + remainingImages.size());
        System.out.println("localImageIds :" + localImageIds.size());
        System.out.println(" images :" + images.size());


        assertTrue(remainingImages.size() == (imagesSizeBefore - localImageIds.size()));


    }

    public void testIsListSetup() throws Exception {

        ListHelper.isListSetup(images);
    }
}

