package com.example.photogallery;


import android.net.Uri;
import com.google.gson.Gson;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class FlickrFetchr {

    Logger mLogger=Logger.getLogger(getClass().getName());

    private static final String API_KEY="1cfa2ec314b06495f0eeb3416212f275";
    private static final String FETCH_RECENTS_METHOD="flickr.photos.getRecent";
    private static final String SEARCH_METHOD="flickr.photos.search";
    private static final Uri END_POINT=Uri.parse("https://api.flickr.com/services/rest/").buildUpon().
            appendQueryParameter("format","json").
            appendQueryParameter("api_key",API_KEY).
            appendQueryParameter("nojsoncallback","1").
            appendQueryParameter("extras","url_s").
            build();


    Photo mPhoto;

//    public FlickrFetchr() {
//        PhotoGalleryComponent component=DaggerPhotoGalleryComponent.builder().photoGalleryModule(new PhotoGalleryModule()).build();
//        component.inject(this);
//    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {

        URL url=new URL(urlSpec);
        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
        InputStream inputStream=connection.getInputStream();
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        int data=0;

        while((data=inputStream.read())!=-1){
            outputStream.write(data);
        }

        outputStream.close();
        inputStream.close();;
        connection.disconnect();

        return outputStream.toByteArray();
    }

    public String getUrlString(String urlSpec) throws IOException {

        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchRecentPhotos(){
        String url=buildUrl(FETCH_RECENTS_METHOD,null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query){
        String url=buildUrl(SEARCH_METHOD,query);
        return downloadGalleryItems(url);
    }

    private String buildUrl(String method,String query){
        Uri.Builder uriBuilder=END_POINT.buildUpon().
                appendQueryParameter("method",method);

        if (method.equals(SEARCH_METHOD)){
            uriBuilder.appendQueryParameter("text",query);
        }

        return uriBuilder.build().toString();
    }

    public List<Photo> downloadGalleryItems(String url){

        List<Photo> items=new ArrayList<>();

//        String url= Uri.parse("https://www.flickr.com/services/rest/").buildUpon().
//                        appendQueryParameter("method","flickr.photos.getRecent").
//                        appendQueryParameter("format","json").
//                        appendQueryParameter("api_key",API_KEY).
//                        appendQueryParameter("nojsoncallback","1").
//                        appendQueryParameter("page",String.valueOf(pageNumber)).
//                        appendQueryParameter("extras","url_s").
//                        build().toString();

        mLogger.info("urlquery "+url);
        try {
            String jsonString=getUrlString(url);
            mLogger.info("web "+jsonString);
//            JSONObject jsonObject=new JSONObject(jsonString);
//            parseItems(items,jsonObject);
            parseJSONString(items,jsonString);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }

    private void parseJSONString(List<Photo> items,String jsonString) throws JSONException{
        Gson gson=new Gson();
        TopObject topObject=gson.fromJson(jsonString,TopObject.class);
        Photo[] itemArray=topObject.getPhotos().getPhoto();
        AtomicInteger count= new AtomicInteger();
        Arrays.stream(itemArray).forEach((item)->{
            items.add(item);
            mLogger.info("item name is "+item.getTitle()+" count is "+ count.getAndIncrement());
        });
    }

//    private void parseItems(List<GalleryItem> items,JSONObject jsonBody) throws JSONException {
//
//
//        JSONObject photosJsonObject=jsonBody.getJSONObject("photos");
//        JSONArray photoJsonArray=photosJsonObject.getJSONArray("photo");
//        mLogger.info("json array size is "+photoJsonArray.length());
//
//        for(int i=0;i<photoJsonArray.length();i++) {
//            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
//            PhotoGalleryComponent component=DaggerPhotoGalleryComponent.builder().photoGalleryModule(new PhotoGalleryModule()).build();
//            component.inject(this);//dagger sil
//            mGalleryItem.setId(photoJsonObject.getString("id"));
//            mGalleryItem.setCaption(photoJsonObject.getString("title"));
//
//            if (photoJsonObject.has("url_s")) {
//                mGalleryItem.setUrl(photoJsonObject.getString("url_s"));
//            }
//
//            items.add(mGalleryItem);
//        }
//        mLogger.info("items size is "+items.size());
//    }

}
