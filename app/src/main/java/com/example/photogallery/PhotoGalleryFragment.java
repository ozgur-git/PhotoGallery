package com.example.photogallery;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding;
import com.example.photogallery.databinding.ListItemBinding;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PhotoGalleryFragment extends Fragment {
    Logger mLogger=Logger.getLogger(getClass().getName());

    private RecyclerView mPhotoRecyclerView;
    private List<Photo> mGalleryItemList=new ArrayList<>();
    private ThumbnailDownloader<GridViewHolder> mThumbnailDownloader;
    int pageNumber;

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        pageNumber=1;
        FetchItemsTask fetchItemsTask=new FetchItemsTask();
        fetchItemsTask.execute(pageNumber);

        Handler handler=new Handler();

        mThumbnailDownloader=new ThumbnailDownloader<>(handler);

        mThumbnailDownloader.setThumbnailDownloadListener((target,bitmap)-> target.setItem(bitmap));

        mThumbnailDownloader.start();
//        mThumbnailDownloader.getLooper();
        mLogger.info("Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLogger.info("onCreateView is called");
        FragmentPhotoGalleryBinding binding= DataBindingUtil.inflate(inflater,R.layout.fragment_photo_gallery,container,false);
        mPhotoRecyclerView=binding.photoRecyclerView;
        ViewTreeObserver observer=mPhotoRecyclerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(() -> {
            int measuredWidth=mPhotoRecyclerView.getMeasuredWidth();
            mLogger.info("measured width is "+ measuredWidth);
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics ();
            display.getMetrics(outMetrics);
            float density  = getResources().getDisplayMetrics().density;

            ((GridLayoutManager)mPhotoRecyclerView.getLayoutManager()).setSpanCount(measuredWidth /(int)(120*density));
        });

        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)){
                    pageNumber=(pageNumber>=10)?1:++pageNumber;
                    mLogger.info("bottom "+pageNumber);
                    FetchItemsTask fetchItemsTask=new FetchItemsTask();
                    fetchItemsTask.execute(pageNumber);
                }
           }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        update();
        return binding.getRoot();
    }

    void update(){
        mPhotoRecyclerView.setAdapter(new GridAdapter(mGalleryItemList));

    }

    private class GridViewHolder extends RecyclerView.ViewHolder{

        TextView mTextView;
        ImageView mImageView;

        public GridViewHolder(@NonNull View itemView,ListItemBinding binding) {
            super(itemView);
//            mTextView=binding.itemTitle;
            mImageView=binding.itemImageView;
//            mTextView=itemView.findViewById(R.id.item_title);
        }

        void setItem(String text){
           mTextView.setText(text);
        }

        void setItem(Bitmap bitmap){
            mImageView.setImageBitmap(bitmap);
        }
    }

    private class GridAdapter extends RecyclerView.Adapter<GridViewHolder>{
        List<Photo> mGalleryItemList;

        public GridAdapter(List<Photo> galleryItemList) {
            mGalleryItemList = galleryItemList;
        }

        @NonNull
        @Override
        public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListItemBinding binding=DataBindingUtil.inflate(LayoutInflater.from(getActivity()),R.layout.list_item,parent,false );
            return new GridViewHolder(binding.getRoot(),binding);

//           return new GridViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.list_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {

//            holder.setItem(mGalleryItemList.get(position).getTitle()+"\n"+" count"+position);
//            holder.setItem(getActivity().getResources().getDrawable(R.drawable.front));
//            String[] urlArray=createArray(position);
            mThumbnailDownloader.queueThumbnail(holder,createArray(position),position);
        }

        @Override
        public int getItemCount() {
            return mGalleryItemList.size();
        }

        private String[] createArray(int position){

            String[] returnArray=new String[21];
            int lowerLimit=((position-10)<0)?position-10+100:position-10;
            int higherLimit=((position+10)<0)?position+10-100:position+10;

            int k=lowerLimit;

            for (int j=0;j<=20;j++){

                if (k<100){
                    returnArray[j]=mGalleryItemList.get(k).getUrl_s();
                    k++;
                } else if (k==100){
                    returnArray[j]=mGalleryItemList.get(0).getUrl_s();
                    k++;
                } else {
                    returnArray[j]=mGalleryItemList.get(k-100).getUrl_s();
                    k++;
                }
            }

            return returnArray;

        }
    }
    class FetchItemsTask extends AsyncTask<Integer,Void,List<Photo>> {

        Logger mLogger=Logger.getLogger(getClass().getName());
        @Inject
        FlickrFetchr mFlickrFetchr;

        public FetchItemsTask() {
            PhotoGalleryComponent component=DaggerPhotoGalleryComponent.builder().photoGalleryModule(new PhotoGalleryModule()).build();
            component.inject(this);
            mLogger.info("fetchitemstask is called");
        }

        @Override
        protected List<Photo> doInBackground(Integer... pageNumber) {
            mLogger.info("fetchitemstask is executed");
//            try {
//                mLogger.info("web"+(new FlickrFetchr()).getUrlString("https://www.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=1cfa2ec314b06495f0eeb3416212f275&format=json&nojsoncallback=1"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return mFlickrFetchr.downloadGalleryItems(pageNumber[0]);
        }

        @Override
        protected void onPostExecute(List<Photo> items) {
            super.onPostExecute(items);
            mGalleryItemList=items;
            update();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        mThumbnailDownloader.clearQueue();
        mLogger.info("Background thread destroyed");
    }
}
