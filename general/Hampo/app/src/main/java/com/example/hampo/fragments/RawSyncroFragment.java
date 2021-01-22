package com.example.hampo.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.hampo.Aplicacion;
import com.example.hampo.R;
import com.example.hampo.presentacion.MainActivity;
import com.example.hampo.ui.nav_mis_hampos;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.comun.MQTT;

import static org.example.comun.MQTT.topicRoot;

public class RawSyncroFragment extends Fragment implements MqttCallback {
    private EditText idJaula;
    private String idUser;
    public static MqttClient client = null;
    private View vista;

    // Proveedor para obtener la localización del usuario
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Crea una instancia del cliente de proveedor de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        crearConexionMQTT();
        vista = inflater.inflate(R.layout.rawsyncrofragment, container, false);
        idUser = ((Aplicacion) getActivity().getApplication()).getUserId();
        idJaula = vista.findViewById(R.id.idJaula);
        vista.findViewById(R.id.syncbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escucharDeTopicMQTT("luzhampo/jaulaSyncDone/" + idJaula.getText().toString());
                /*enviarMensajeMQTT("luzhampo/jaulaSyncDone/" + idJaula.getText().toString(),
                        "luzhampo/jaulaSyncDone/" + idJaula.getText().toString());*/
                enviarMensajeMQTT(idUser + "&" + idJaula.getText().toString(),
                        "luzhampo/jaulaSync/" + idJaula.getText().toString()
                );
            }
        });
        return vista;
    }

    //Crear conexion con el broker MQTT
    public void crearConexionMQTT() {
        try {
            Log.i(MQTT.TAG, "Conectando al broker " + MQTT.broker);
            client = new MqttClient(MQTT.broker, MQTT.clientId,
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
            Log.d(MQTT.TAG, "luzhampo/jaulaSyncDone/" + idJaula.getText().toString());

            if (topic.equalsIgnoreCase("luzhampo/jaulaSyncDone/" + idJaula.getText().toString())) {
                saveUserLocation();
                Log.d(MQTT.TAG, "sync confirmation");
                escucharDeTopicMQTT("luzhampo/jaulaWifiSyncDone/" + idJaula.getText().toString());
                nav_mis_hampos.idJaulaToSyncWifiNFCRAWTAG = idJaula.getText().toString();
                Log.d("idJaulaToSyncWifiNFCRAWTAG","idJaulaToSyncWifiNFCRAWTAG = "+nav_mis_hampos.idJaulaToSyncWifiNFCRAWTAG);
            } else if (topic.equalsIgnoreCase("luzhampo/jaulaWifiSyncDone/" + idJaula.getText().toString())){
                nav_mis_hampos.guardarEnFicheroCache(true, getContext());
                //Intent intent = getActivity().getIntent();
                //getActivity().finish();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        } catch (Exception e) {
            Log.d(MQTT.TAG, "Error en message arrived callback: " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(MQTT.TAG, "Entrega completa");
    }

    public void saveUserLocation() {
        Log.d("LocationDebug", "Entrando en getLastLocation()");

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d("LocationDebug", "Entrando en getLastLocation success listener");
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            ((Aplicacion) getActivity().getApplication()).localizacionDeLaJaula = location;
                            Log.d("LocationDebug", "localizacion guardada con exito");
                        }
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LocationDebug", "error: " + e.getMessage());
                    }
                });
    }
}
