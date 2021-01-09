package com.example.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    List<GalleryItem> mGalleryItemList=new ArrayList<>();
//    FetchItemsTask fetchItemsTask;

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        FetchItemsTask fetchItemsTask=new FetchItemsTask();
        fetchItemsTask.execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLogger.info("onCreateView is called");
        FragmentPhotoGalleryBinding binding= DataBindingUtil.inflate(inflater,R.layout.fragment_photo_gallery,container,false);
        mPhotoRecyclerView=binding.photoRecyclerView;
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        update();
        return binding.getRoot();

    }

    void update(){
        mPhotoRecyclerView.setAdapter(new GridAdapter(mGalleryItemList));

    }

    private class GridViewHolder extends RecyclerView.ViewHolder{

//        GalleryItem mGalleryItem;

        TextView mTextView;


        public GridViewHolder(@NonNull View itemView,ListItemBinding binding) {
            super(itemView);
            mTextView=binding.itemTitle;
//            mTextView=itemView.findViewById(R.id.item_title);
        }

        void setItem(String text){
           mTextView.setText(text);
//            mGalleryItem=galleryItem;
        }
    }

    private class GridAdapter extends RecyclerView.Adapter<GridViewHolder>{
        List<GalleryItem> mGalleryItemList;

        public GridAdapter(List<GalleryItem> galleryItemList) {
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
            holder.setItem(mGalleryItemList.get(position).getCaption());

        }

        @Override
        public int getItemCount() {
            return mGalleryItemList.size();
        }
    }
    class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {

        Logger mLogger=Logger.getLogger(getClass().getName());
        @Inject
        FlickrFetchr mFlickrFetchr;

        public FetchItemsTask() {
            PhotoGalleryComponent component=DaggerPhotoGalleryComponent.builder().photoGalleryModule(new PhotoGalleryModule()).build();
            component.inject(this);
            mLogger.info("fetchitemstask is called");
        }

        public List<GalleryItem> getGalleryItemList() {
            return mGalleryItemList;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void...params) {
            mLogger.info("fetchitemstask is executed");
            //            mLogger.info("web"+(new FlickrFetchr()).getUrlString("https://www.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=1cfa2ec314b06495f0eeb3416212f275&format=json&nojsoncallback=1"));
            return mFlickrFetchr.fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            super.onPostExecute(items);
            mGalleryItemList=items;
            update();
        }
    }


}
