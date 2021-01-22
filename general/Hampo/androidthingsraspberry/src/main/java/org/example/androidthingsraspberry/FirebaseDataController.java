package org.example.androidthingsraspberry;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static org.example.androidthingsraspberry.MainActivity.idJaula;
import static org.example.androidthingsraspberry.MainActivity.idJaulaOnDatabase;
import static org.example.androidthingsraspberry.MainActivity.idUser;

public class FirebaseDataController {
    //Cuando se realiza la vinculación recibo por MQTT el id del usuario asociado a la jaula
    private FirebaseFirestore db;
    private DocumentReference jaula;

    public FirebaseDataController() {
        this.db = FirebaseFirestore.getInstance();
        //this.jaula = db.collection(MainActivity.idUser).document(MainActivity.idJaula);
    }

    public void sendSensorsData(SensorsData sensorsData) {
        Log.d("sensorsData", "Dentro de sendSensorsData()");
        Log.d("sensorsData", "sendSensordataIdUser = " + idUser);
        Log.d("sensorsData", "sendSensordataIdJaula = " + idJaulaOnDatabase);

        db.collection(idUser).document(idJaulaOnDatabase).collection("datosJaula")
                .add(sensorsData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("sensorsData", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("sensorsData", "Error adding document", e);
                    }
                });
    }

    //Obtiene los datos de la bdd ordenados ascendentemente por fecha
    public void getSensorsData(SensorsData sensorsData) {
        jaula.collection("datosSensores").orderBy("timemilis", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("sensorsData", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("sensorsData", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    //Obtiene los datos de la bdd ordenados ascendentemente por fecha y con limit 1
    public void getSensorsDataLastLecture(SensorsData sensorsData) {
        jaula.collection("datosSensores").orderBy("timemilis", Query.Direction.ASCENDING).limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("sensorsData", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("sensorsData", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}