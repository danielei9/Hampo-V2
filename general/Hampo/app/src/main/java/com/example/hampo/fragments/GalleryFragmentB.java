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
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hampo.AdaptadorImagenesB;
import com.example.hampo.Imagen;
import com.example.hampo.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class GalleryFragmentB extends Fragment {
    private StorageReference storageRef;
    public AdaptadorImagenesB adaptador;
    private View vista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("imagee", "onCreateView");
        Toast.makeText(getActivity().getApplicationContext(),"onCreateView",Toast.LENGTH_SHORT).show();

        vista = inflater.inflate(R.layout.fragment_b_gallery, container, false);
        //Log.e("imagee","Entro FRAGMENT");

        storageRef = FirebaseStorage.getInstance().getReference();

        RecyclerView recyclerView = vista.findViewById(R.id.recyclerView);
        Query query = FirebaseFirestore.getInstance()
                .collection("imagenes/").orderBy("tiempo", Query.Direction.DESCENDING); //BAJAR DE LAS COLECCIONES CON REFERENCIA URL
        FirestoreRecyclerOptions<Imagen> opciones = new FirestoreRecyclerOptions
                .Builder<Imagen>().setQuery(query, Imagen.class).build();
        adaptador = new AdaptadorImagenesB(getContext(), opciones);
        recyclerView.setAdapter(adaptador);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adaptador.startListening();
        return vista;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
         Toast.makeText(getActivity().getApplicationContext(),"onActivityCreated",Toast.LENGTH_SHORT).show();

        Log.e("Galeria", "onActivityCreated");
        final RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.e("CLICKS", "CLICKOK " + adaptador.getItem(position).getTitulo());
                       // startActivity(new Intent(getContext(), GalleryFragment.class));
/*
                        // Crear fragmento de tu clase
                        Fragment fragment = new GalleryFragmentA();
                        // Obtener el administrador de fragmentos a través de la actividad
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        // Definir una transacción
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        // Remplazar el contenido principal por el fragmento
                        fragmentTransaction.replace(R.id.view_pager_gallery, fragment);
                       // fragmentTransaction.addToBackStack(null);
                        // Cambiar
                        fragmentTransaction.commit();*/

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.e("CLICKS", "CLICKOK LONG");
                        ImageButton deleteImge = (ImageButton) vista.findViewById(R.id.deleteImage);
                        deleteImge.setVisibility(View.VISIBLE);
                    }
                })
        );
       /* Button button_upload = (Button) getView().findViewById(R.id.button_upload2);
        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getActivity().getApplicationContext(),"Pulsando start",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, 1234);
                Log.e("Galeria","Entro boton");            }
        });*/
    }

    @Override
    public void onActivityResult(final int requestCode,
                                 final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1234) {
                String nombreFichero = UUID.randomUUID().toString();
                subirFichero(data.getData(), "Gallery/" + nombreFichero); // STORAGE
                Log.e("Galeria", "Subir fichero");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adaptador.startListening();
        Toast.makeText(getActivity().getApplicationContext(),"START",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStop() {
        super.onStop();
        adaptador.stopListening();
    }
    @Override
    public void onPause() {
        super.onPause();

    }  @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getActivity().getApplicationContext(),"RESUME",Toast.LENGTH_SHORT).show();

    }

    private void subirFichero(Uri fichero, String referencia) {
        final StorageReference ref = storageRef.child(referencia);
        UploadTask uploadTask = ref.putFile(fichero);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful())
                    throw task.getException();
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.e("Almacenamiento", "URL: " + downloadUri.toString());
                    registrarImagen("Subida por Móvil", downloadUri.toString());
                } else {
                    Log.e("Almacenamiento", "ERROR: subiendo fichero");
                }
            }
        });
    }

    private void registrarImagen(String subida_por_movil, String toString) {
        Imagen imagen = new Imagen(subida_por_movil, toString);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection("imagenes"); //FIREBASE
        collection.document().set(imagen);
    }

}
