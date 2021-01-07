package com.example.photogallery;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.logging.Logger;

public class FetchItemsTask extends AsyncTask {

    Logger mLogger=Logger.getLogger(getClass().getName());

    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            mLogger.info((new FlickrFetchr()).getUrlString("http://google.com"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
