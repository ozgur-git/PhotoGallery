package com.example.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebViewFragment;
import androidx.fragment.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity{

    PhotoPageFragment fragment;

    public static Intent newIntent(Context context, Uri photoPageUri){
        Intent intent=new Intent(context,PhotoPageActivity.class);
        intent.setData(photoPageUri);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        fragment=PhotoPageFragment.newInstance(getIntent().getData());
        return fragment;
    }

    @Override
    public void onBackPressed() {

//        super.onBackPressed();

        if (fragment.canGoBack()){
            fragment.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
