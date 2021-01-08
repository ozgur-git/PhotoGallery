package com.example.photogallery;

import android.os.AsyncTask;

import java.util.logging.Logger;

public class FetchItemsTask extends AsyncTask {

    Logger mLogger=Logger.getLogger(getClass().getName());

    @Override
    protected Object doInBackground(Object[] objects) {

        //            mLogger.info("web"+(new FlickrFetchr()).getUrlString("https://www.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=1cfa2ec314b06495f0eeb3416212f275&format=json&nojsoncallback=1"));
        new FlickrFetchr().fetchItems();//todo dagger inject
        return null;

    }
}
