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
    private RequestHandler mRequestHandler;
    private ConcurrentHashMap<T,String> mRequestMap=new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> thumbnailDownloadListener) {
        mThumbnailDownloadListener = thumbnailDownloadListener;
    }

    public ThumbnailDownloader(Handler responseHandler){
       super(TAG);
       mResponseHandler=responseHandler;
   }

    @Override
    protected void onLooperPrepared() {

        mRequestHandler=RequestHandler.getRequestHandler(this);

        /*
        mRequestHandler=new Handler(){//leaks?

            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what==MESSAGE_DOWNLOAD){
                    T target=(T) msg.obj;
                    mLogger.info("Got a request or URL: "+mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };

         */
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

        mResponseHandler.post(()->{
            if (mRequestMap.get(target)!=url||mHasQuit){
                return;
            }
            mRequestMap.remove(target);
            mLogger.info("mRequestMap size is 0 "+mRequestMap.size());
            mThumbnailDownloadListener.onThumbnailDownloaded(target,bitmap);
        });

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

     public void clearQueue(){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
     }

    @Override
    public boolean quit() {
        mHasQuit=true;
        return super.quit();
    }

    public static class RequestHandler<T> extends Handler {

        private static RequestHandler requestHandler;

        static ThumbnailDownloader mThumbnailDownloader;


        private RequestHandler(){


        }

        public static RequestHandler getRequestHandler(ThumbnailDownloader thumbnailDownloader) {
            mThumbnailDownloader=thumbnailDownloader;
            if(requestHandler==null){
                requestHandler=new RequestHandler();
            }
            return requestHandler;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what==MESSAGE_DOWNLOAD){
                T target=(T) msg.obj;
//                mLogger.info("Got a request or URL: "+mRequestMap.get(target));
                mThumbnailDownloader.handleRequest(target);
            }
        }
    }

}
