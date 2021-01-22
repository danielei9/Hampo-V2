package com.example.hampo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.hampo.AdapterHamposFirestoreUI;
import com.example.hampo.Aplicacion;
import com.example.hampo.R;
import com.example.hampo.main.SectionsPagerAdapterHampoList;
import com.example.hampo.presentacion.CreateHampoActivity;
import com.example.hampo.presentacion.SpacesItemDecoration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.comun.MQTT;

import static org.example.comun.MQTT.topicRoot;


public class nav_mis_hampos extends Fragment implements MqttCallback {
    private MqttClient client;
    public static String idJaulaToSyncWifiNFCRAWTAG;
    private String idJaula;
    private RecyclerView recyclerView;
    private LayoutInflater inflaterMain;
    private ViewGroup containerMain;
    private View vistaMain;
    private EditText ssid;
    private EditText password;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static AdapterHamposFirestoreUI adaptador;
    private String id;
    public static boolean syncStatus;

    public nav_mis_hampos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crearConexionMQTT();
        id = ((Aplicacion) getActivity().getApplication()).id;
        adaptador = ((Aplicacion) getActivity().getApplication()).adaptador;
        syncStatus = cargarSyncStatusDeFicheroCache(getContext());
        Log.d("syncstatus", "Syncstatus en cache = " + syncStatus);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflaterMain = inflater;
        containerMain = container;
        /**************************************************************************/
        View vista;
        Log.d("syncstatus", "Syncstatus= " + syncStatus);
        if (syncStatus) {
            vista = inflater.inflate(R.layout.fragment_nav_mis_hampos, container, false);
            recyclerView = (RecyclerView) vista.findViewById(R.id.recyclerView);
            FloatingActionButton fab;

            id = ((Aplicacion) getActivity().getApplication()).id;
            recyclerView.addItemDecoration(new SpacesItemDecoration(16));
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            llenarLista();

            recyclerView.setAdapter(adaptador);

            adaptador.startListening();

            //asignas escuchador para cada sitio y visualizar el sitio que aprietes
            adaptador.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = recyclerView.getChildLayoutPosition(v);
                    Intent i = new Intent(getContext(), MiHampo.class);
                    i.putExtra("id", adaptador.getKey(pos));
                    startActivity(i);
                }
            });

            fab = vista.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lanzarCrearHampo();
                }
            });
            vistaMain = vista;
            checkIfUserHasHampos();
        } else {
            vista = inflater.inflate(R.layout.hampolistempty, container, false);
            /**************************************/
            SectionsPagerAdapterHampoList SectionsPagerAdapterHampoList = new SectionsPagerAdapterHampoList(getContext(), getActivity().getSupportFragmentManager());
            ViewPager viewPager = vista.findViewById(R.id.view_pagerEmptyHampo);
            viewPager.setAdapter(SectionsPagerAdapterHampoList);
            TabLayout tabs = vista.findViewById(R.id.tabsEmptyHampo);
            tabs.setupWithViewPager(viewPager);
            if (getActivity().getIntent() != null) {
                Bundle extras = getActivity().getIntent().getExtras();
                if (extras != null) {
                    idJaula = extras.getString("id");
                    if (idJaula != null) {
                        Toast.makeText(getContext(), "Id jaula nfc: " + idJaula, Toast.LENGTH_SHORT).show();
                        Log.d("Coso", "Id jaula nfc: " + idJaula);
                    }
                }
            }
            ssid = vista.findViewById(R.id.ssidEditText);
            password = vista.findViewById(R.id.passwordEditText);
            vista.findViewById(R.id.sendWifiParamsBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enviarMensajeMQTT(ssid.getText() + "&" + password.getText(), "luzhampo/wifiUpdate/" + nav_mis_hampos.idJaulaToSyncWifiNFCRAWTAG);
                }
            });
            /**************************************/
        }
        return vista;
    }

    private void checkIfUserHasHampos() {
        FirebaseFirestore.getInstance().collection(id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Si es true el usuario no tiene hampos
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(getActivity().getApplicationContext(), "No tienes hampos", Toast.LENGTH_LONG).show();
                                TextView textView = vistaMain.findViewById(R.id.textViewGone);
                                textView.setVisibility(View.VISIBLE);
                                ImageView arrowImg = vistaMain.findViewById(R.id.arrow);
                                arrowImg.setVisibility(View.VISIBLE);
                                ImageView syncDoneImg = vistaMain.findViewById(R.id.syncDoneImg);
                                syncDoneImg.setVisibility(View.VISIBLE);
                                CardView cardSyncDone = vistaMain.findViewById(R.id.cardViewSyncDone);
                                cardSyncDone.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Si tienes hampos", Toast.LENGTH_LONG).show();
                                Log.d("CheckUserHampos", "Si tienes hampos");
                                TextView textView = vistaMain.findViewById(R.id.textViewGone);
                                textView.setVisibility(View.GONE);
                                ImageView arrowImg = vistaMain.findViewById(R.id.arrow);
                                arrowImg.setVisibility(View.GONE);
                                ImageView syncDoneImg = vistaMain.findViewById(R.id.syncDoneImg);
                                syncDoneImg.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                CardView cardSyncDone = vistaMain.findViewById(R.id.cardViewSyncDone);
                                cardSyncDone.setVisibility(View.GONE);
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("CheckUserHampos", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("CheckUserHampos", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        adaptador = ((Aplicacion) getActivity().getApplication()).adaptador;
        if (adaptador != null) {
            adaptador.startListening();
        }
    }

    public void lanzarCrearHampo() {
        Intent i = new Intent(getContext(), CreateHampoActivity.class);
        startActivity(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adaptador.stopListening();
    }


    private void llenarLista() {

        CollectionReference hampos = db.collection("hampos");

        // seleccionar hampos que tienen la uid del usuario
    }

    public static void guardarEnFicheroCache(boolean syncStatusCache, Context context) {
        // Creo el fichero que se almacenará en cache en modo privado(solo accesible desde nuestra app)
        SharedPreferences ficheroCache = context.getSharedPreferences("ficheroCache", Context.MODE_PRIVATE);

        // Creo el editor del fichero que me permite hacer modificaciones
        SharedPreferences.Editor editorFicheroCache = ficheroCache.edit();
        editorFicheroCache.putBoolean("syncStatus", syncStatusCache);

        editorFicheroCache.commit();
    }

    public static boolean cargarSyncStatusDeFicheroCache(Context context) {
        // Creo el fichero que se almacenará en cache en modo privado(solo accesible desde nuestra app)
        SharedPreferences ficheroCache = context.getSharedPreferences("ficheroCache", Context.MODE_PRIVATE);

        return ficheroCache.getBoolean("syncStatus", false);
    }

    /*************************/
    // MQTT STUFF
//Crear conexion con el broker MQTT
    public void crearConexionMQTT() {
        try {
            Log.i(MQTT.TAG, "Conectando al broker " + MQTT.broker);
            client = new MqttClient(MQTT.broker, "9753",
                    new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill(topicRoot + "WillTopic", "App desconectada".getBytes(), MQTT.qos, false);
            client.connect(connOpts);
        } catch (MqttException e) {
            Log.e(MQTT.TAG, "Error al conectar.", e);
        }
    }

    //Suscribirse a un topic
    public void escucharDeTopicMQTT(String subTopic) {
        try {
            Log.e(MQTT.TAG, "Suscrito a " + subTopic);
            client.subscribe(subTopic, MQTT.qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(MQTT.TAG, "Error al suscribir.", e);
        }
    }

    //Publicar mensaje en un topic
    public void enviarMensajeMQTT(String data, String subTopic) {
        try {
            Log.i(MQTT.TAG, "Publicando mensaje: " + data);
            MqttMessage message = new MqttMessage(data.getBytes());
            message.setQos(MQTT.qos);
            message.setRetained(false);
            client.publish(subTopic, message);
        } catch (
                MqttException e) {
            Log.e(MQTT.TAG, "Error al publicar.", e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(MQTT.TAG, "Conexión perdida: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Log.e("mqttDebug", "message arrived");
        try {
            String payload = new String(message.getPayload());
            Log.d(MQTT.TAG, "Recibiendo: " + topic + "->" + payload);
            Log.d(MQTT.TAG, topic);
            Log.d(MQTT.TAG, "jaulaSyncDone/" + idJaula);

            if (topic.equalsIgnoreCase("luzhampo/jaulaSyncDone/" + idJaula)) {

            }
        } catch (Exception e) {
            Log.d(MQTT.TAG, "Error en message arrived callback: " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(MQTT.TAG, "Entrega completa");
    }
}