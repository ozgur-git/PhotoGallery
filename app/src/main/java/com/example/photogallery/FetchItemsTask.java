package com.example.photogallery;

import android.os.AsyncTask;

import java.util.List;
import java.util.logging.Logger;

public class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {

    Logger mLogger=Logger.getLogger(getClass().getName());

    List<GalleryItem> mGalleryItemList;
    Observer observer;

    public FetchItemsTask() {
        mLogger.info("fetchitemstask is called");
    }

    public void setObserver(Observer observer) {
        this.observer = observer;
    }


    public List<GalleryItem> getGalleryItemList() {
        return mGalleryItemList;
    }

    @Override
    protected List<GalleryItem> doInBackground(Void...params) {

        //            mLogger.info("web"+(new FlickrFetchr()).getUrlString("https://www.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=1cfa2ec314b06495f0eeb3416212f275&format=json&nojsoncallback=1"));
        return new FlickrFetchr().fetchItems();//todo dagger inject
    }

    @Override
    protected void onPostExecute(List<GalleryItem> items) {
        super.onPostExecute(items);
        mGalleryItemList=items;
//        observer.update();
    }
}
