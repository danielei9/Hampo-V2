package com.example.hampo.fragments;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hampo.AdaptadorImagenes;
import com.example.hampo.Aplicacion;
import com.example.hampo.Imagen;
import com.example.hampo.R;
import com.example.hampo.casos_uso.CasosUsoMQTT;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class GalleryFragmentA extends Fragment {
    private StorageReference storageRef;
    public AdaptadorImagenes adaptador;
    private View vista;
    private CasosUsoMQTT mqttController;
    private String nombreFichero;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("imagee" ,"onCreateView");
        vista = inflater.inflate(R.layout.fragment_a_gallery,container,false);

        mqttController = new CasosUsoMQTT();
        mqttController.crearConexionMQTT("1234548612");

        storageRef = FirebaseStorage.getInstance().getReference();

        RecyclerView recyclerView = vista.findViewById(R.id.recyclerView);
        /*Query query = FirebaseFirestore.getInstance()
                .collection("imagenes/").orderBy("tiempo", Query.Direction.ASCENDING).limitToLast(1);*/ //BAJAR DE LAS COLECCIONES CON REFERENCIA URL
        Query query = FirebaseFirestore.getInstance()
                .collection(((Aplicacion)getActivity().getApplication()).id).document(((Aplicacion)getActivity().getApplication()).idJaulaOnDatabase).collection("imagenes").orderBy("tiempo", Query.Direction.ASCENDING).limitToLast(1);
        FirestoreRecyclerOptions<Imagen> opciones = new FirestoreRecyclerOptions
                .Builder<Imagen>().setQuery(query, Imagen.class).build();
        adaptador = new AdaptadorImagenes(getContext(), opciones);
        recyclerView.setAdapter(adaptador);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptador.startListening();
        return vista;
    }
void reloadFragment(){
    // Reload current fragment
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    if (Build.VERSION.SDK_INT >= 26) {
        ft.setReorderingAllowed(false);
    }
    ft.detach(this).attach(this).commit();
}
    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        ImageView button_upload_from_gallery = getView().findViewById(R.id.button_upload_from_gallery);
        button_upload_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getActivity().getApplicationContext(),"Pulsando start",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, 1234);
                Log.e("Galeria","Entro boton");            }
        });

        ImageView button_make_photo = getView().findViewById(R.id.button_make_photo);
        button_make_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(),"Haciendo foto",Toast.LENGTH_SHORT).show();
                mqttController.enviarMensajeMQTT("camarahampo/mkfoto", "camarahampo/mkfoto");
            // da error Log.e("STRINGSs",  String.valueOf(adaptador.getItem(0).getTiempo()));
            }
        });

        ImageView button_delete = getView().findViewById(R.id.button_delete);
        button_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nombreFichero = adaptador.getItem(0).getTitulo();

                deleteFile();
            }
        });


    }

    @Override
    public void onActivityResult(final int requestCode,
                                 final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1234) {
                 nombreFichero = UUID.randomUUID().toString();
                subirFichero(data.getData(), "Gallery/" ); // STORAGE
                Log.e("Galeria","Subir fichero");
            }
        }
    }
    @Override public void onStart() {
        super.onStart();
        adaptador.startListening();
    }
    @Override public void onStop() {
        super.onStop();
        adaptador.stopListening();
    }

    private void subirFichero(Uri fichero, String referencia) {
        final StorageReference ref = storageRef.child(referencia+nombreFichero);
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
                    registrarImagen(nombreFichero, downloadUri.toString());
                    reloadFragment();
                } else {
                    Log.e("Almacenamiento", "ERROR: subiendo fichero");
                }
            }
        });
    }

    private void registrarImagen(String name, String Url) {
        Imagen imagen = new Imagen(name,Url);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection("imagenes"); //FIREBASE
        collection.document().set(imagen);
    }
    public void deleteFile() {
        final String[] document_ = new String[1];
        StorageReference referenciaImagen = storageRef.child("Gallery/" + nombreFichero );
        Log.e("Almacenamiento","Gallery/"+ nombreFichero);

        referenciaImagen.delete()
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d("Almacenamiento", "Fichero borrado");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //Error al subir el fichero
                        Log.d("Almacenamiento", "Fichero no borrado");
                    }
                });

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
         db.collection("imagenes")
                .whereEqualTo("titulo", nombreFichero)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                document_[0] = document.getId();
                                deleteAtPath("imagenes/",document.getId());
                                Log.d("TAG","imagenes/"+document.getId());
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void deleteAtPath(final String path,String nameId) {

        FirebaseFirestore docRef = FirebaseFirestore.getInstance();
        DocumentReference selectedDoc = docRef.collection(path).document(nameId);
        selectedDoc.delete();
        /*
        Map<String, Object> data = new HashMap<>();
        data.put("path", path);
        HttpsCallableReference deleteFn =
                FirebaseFunctions.getInstance().getHttpsCallable("recursiveDelete");
        deleteFn.call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        Log.e("TAG","OK");
                        // Delete Success
                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG","No OK: "+e + " " + path);
                        // Delete failed
                        // ...
                    }
                });*/
    }
}
