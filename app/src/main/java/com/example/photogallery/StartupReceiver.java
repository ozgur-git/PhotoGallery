package com.example.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.logging.Logger;

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG="StartupReceiver";
    Logger mLogger=Logger.getLogger(getClass().getName());

    @Override
    public void onReceive(Context context, Intent intent) {


        boolean isOn=QueryReferences.isAlarmOn(context);
        mLogger.info("received broadcast intent is "+intent.getAction()+" boolean is "+isOn);
        PollService.setServiceAlarm(context,!isOn);

//        mLogger.info("lay lay lom received broadcast intent is "+intent.getAction()+" boolean is ");
    }
}
