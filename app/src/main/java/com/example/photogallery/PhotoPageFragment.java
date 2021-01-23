package com.example.photogallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;

public class PhotoPageFragment extends VisibleFragment{

    private static final String ARG_URI="photo_page_uri";

    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static PhotoPageFragment newInstance(Uri uri){
        Bundle args=new Bundle();
        args.putParcelable(ARG_URI,uri);

        PhotoPageFragment fragment=new PhotoPageFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUri=getArguments().getParcelable(ARG_URI);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_photo_page,container,false);

        mProgressBar=v.findViewById(R.id.progressBar);
        mWebView=v.findViewById(R.id.web_view);

        mProgressBar.setMax(100);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress==100){
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
//                getActivity().getActionBar().setSubtitle(title);
            }


        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return super.shouldOverrideUrlLoading(view, url);

                if (!(url.startsWith("http")||url.startsWith("https"))){
                    Intent intent=(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
                    return true;
                }
                return false;
            }
        });

        mWebView.loadUrl(mUri.toString());

//        mWebView.loadUrl("https://play.google.com");
        mWebView.addJavascriptInterface(new Object(){
            @JavascriptInterface
            public void send(String message){
                Log.i("JAVASCRIPT","message is "+message);
            }
        },"androidObject");

//        mWebView.loadUrl("http://10.0.2.2:8080/and/");//javascript object injection is tested with localhost

        return v;
    }

    public WebView getWebView() {
        return mWebView;
    }

    public boolean canGoBack(){
        return mWebView.canGoBack();
    }

    public void goBack(){
        mWebView.goBack();
    }
}
