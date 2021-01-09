package com.example.photogallery;

import dagger.Component;

@Component(modules = PhotoGalleryModule.class)
public interface PhotoGalleryComponent {

    void inject(FlickrFetchr flickrFetchr);
    void inject(PhotoGalleryFragment.FetchItemsTask fetchItemsTask);
}
