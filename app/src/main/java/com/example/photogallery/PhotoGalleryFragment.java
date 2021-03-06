package com.example.photogallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding;
import com.example.photogallery.databinding.ListItemBinding;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static androidx.appcompat.widget.SearchView.OnQueryTextListener;

public class PhotoGalleryFragment extends VisibleFragment {
    Logger mLogger=Logger.getLogger(getClass().getName());

    private RecyclerView mPhotoRecyclerView;
    private List<Photo> mGalleryItemList=new ArrayList<>();
    private ProgressBar mProgressBar;

    private ThumbnailDownloader<GridViewHolder> mThumbnailDownloader;
    int pageNumber;

    private static final int BASE_INDEX=11;

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        pageNumber=1;
        updateItems();
//        FetchItemsTask fetchItemsTask=new FetchItemsTask();
//        fetchItemsTask.execute(pageNumber);

//        Intent intent=PollService.newIntent(getActivity());
//        getActivity().startService(intent);

        Handler handler=new Handler();

        mThumbnailDownloader=new ThumbnailDownloader<>(handler);

        mThumbnailDownloader.setThumbnailDownloadListener((target,bitmap)->target.setItem(bitmap));

        mThumbnailDownloader.start();
        PollService.setServiceAlarm(getActivity(),true);
//        mThumbnailDownloader.getLooper();
        mLogger.info("Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLogger.info("onCreateView is called");
        FragmentPhotoGalleryBinding binding= DataBindingUtil.inflate(inflater,R.layout.fragment_photo_gallery,container,false);
        mPhotoRecyclerView=binding.photoRecyclerView;
        mProgressBar=binding.progressBar;
//        updateItems();
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
//                    recyclerView.setVisibility(View.INVISIBLE);
//                    mProgressBar.setVisibility(View.VISIBLE);
                    updateItems();
//                    FetchItemsTask fetchItemsTask=new FetchItemsTask();
//                    fetchItemsTask.execute(pageNumber);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_item_clear:{
                QueryReferences.setStoredQuery(getActivity(),null);
                updateItems();
                update();
                return true;
            }
            case R.id.menu_item_toggle_polling:{
                boolean shouldStartAlarm=!PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(),shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;

            }
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery,menu);

        MenuItem searchItem=menu.findItem(R.id.menu_item_search);
        MenuItem toggleItem=menu.findItem(R.id.menu_item_toggle_polling);

        if (PollService.isServiceAlarmOn(getActivity())){
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }

        final SearchView searchView= (SearchView) searchItem.getActionView();

        searchView.setOnSearchClickListener(v -> searchView.setQuery(QueryReferences.getStoredQuery(getActivity()),false));

        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mLogger.info("submitted query is "+query);
                QueryReferences.setStoredQuery(getActivity(), query);
                mPhotoRecyclerView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                updateItems();
//                update();
                searchView.onActionViewCollapsed();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void updateItems() {
        new FetchItemsTask().execute(String.valueOf(pageNumber),QueryReferences.getStoredQuery(getActivity()));
    }

    void update(){
        mLogger.info("update has been called");
        mPhotoRecyclerView.setAdapter(new GridAdapter(mGalleryItemList));
    }

    private class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Photo mPhoto;
        ImageView mImageView;

        public GridViewHolder(@NonNull View itemView,ListItemBinding binding) {
            super(itemView);
//            mTextView=binding.itemTitle;
            mImageView=binding.itemImageView;
            mImageView.setOnClickListener(this);
//            mTextView=itemView.findViewById(R.id.item_title);
        }

        void setPhoto(Photo photo){
            mPhoto=photo;

        }

        void setItem(Bitmap bitmap){
            mImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onClick(View v) {
//            Intent intent=new Intent(Intent.ACTION_VIEW,mPhoto.getPhotoPageUri());
            Intent intent=PhotoPageActivity.newIntent(getActivity(),mPhoto.getPhotoPageUri());
            startActivity(intent);
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
            Photo[] photoArray=new Photo[21];
            mThumbnailDownloader.queueThumbnail(holder,createArray(position,photoArray),position);
            holder.setPhoto(photoArray[BASE_INDEX]);
        }

        @Override
        public int getItemCount() {
            return mGalleryItemList.size();
        }

        private String[] createArray(int position,Photo[] photoArray){

            mLogger.info("create array position is "+position);

            String[] returnArray=new String[21];

            int gallerySize=mGalleryItemList.size();
            int lowerLimit=((position-10)<0)?position-10+gallerySize:position-10;
            int higherLimit=((position+10)<0)?position+10-gallerySize:position+10;

            int k=lowerLimit;

            for (int j=0;j<=20;j++){

                if (k<gallerySize-1){
                    photoArray[j]=mGalleryItemList.get(k);
                    returnArray[j]=mGalleryItemList.get(k).getUrl_s();
                    k++;
                } else if (k==gallerySize){
                    photoArray[j]=mGalleryItemList.get(0);
                    returnArray[j]=mGalleryItemList.get(0).getUrl_s();
                    k++;
                } else {
                    photoArray[j]=mGalleryItemList.get(k-gallerySize+1);
                    returnArray[j]=mGalleryItemList.get(k-gallerySize+1).getUrl_s();
                    k++;
                }
            }

            return returnArray;
        }
    }
    class FetchItemsTask extends AsyncTask<String,Void,List<Photo>> {

        Logger mLogger=Logger.getLogger(getClass().getName());
        @Inject
        FlickrFetchr mFlickrFetchr;

        public FetchItemsTask() {
            PhotoGalleryComponent component=DaggerPhotoGalleryComponent.builder().photoGalleryModule(new PhotoGalleryModule()).build();
            component.inject(this);
            mLogger.info("fetchitemstask is called");
        }

        @Override
        protected List<Photo> doInBackground(String... params) {
            mLogger.info("fetchitemstask is executed");
//            try {
//                mLogger.info("web"+(new FlickrFetchr()).getUrlString("https://www.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=1cfa2ec314b06495f0eeb3416212f275&format=json&nojsoncallback=1"));
//            } catch (IOException e) {
//                e.printStackTrace();

            //            }
//            mProgressBar.setVisibility(View.VISIBLE);

            String query=params[1];

            if (query==null){
                return mFlickrFetchr.fetchRecentPhotos(Integer.parseInt(params[0]));
            } else {
                return mFlickrFetchr.searchPhotos(query,Integer.parseInt(params[0]));
            }
        }

        @Override
        protected void onPostExecute(List<Photo> items) {
            super.onPostExecute(items);
            mGalleryItemList=items;
            update();
            mPhotoRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
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
