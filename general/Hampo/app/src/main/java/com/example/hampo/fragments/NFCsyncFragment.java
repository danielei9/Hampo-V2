package com.example.hampo.fragments;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;

        import com.example.hampo.Aplicacion;
        import com.example.hampo.R;
        import com.example.hampo.presentacion.MainActivity;
        import com.example.hampo.ui.nav_mis_hampos;

        import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
        import org.eclipse.paho.client.mqttv3.MqttCallback;
        import org.eclipse.paho.client.mqttv3.MqttClient;
        import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
        import org.eclipse.paho.client.mqttv3.MqttException;
        import org.eclipse.paho.client.mqttv3.MqttMessage;
        import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
        import org.example.comun.MQTT;

        import static org.example.comun.MQTT.topicRoot;

public class NFCsyncFragment extends Fragment implements MqttCallback {
    private EditText nfcRawId;
    private EditText idPrivadaJaula;
    private String idUser;
    public static MqttClient client = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        crearConexionMQTT();
        final View vista = inflater.inflate(R.layout.fragment_nfc_sync, container, false);
        idUser = ((Aplicacion) getActivity().getApplication()).getUserId();
        idPrivadaJaula = vista.findViewById(R.id.idJaula);
        /*vista.findViewById(R.id.syncbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escucharDeTopicMQTT("jaulaSyncDone/" + idPrivadaJaula.getText().toString());
                String idUser = ((Aplicacion) getActivity().getApplication()).getUserId();
                enviarMensajeMQTT("jaulaSyncDone/" + idPrivadaJaula.getText().toString(),"jaulaSyncDone/" + idPrivadaJaula.getText().toString());
                enviarMensajeMQTT(idUser + "&" + nfcRawId.getText().toString(),
                        "jaulaSync/" + idPrivadaJaula.getText().toString()
                );
            }
        });*/
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
            Log.d(MQTT.TAG, "jaulaSyncDone/" + idPrivadaJaula.getText().toString());

            if (topic.equalsIgnoreCase("jaulaSyncDone/" + idPrivadaJaula.getText().toString())) {
                Log.d(MQTT.TAG, "sync confirmation");
                nav_mis_hampos.syncStatus=true;
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
}
