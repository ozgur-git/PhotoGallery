package com.example.photogallery;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;

import java.util.logging.Logger;

public class PollService extends IntentService {

   Logger mLogger=Logger.getLogger(getClass().getName());

   private static final String TAG="PollService";

   public PollService() {
        super(TAG);
    }

    public static Intent newIntent(Context context){
        return new Intent(context,PollService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mLogger.info("service started!");
    }
}
