package com.example.photogallery;


import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public class FlickrFetchr {

    Logger mLogger=Logger.getLogger(getClass().getName());

    private static final String API_KEY="1cfa2ec314b06495f0eeb3416212f275";

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

    public void fetchItems(){

        String url= Uri.parse("https://www.flickr.com/services/rest/").buildUpon().
                        appendQueryParameter("method","flickr.photos.getRecent").
                        appendQueryParameter("format","json").
                        appendQueryParameter("api_key","1cfa2ec314b06495f0eeb3416212f275").
                        appendQueryParameter("nojsoncallback","1").
                        appendQueryParameter("extras","url_s").
                        build().toString();

        try {
            String jsonString=getUrlString(url);
            JSONObject jsonObject=new JSONObject(jsonString);
            mLogger.info("web"+jsonString);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseItems(List<GalleryItem> items,JSONObject jsonBody) throws JSONException {

        JSONObject photosJsonObject=jsonBody.getJSONObject("photos");
        JSONArray photosJsonArray=photosJsonObject.getJSONArray("ohoto");

        for(int i=0;i<photosJsonArray.length();i++){





        }





    }





}
