package com.example.photogallery;

import android.os.AsyncTask;

import java.util.List;
import java.util.logging.Logger;

public class FetchItemsTask extends AsyncTask {

    Logger mLogger=Logger.getLogger(getClass().getName());

    List<GalleryItem> mGalleryItemList;

    public FetchItemsTask(List<GalleryItem> galleryItemList) {
        mGalleryItemList = galleryItemList;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        //            mLogger.info("web"+(new FlickrFetchr()).getUrlString("https://www.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=1cfa2ec314b06495f0eeb3416212f275&format=json&nojsoncallback=1"));
        mGalleryItemList=new FlickrFetchr().fetchItems();//todo dagger inject
        mLogger.info("asynctask size is "+mLogger);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
