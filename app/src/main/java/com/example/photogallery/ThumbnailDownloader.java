package com.example.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ThumbnailDownloader<T> extends HandlerThread {

    Logger mLogger=Logger.getLogger(getClass().getName());

    private static final String TAG="Thumbnail Downloader";
    private static final int MESSAGE_DOWNLOAD=0;

    private boolean mHasQuit=false;
    private Handler mRequestHandler;
    private ConcurrentHashMap<T,String> mRequestMap=new ConcurrentHashMap<>();


    public ThumbnailDownloader(){
       super(TAG);
   }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler=new Handler(){

            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what==MESSAGE_DOWNLOAD){
                    T target=(T) msg.obj;
                    mLogger.info("Got a request or URL: "+mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    private void handleRequest(final T target){

        final String url=mRequestMap.get(target);

        if (url==null){
            return;
        }

        byte[] bitmapBytes= new byte[0];
        try {
            bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            mLogger.info("Bitmap downloaded");
        } catch (IOException e) {
            mLogger.info("error downloading image"+e);
        }

        final Bitmap bitmap= BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.length);
    }
    public void queueThumbnail(T target, String url){
        mLogger.info("Got a URL: "+url);

        if (url==null){
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target,url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD,target).sendToTarget();
        }
     }

    @Override
    public boolean quit() {
        mHasQuit=true;
        return super.quit();
    }
}
