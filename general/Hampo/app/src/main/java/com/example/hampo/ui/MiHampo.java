package com.example.hampo.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.hampo.Aplicacion;
import com.example.hampo.R;
import com.example.hampo.casos_uso.CasosUsoHampo;
import com.example.hampo.casos_uso.CasosUsoMQTT;
import com.example.hampo.chart.SensorsData;
import com.example.hampo.datos.HamposFirestore;
import com.example.hampo.main.SectionsPagerAdapterMiHampo;
import com.example.hampo.modelo.Hampo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class MiHampo extends AppCompatActivity {

    // Viende de aplicacion getUid
    private String idUser;
    // Viene de los extras en intent de nav_mi_hampo
    private String idJaula;

    private CollectionReference hampo;
    private DocumentReference jaula;

    private TextView nombre;
    private TextView raza;

    private String TAG = "Coso";

    private TextView aux;

    private TextView progresoComida;
    private TextView viewDistancia;
    private TextView progresoActividad;
    private TextView progresoBebida;
    private TextView tipoIluminacion;
    private TextView luzUV;
    private TextView sexoView;
    private ConstraintLayout totalComida;
    private ConstraintLayout totalActividad;
    private ConstraintLayout totalBebida;

    private CardView botonEditar;
    private CardView botonBorrar;
    private CardView botonLuz;
    private CardView botonLuzUV;
    private CardView botonAdiestrar;
    private CardView botonAlimentar;

    private ImageView fotoBombilla;
    private ImageView fotoBombillaUV;
    private ImageView imagenHampo;

    private int iluminacion = 0;
    private int iluminacionUV = 0;


    private StorageReference mStorageRef;
    private HamposFirestore hampoDb;
    private Hampo h = new Hampo();
    private CasosUsoHampo cuh;
    private CasosUsoMQTT mqttController;

    public ArrayList<Float> datosTemperatura = new ArrayList<>();
    public ArrayList<Float> datosHumedad = new ArrayList<>();
    public ArrayList<Float> datosLuminosidad = new ArrayList<>();
    public ArrayList<String> fechasTemperatura = new ArrayList<>();
    CollectionReference db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mihampo_main);

        /**************************************/
        Bundle extras = getIntent().getExtras();
        idJaula = extras.getString("id");
        Toast.makeText(MiHampo.this, "Id jaula nfc: " + idJaula, Toast.LENGTH_SHORT).show();
        Log.d("Coso", "Id jaula nfc: " + idJaula);
        idUser = ((Aplicacion) getApplication()).id;
        hampoDb = new HamposFirestore(idUser);
        
        //Tablayout with viewpager
        /**************************************/
        Log.d("IdUserDebug", "Id user dentro de mi hampo");
        db = FirebaseFirestore.getInstance().collection(idUser);
        //db = FirebaseFirestore.getInstance().collection("zL8YlNGe51MZyX8VbNMM0vzyI7l1");
        // Datos jaula
        // Cargo los datos de las gráficas
        //db.document("taDVh1M7JolXGXCDziQn").collection("datosSensores")
        db.document(idJaula).collection("datosJaula")
                .orderBy("uploadedOnTimestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SensorsData datosSensores = document.toObject(SensorsData.class);
                                if (CasosUsoHampo.checkTodayDate(Long.parseLong(datosSensores.getUploadedOnTimestamp()))) {
                                    Log.d("debugGrafica", datosSensores.toString());
                                    Log.d("debugGrafica", document.getId() + " => " + document.getData());
                                    datosTemperatura.add(Float.parseFloat(datosSensores.getTemperatura()));
                                    datosHumedad.add(Float.parseFloat(datosSensores.getHumedad()));
                                    datosLuminosidad.add(Float.parseFloat(datosSensores.getLuminosidad()));
                                    Log.d("debugGrafica", "temperatura = " + datosTemperatura.get(datosTemperatura.size() - 1));
                                    fechasTemperatura.add(CasosUsoHampo.fromTimemilisToStringDate(Long.parseLong(datosSensores.getUploadedOnTimestamp())));
                                    //fechasTemperatura.add(datosSensores.getTimestamp());
                                    Log.d("debugGrafica", "fecha temperatura = " + fechasTemperatura.get(fechasTemperatura.size() - 1));
                                }
                            }
                            SectionsPagerAdapterMiHampo sectionsPagerAdapterMiHampo = new SectionsPagerAdapterMiHampo(getApplicationContext(), getSupportFragmentManager(), fechasTemperatura, datosTemperatura, datosHumedad, datosLuminosidad);
                            ViewPager viewPager = findViewById(R.id.view_pager);
                            viewPager.setAdapter(sectionsPagerAdapterMiHampo);
                            TabLayout tabs = findViewById(R.id.tabs);
                            tabs.setupWithViewPager(viewPager);
                        } else {
                            Log.w("debugGrafica", "Error getting documents.", task.getException());
                        }
                    }
                });
        /*

        nombre = findViewById(R.id.nombreHampo);
        raza = findViewById(R.id.razaHampo);
        imagenHampo = findViewById(R.id.imagenHampo);

        cuh = new CasosUsoHampo(this, ((Aplicacion) getApplication()).hampos);
        mqttController = new CasosUsoMQTT();
        mqttController.crearConexionMQTT("1234548612");

        progresoComida = findViewById(R.id.progresoComida);
        progresoActividad = findViewById(R.id.progresoActividad);
        progresoBebida = findViewById(R.id.progresoBebida);
        totalComida = findViewById(R.id.fondoComida);
        totalActividad = findViewById(R.id.fondoActividad);
        totalBebida = findViewById(R.id.fondoBebida);
        viewDistancia = findViewById(R.id.viewDistancia);
        tipoIluminacion = findViewById(R.id.tipoIluminacion);
        luzUV = findViewById(R.id.luzUV);
        sexoView = findViewById(R.id.sexoView);

        botonEditar = findViewById(R.id.botonEditar);
        botonBorrar = findViewById(R.id.botonBorrar);
        botonLuz = findViewById(R.id.botonLuz);
        botonLuzUV = findViewById(R.id.botonLuzUV);
        botonAdiestrar = findViewById(R.id.botonAdiestrar);
        botonAlimentar = findViewById(R.id.botonAlimentar);

        fotoBombilla = findViewById(R.id.fotoBombilla);
        fotoBombillaUV = findViewById(R.id.fotoBombillaUV);

        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarEditar(v);
            }
        });

        botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarBorrar(v);
            }
        });

        botonLuzUV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iluminacionUV == 1) {
                    mqttController.enviarMensajeMQTT("luzhampo/uv/on", "luzhampo/uv/on");
                    iluminacionUV--;
                    fotoBombillaUV.setImageResource(R.drawable.ic_light_bulb_uv);
                    luzUV.setText("Encendida");
                } else {
                    mqttController.enviarMensajeMQTT("luzhampo/uv/off", "luzhampo/uv/off");
                    iluminacionUV++;
                    fotoBombillaUV.setImageResource(R.drawable.ic_light_bulb);
                    luzUV.setText("Apagada");
                }
            }
        });

        botonAdiestrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //codigo para el adiestramiento
            }
        });

        botonAlimentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //codigo para alimentar
                mqttController.enviarMensajeMQTT("luzhampo/alimentar", "luzhampo/alimentar");
            }
        });

        botonLuz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (iluminacion) {
                    case 0:
                        iluminacion++;
                        fotoBombilla.setImageResource(R.drawable.ic_light_bulb);
                        tipoIluminacion.setText("Apagadas");
                        mqttController.enviarMensajeMQTT("luzhampo/both/off", "luzhampo/both/off");
                        break;
                    case 1:
                        iluminacion++;
                        fotoBombilla.setImageResource(R.drawable.ic_light_bulb_on);
                        tipoIluminacion.setText("Arriba");
                        mqttController.enviarMensajeMQTT("luzhampo/top/on", "luzhampo/top/on");
                        break;
                    case 2:
                        iluminacion++;
                        fotoBombilla.setImageResource(R.drawable.ic_light_bulb_on);
                        tipoIluminacion.setText("Abajo");
                        mqttController.enviarMensajeMQTT("luzhampo/down/on", "luzhampo/down/on");
                        break;
                    case 3:
                        iluminacion = 0;
                        fotoBombilla.setImageResource(R.drawable.ic_light_bulb_on);
                        tipoIluminacion.setText("Ambas");
                        mqttController.enviarMensajeMQTT("luzhampo/both/on", "luzhampo/both/on");
                        break;
                }
            }
        });


        Toast.makeText(MiHampo.this, idJaula, Toast.LENGTH_SHORT).show();
        actualizarDatosHampo();
        actualizarDatosLectura();*/
    }


    /*
    private void actualizarDatosHampo() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        hampoDb.elemento(idJaula, new EscuchadorHampo() {
            @Override
            // Callback encargado de cargar los datos recibidos de la bdd en el layout
            public void onRespuesta(Hampo hampo) {
                Log.d("Coso", "hampo uri: " + hampo.getUriFoto());
                h = hampo;

                Glide.with(MiHampo.this)
                        .load(hampo.getUriFoto())
                        .into(imagenHampo);

                // Cargo el nombre del hampo
                nombre.setText(hampo.getNombre());

                // Cargo la raza del hampo
                getRazas(hampo.getRaza());

                // Cargo el sexo
                if (hampo.getSex().equals("male")) {
                    sexoView.setText("♂️");
                } else if (hampo.getSex().equals("female")) {
                    sexoView.setText("♀️");
                } else {
                    sexoView.setText("Otro");
                }
            }
        });

    }

    private void actualizarDatosLectura() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query lecturas = db.collection(id).document(idJaula).collection("Lecturas").orderBy("Fecha").limit(1);

        lecturas.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() == 1) {

                        aux = findViewById(R.id.porcentajeIluminacion);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Iluminacion").toString() + "%");

                        aux = findViewById(R.id.temperatura);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Temperatura").toString() + "ºC");

                        aux = findViewById(R.id.porcentajeHumedad);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Humedad").toString() + "%");

                        aux = findViewById(R.id.porcentajeComida);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Comedero").toString() + "%");

                        aux = findViewById(R.id.porcentajeBebida);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Bebedero").toString() + "%");

                        aux = findViewById(R.id.porcentajeActividad);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Actividad").toString() + "%");

                        aux = findViewById(R.id.viewDistancia);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Distancia").toString() + "cm");

                        actualizarBarras(task.getResult().getDocuments().get(0).getData().get("Bebedero").toString(), task.getResult().getDocuments().get(0).getData().get("Comedero").toString(), task.getResult().getDocuments().get(0).getData().get("Actividad").toString());
                    }
                } else {
                    Log.e("Firebase", "Error al leer", task.getException());
                }
            }
        });


    }

    private void actualizarBarras(String b, String c, String a) {

        int totalB = totalBebida.getWidth();
        int totalA = totalActividad.getWidth();
        int totalC = totalComida.getWidth();
        int porcentajeB = totalB * Integer.parseInt(b) / 100;
        int porcentajeC = totalC * Integer.parseInt(c) / 100;
        int porcentajeA = totalA * Integer.parseInt(a) / 100;


        ConstraintLayout.LayoutParams lpC = (ConstraintLayout.LayoutParams) progresoComida.getLayoutParams();
        lpC.width = porcentajeC;
        progresoComida.setLayoutParams(lpC);

        ConstraintLayout.LayoutParams lpA = (ConstraintLayout.LayoutParams) progresoActividad.getLayoutParams();
        lpA.width = porcentajeA;
        progresoActividad.setLayoutParams(lpA);

        ConstraintLayout.LayoutParams lpB = (ConstraintLayout.LayoutParams) progresoBebida.getLayoutParams();
        lpB.width = porcentajeB;
        progresoBebida.setLayoutParams(lpB);

    }


    public void getRazas(final String nombreRaza) {
        HamposFirestore db = new HamposFirestore(id);
        db.db.collection("raza").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> arrayListRazas = new ArrayList<>();
                final HashMap<String, String> hashMapComida = new HashMap<String, String>();
                final HashMap<String, String> hashMapBebida = new HashMap<String, String>();

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        arrayListRazas.add(document.getId());
                    }
                    String[] arrayRazas = new String[arrayListRazas.size()];
                    arrayRazas = arrayListRazas.toArray(arrayRazas);
                    Log.d("Coso", arrayRazas[Integer.parseInt(nombreRaza)]);
                    raza.setText(arrayRazas[Integer.parseInt(nombreRaza)]);

                } else {
                    Log.e("Razas", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void lanzarEditar(View view) {
        Intent i = new Intent(view.getContext(), EditHampoActivity.class);
        i.putExtra("idJaula", idJaula);
        startActivity(i);

    }

    private void lanzarBorrar(View view) {
        //Intent i = new Intent(view.getContext(),CreateHampoActivity.class);
        //startActivity(i);
        cuh.borrar(idJaula);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarDatosHampo();
        actualizarDatosLectura();

    }*/
}
