package com.example.photogallery;

import android.graphics.Bitmap;

public interface ThumbnailDownloadListener<T> {
    public void onThumbnailDownloaded(T target, Bitmap thumbnail);
}
