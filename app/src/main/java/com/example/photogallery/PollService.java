package com.example.photogallery;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PollService extends IntentService {

   Logger mLogger=Logger.getLogger(getClass().getName());

   private static final String TAG="PollService";
   private static final long POLL_INTERVAL_MS= TimeUnit.MINUTES.toMillis(1);
   public static final String ACTION_SHOW_NOTIFICATION="com.example.photogallery.SHOW_NOTIFICATION";
   public static final String PERM_PRIVATE="com.example.android.photogallery.PRIVATE";

   private static final int PAGE_NUMBER=1;

   public PollService() {
        super(TAG);
    }

   public static void setServiceAlarm(Context context,boolean isOn){
//       Log.i("setAlarm","alarm is called");
       Intent intent=PollService.newIntent(context);
      PendingIntent pendingIntent=PendingIntent.getService(context,0,intent,0);

      AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

       if (isOn){
           alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),POLL_INTERVAL_MS,pendingIntent);//TODO wake up dene
           Log.i("alarm","alarm is on");
       } else {
           alarmManager.cancel(pendingIntent);
           pendingIntent.cancel();
           Log.i("alalrm","alarm is cancelled");
       }

       QueryReferences.setAlarmOn(context,isOn);
   }

   public static boolean isServiceAlarmOn(Context context){
       Intent intent=PollService.newIntent(context);
       PendingIntent pendingIntent=PendingIntent.getService(context,0,intent,PendingIntent.FLAG_NO_CREATE);
       return pendingIntent!=null;
   }

    public static Intent newIntent(Context context){
        return new Intent(context,PollService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mLogger.info("broadcast service started!");
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
            Resources resources=getResources();
            Intent i=PhotoGalleryActivity.newIntent(this);
            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,i,0);

            Notification notification= new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat notificationManager=NotificationManagerCompat.from(this);
            notificationManager.notify(0,notification);
            sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION));
        }

        QueryReferences.setPrefLastResultId(this,resultId);

    }

    private boolean isNetworkAvailableConnected() {
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        mLogger.info("network info is "+connectivityManager.getActiveNetwork());

        return connectivityManager.getActiveNetworkInfo()!=null&&connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
