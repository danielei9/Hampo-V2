package com.example.hampo.fragments;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.hampo.AdaptadorImagenes;
import com.example.hampo.Imagen;
import com.example.hampo.R;
import com.example.hampo.SectionsPagerAdapterGallery;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class GalleryFragment extends Fragment {
    private StorageReference storageRef;
    public AdaptadorImagenes adaptador;
    private View vista;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("imagee", "onCreateView");
        vista = inflater.inflate(R.layout.fragment_gallery,container,false);
        context = getContext();
        SectionsPagerAdapterGallery sectionsPagerAdapter = new SectionsPagerAdapterGallery(context, getActivity().getSupportFragmentManager());
       ViewPager viewPager = vista.findViewById(R.id.view_pager_gallery);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = vista.findViewById(R.id.tabs_gallery);
        tabs.setupWithViewPager(viewPager);
        return vista;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        Log.e("Galeria", "onActivityCreated");


    }

    @Override
    public void onActivityResult(final int requestCode,
                                 final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}