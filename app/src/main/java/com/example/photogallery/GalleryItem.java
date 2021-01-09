package com.example.photogallery;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class GalleryItem {

    public String mCaption;
    private String mId;
    private String mUrl;

//    @Bindable
    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
//        notifyPropertyChanged(BR.caption);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
