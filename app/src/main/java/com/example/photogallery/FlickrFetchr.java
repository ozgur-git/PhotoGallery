package com.example.photogallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FlickrFetchr {

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





}
