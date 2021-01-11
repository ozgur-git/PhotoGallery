package com.example.photogallery;

import android.os.HandlerThread;

import java.util.logging.Logger;

public class ThumbnailDownloader<T> extends HandlerThread {

    Logger mLogger=Logger.getLogger(getClass().getName());

    private final static String TAG="Thumbnail Downloader";

    private boolean mHasQuit=false;

    public ThumbnailDownloader(){
       super(TAG);
   }

    public void queueThumbnail(T target,String url){
         mLogger.info("Got a URL: "+url);
     }

    @Override
    public boolean quit() {
        mHasQuit=true;
        return super.quit();
    }
}
