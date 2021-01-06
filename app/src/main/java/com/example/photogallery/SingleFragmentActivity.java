package com.example.photogallery;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.logging.Logger;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    Logger mLogger=Logger.getLogger(getClass().getName());

    @LayoutRes
    private int getLayoutResId(){

        return R.layout.activity_fragment;
    }

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());

        if  (savedInstanceState==null) {

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, createFragment()).commit();
        }

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels ;
        float dpWidth  = outMetrics.widthPixels / density;

        mLogger.info("density is "+density+" height is "+dpHeight+" width is "+dpWidth);

    }
}
