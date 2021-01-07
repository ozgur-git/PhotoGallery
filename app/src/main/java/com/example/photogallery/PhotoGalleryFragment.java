package com.example.photogallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding;

public class PhotoGalleryFragment extends Fragment {

    private RecyclerView mPhotoRecyclerView;

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

        FetchItemsTask fetchItemsTask=new FetchItemsTask();

        fetchItemsTask.execute();

        return binding.getRoot();
    }
}
