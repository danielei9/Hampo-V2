package com.example.hampo;

import android.app.Application;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hampo.datos.HamposAsinc;
import com.example.hampo.datos.HamposFirestore;
import com.example.hampo.modelo.Hampo;
import com.example.hampo.presentacion.LoginActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Aplicacion extends Application {
    public HamposAsinc hampos;
    public AdapterHamposFirestoreUI adaptador;

    public FirebaseAuth auth;
    public String id;
    public Location localizacionDeLaJaula;
    public String idJaulaApplication;
    public String idJaulaOnDatabase;
    public boolean userHasHampos;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        refrescar();
    }

    public void refrescar() {
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {

            id = auth.getUid();
            hampos = new HamposFirestore(id);
            Query query = FirebaseFirestore.getInstance()
                    .collection(id);
            // Si la query devuelve null significa que no hay ningun userId en la colección de usuarios
            if (query == null) {
                FirebaseFirestore.getInstance().collection("/").document(id);
            }
            FirestoreRecyclerOptions<Hampo> opciones = new FirestoreRecyclerOptions
                    .Builder<Hampo>().setQuery(query, Hampo.class).build();
            adaptador = new AdapterHamposFirestoreUI(opciones, this);

        } else {
            Intent i = new Intent(this.getBaseContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

    }

    public String getUserId() {
        return auth.getUid();
    }
}