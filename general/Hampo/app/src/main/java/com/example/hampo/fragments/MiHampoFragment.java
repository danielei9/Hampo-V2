package com.example.hampo.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hampo.Aplicacion;
import com.example.hampo.R;
import com.example.hampo.casos_uso.CasosUsoHampo;
import com.example.hampo.casos_uso.CasosUsoMQTT;
import com.example.hampo.datos.EscuchadorHampo;
import com.example.hampo.datos.HamposFirestore;
import com.example.hampo.modelo.Hampo;
import com.example.hampo.presentacion.EditHampoActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MiHampoFragment extends Fragment {
    private String userId;
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
    private CardView botonCompartir;
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
    private View vista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.mi_hampo,container,false);
        Bundle extras = getActivity().getIntent().getExtras();
        idJaula = extras.getString("id");
        userId = ((Aplicacion) getActivity().getApplicationContext()).id;
        hampoDb = new HamposFirestore(userId);
        cuh = new CasosUsoHampo(getActivity(), ((Aplicacion) getActivity().getApplicationContext()).hampos);
        mqttController = new CasosUsoMQTT();
        mqttController.crearConexionMQTT("1234548612");

        nombre = vista.findViewById(R.id.nombreHampo);
        raza = vista.findViewById(R.id.razaHampo);
        imagenHampo = vista.findViewById(R.id.imagenHampo);

        progresoComida = vista.findViewById(R.id.progresoComida);
        progresoActividad = vista.findViewById(R.id.progresoActividad);
        progresoBebida = vista.findViewById(R.id.progresoBebida);
        totalComida = vista.findViewById(R.id.fondoComida);
        totalActividad = vista.findViewById(R.id.fondoActividad);
        totalBebida = vista.findViewById(R.id.fondoBebida);
        viewDistancia = vista.findViewById(R.id.viewDistancia);
        tipoIluminacion = vista.findViewById(R.id.tipoIluminacion);
        luzUV = vista.findViewById(R.id.luzUV);
        sexoView = vista.findViewById(R.id.sexoView);

        botonEditar = vista.findViewById(R.id.botonEditar);
        botonCompartir = vista.findViewById(R.id.botonCompartir);
        botonBorrar = vista.findViewById(R.id.botonBorrar);
        botonLuz = vista.findViewById(R.id.botonLuz);
        botonLuzUV = vista.findViewById(R.id.botonLuzUV);
        botonAdiestrar = vista.findViewById(R.id.botonAdiestrar);
        botonAlimentar = vista.findViewById(R.id.botonAlimentar);

        fotoBombilla = vista.findViewById(R.id.fotoBombilla);
        fotoBombillaUV = vista.findViewById(R.id.fotoBombillaUV);

        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarEditar(v);
            }
        });
        botonCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               shareImage( store(getScreenShot(getView()), UUID.randomUUID().toString()));
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
                mqttController.enviarMensajeMQTT("luzhampo/adiestramiento", "luzhampo/adiestramiento");
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


        Toast.makeText(getContext(), idJaula, Toast.LENGTH_SHORT).show();
        actualizarDatosHampo();
        actualizarDatosLectura();
        return vista;
    }
    private void shareImage(File file){
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No App Available", Toast.LENGTH_SHORT).show();
        }
    }
    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }
    public static File store(Bitmap bm, String fileName){
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dir;
    }
    private void actualizarDatosHampo() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        hampoDb.elemento(idJaula, new EscuchadorHampo() {
            @Override
            // Callback encargado de cargar los datos recibidos de la bdd en el layout
            public void onRespuesta(Hampo hampo) {
                Log.d("Coso", "hampo uri: " + hampo.getUriFoto());
                h = hampo;

                Glide.with(getContext())
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
        Query lecturas = db.collection(userId).document(idJaula).collection("datosJaula").orderBy("uploadedOnTimestamp", Query.Direction.ASCENDING).limit(1);

        lecturas.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() == 1) {

                        aux = vista.findViewById(R.id.porcentajeIluminacion);
                        String luminosidad = task.getResult().getDocuments().get(0).getData().get("luminosidad").toString();
                        aux.setText(luminosidad.substring(0,luminosidad.length()-3) + "%");

                        aux = vista.findViewById(R.id.temperatura);
                        String temperatura = task.getResult().getDocuments().get(0).getData().get("temperatura").toString();
                        aux.setText(temperatura.substring(0,temperatura.length()-3) + "ºC");

                        aux = vista.findViewById(R.id.porcentajeHumedad);
                        String porcentajeHumedad = task.getResult().getDocuments().get(0).getData().get("humedad").toString();
                        aux.setText(porcentajeHumedad.substring(0,porcentajeHumedad.length()-3) + "%");

                        aux = vista.findViewById(R.id.porcentajeComida);
                        String porcentajeComida = task.getResult().getDocuments().get(0).getData().get("comederoCm").toString();
                        aux.setText(porcentajeComida.substring(0,porcentajeComida.length()-3) + "%");

                        aux = vista.findViewById(R.id.porcentajeBebida);
                        String porcentajeBebida = task.getResult().getDocuments().get(0).getData().get("bebederoCm").toString();
                        aux.setText(porcentajeBebida.substring(0,porcentajeBebida.length()-3) + "%");

                        aux = vista.findViewById(R.id.porcentajeActividad);
                        String porcentajeActividad = task.getResult().getDocuments().get(0).getData().get("movimiento").toString();
                        aux.setText(porcentajeActividad.substring(0,porcentajeActividad.length()-3) + "%");

                        aux = vista.findViewById(R.id.viewDistancia);
                        String viewDistancia = task.getResult().getDocuments().get(0).getData().get("metrosRecorridos").toString();
                        aux.setText(viewDistancia.substring(0,viewDistancia.length()-3) + "cm");

                        actualizarBarras(task.getResult().getDocuments().get(0).getData().get("bebederoCm").toString(), task.getResult().getDocuments().get(0).getData().get("comederoCm").toString(), task.getResult().getDocuments().get(0).getData().get("movimiento").toString());
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
        double porcentajeB = totalB * Double.parseDouble(b) / 100;
        double porcentajeC = totalC * Double.parseDouble(c) / 100;
        double porcentajeA = totalA * Double.parseDouble(a) / 100;


        ConstraintLayout.LayoutParams lpC = (ConstraintLayout.LayoutParams) progresoComida.getLayoutParams();
        lpC.width = (int)porcentajeC;
        progresoComida.setLayoutParams(lpC);

        ConstraintLayout.LayoutParams lpA = (ConstraintLayout.LayoutParams) progresoActividad.getLayoutParams();
        lpA.width = (int)porcentajeA;
        progresoActividad.setLayoutParams(lpA);

        ConstraintLayout.LayoutParams lpB = (ConstraintLayout.LayoutParams) progresoBebida.getLayoutParams();
        lpB.width = (int)porcentajeB;
        progresoBebida.setLayoutParams(lpB);

    }


    public void getRazas(final String nombreRaza) {
        HamposFirestore db = new HamposFirestore(userId);
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
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        cuh.borrar(idJaula);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("¿Deseas eliminar el hampo, no podrás deshacer esta acción?").setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarDatosHampo();
        actualizarDatosLectura();

    }
}
