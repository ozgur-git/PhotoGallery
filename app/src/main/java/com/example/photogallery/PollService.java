package com.example.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PollService extends IntentService {

   Logger mLogger=Logger.getLogger(getClass().getName());

   private static final String TAG="PollService";
   private static final long POLL_INTERVAL_MS= TimeUnit.MINUTES.toMillis(1);

   private static final int PAGE_NUMBER=1;

   public PollService() {
        super(TAG);
    }

   public static void setServiceAlarm(Context context,boolean isOn){
      Intent intent=PollService.newIntent(context);
      PendingIntent pendingIntent=PendingIntent.getService(context,0,intent,0);

      AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

       if (isOn){
           alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),POLL_INTERVAL_MS,pendingIntent);//TODO wake up dene
       } else {
           alarmManager.cancel(pendingIntent);
           pendingIntent.cancel();
       }

   }

    public static Intent newIntent(Context context){
        return new Intent(context,PollService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mLogger.info("service started!");
        if (!isNetworkAvailableConnected()) {
            return;
        }

        String query=QueryReferences.getStoredQuery(this);
        String lastResultId=QueryReferences.getPrefLastResultId(this);

        List<Photo> items;

        if (query==null){
            items=new FlickrFetchr().fetchRecentPhotos(PAGE_NUMBER);
        } else {
            items=new FlickrFetchr().searchPhotos(query,PAGE_NUMBER);
        }

        mLogger.info(("service items size is "+items.size()));

        if (items.size()==0){
            return;
        }

        String resultId=items.get(0).getId();

        if (resultId.equals(lastResultId)){
            mLogger.info("Got an old result "+resultId);
        } else {
            mLogger.info("Got a new result "+resultId);
        }

        QueryReferences.setPrefLastResultId(this,resultId);

    }

    private boolean isNetworkAvailableConnected() {
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        mLogger.info("network info is "+connectivityManager.getActiveNetwork());

        return connectivityManager.getActiveNetworkInfo()!=null&&connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
