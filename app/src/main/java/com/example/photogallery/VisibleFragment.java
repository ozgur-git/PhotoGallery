package com.example.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public abstract class VisibleFragment extends Fragment {

    private static final String TAG="Visible Fragment";

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter=new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mOnShowNotification,filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);
    }

    private BroadcastReceiver mOnShowNotification=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Got a broadcast: "+intent.getAction(), Toast.LENGTH_SHORT).show();
        }
    };
}
