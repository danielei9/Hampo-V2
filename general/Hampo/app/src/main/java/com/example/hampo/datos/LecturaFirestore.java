package com.example.hampo.datos;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hampo.modelo.Lectura;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class LecturaFirestore implements LecturaAsinc {


    @Override
    public void ultimaLectura(String id, String id_jaula, final EscuchadorElemento escuchador) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query lecturas = db.collection(id).document(id_jaula).collection("Lecturas").orderBy("Fecha").limit(1);

        lecturas.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().size()==1){

                        Lectura lectura = task.getResult().getDocuments().get(0).toObject(Lectura.class);
                        escuchador.onRespuesta(lectura);
                    }
                } else {
                    Log.e("Firebase", "Error al leer", task.getException());
                    escuchador.onRespuesta(null);
                }
            }
        });
    }

}
