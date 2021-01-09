package com.example.photogallery;

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

import java.util.List;
import java.util.logging.Logger;

public class PhotoGalleryFragment extends Fragment {

    Logger mLogger=Logger.getLogger(getClass().getName());

    private RecyclerView mPhotoRecyclerView;

    List<GalleryItem> mGalleryItemList;

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentPhotoGalleryBinding binding= DataBindingUtil.inflate(inflater,R.layout.fragment_photo_gallery,container,false);

        mPhotoRecyclerView=binding.photoRecyclerView;

        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        FetchItemsTask fetchItemsTask=new FetchItemsTask(mGalleryItemList);//todo dagger

        fetchItemsTask.execute();

        if (mGalleryItemList!=null){
           mLogger.info("size is "+mGalleryItemList.size());
            mPhotoRecyclerView.setAdapter(new GridAdapter(mGalleryItemList));
        }

        return binding.getRoot();
    }

    private class GridViewHolder extends RecyclerView.ViewHolder{

        TextView mTextView;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView=itemView.findViewById(R.id.item_title);
        }

        void setItem(String text){

           mTextView.setText(text);
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

           return new GridViewHolder(getLayoutInflater().inflate(R.layout.list_item,parent,false ));
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
}
