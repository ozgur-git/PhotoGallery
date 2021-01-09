package com.example.photogallery;

import dagger.Module;
import dagger.Provides;

@Module
public class PhotoGalleryModule {

    @Provides
    FlickrFetchr provideFlickrFetchr() {
        return new FlickrFetchr();
    }

    @Provides
    GalleryItem provideGalleryItem() {
        return new GalleryItem();
    }
}


