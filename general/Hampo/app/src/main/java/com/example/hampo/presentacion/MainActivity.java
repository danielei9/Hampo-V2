package com.example.hampo.presentacion;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.hampo.Aplicacion;
import com.example.hampo.R;
import com.example.hampo.ServicioMusica;
import com.example.hampo.casos_uso.CasosUsoHampo;
import com.example.hampo.fragments.MiHampoFragment;
import com.example.hampo.fragments.RawSyncroFragment;
import com.example.hampo.ui.nav_mis_hampos;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.comun.MQTT;

import java.io.UnsupportedEncodingException;

import static org.example.comun.MQTT.topicRoot;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    // Proveedor para obtener la localización del usuario
    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseAuth mAuth;

    private AppBarConfiguration mAppBarConfiguration;

    private SharedPreferences pref;

    NfcAdapter nfcAdapter;
    public String mensaje;
    private FirebaseFirestore db;
    private String id;
    public static MqttClient client;
    private String idJaula;
    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Crea una instancia del cliente de proveedor de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setContentView(R.layout.activity_main);
        crearConexionMQTT();
        mAuth = FirebaseAuth.getInstance();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        db = FirebaseFirestore.getInstance();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        id = ((Aplicacion) getApplication()).id;

        //preferencias

        if (pref.getBoolean("tema", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else if (!pref.getBoolean("tema", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Musica
        if (id != null) {
            if (pref.getBoolean("musica", true))
                startService(new Intent(MainActivity.this,
                        ServicioMusica.class));
            else if (!pref.getBoolean("musica", true))
                stopService(new Intent(MainActivity.this,
                        ServicioMusica.class));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_mis_hampos, R.id.nav_mi_perfil, R.id.nav_config, R.id.nav_adiestramiento, R.id.nav_FAQ)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        performNfcOperation(getIntent());
        CasosUsoHampo.solicitarPermiso("android.permission.ACCESS_FINE_LOCATION",
                "Se requieren permisos para acceder a la ubicación del dispositivo, " +
                        "la localización solo será necesaria para situar la jaula. Después puede ir" +
                        "a ajustes de la aplicación para denegar el acceso a la localización", 69, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            lanzarPreferencias(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void lanzarPreferencias(View v) {
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivity(i);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void cerrarSesion(View v) {
        if (v.getId() == R.id.btn_cerrar_sesion) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, CustomLoginActivity.class));
                            finish();
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (id == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        //enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(MainActivity.this,
                ServicioMusica.class));
        disableForegroundDispatchSystem();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        performNfcOperation(intent);
        super.onNewIntent(intent);
    }

    private void performNfcOperation(Intent intent) {
        if (intent.hasExtra(nfcAdapter.EXTRA_TAG)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (parcelables != null && parcelables.length > 0) {
                readTextFromMessage((NdefMessage) parcelables[0]);
                Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                idJaula = mensaje;
                ((Aplicacion) getApplication()).idJaulaApplication = idJaula;
                idUser = ((Aplicacion) getApplication()).getUserId();
                escucharDeTopicMQTT("luzhampo/jaulaSyncDone/" + idJaula);
                /*enviarMensajeMQTT("jaulaSyncDone/" + idJaula,
                        "jaulaSyncDone/" + idJaula);*/
                // no lo envia porque se actualiza antes la actividad ****
                enviarMensajeMQTT(idUser + "&" + idJaula,
                        "luzhampo/jaulaSync/" + idJaula
                );
                DocumentReference existe = db.collection(id).document(mensaje);
                existe.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            /*
                            if (document.exists()) {
                                // Si el id de la jaula existe
                                Intent i = new Intent(MainActivity.this, MiHampo.class);
                                Log.d("Coso", "Id jaula nfc on bucle: " + mensaje);
                                i.putExtra("id", mensaje);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(MainActivity.this, CreateHampoActivity.class);
                                Log.d("Coso", "Id jaula nfc on bucle: " + mensaje);
                                i.putExtra("id", mensaje);
                                startActivity(i);
                            }*/
                        } else {

                        }
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "No NDEF messages found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            mensaje = tagContent;
        } else {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);


    }

    private void disableForegroundDispatchSystem() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable", Toast.LENGTH_SHORT).show();
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
        } catch (Exception e) {
            Log.e("FormatTag", e.getMessage());

        }

    }


    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);

        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    /*************************/
    // MQTT STUFF
//Crear conexion con el broker MQTT
    public void crearConexionMQTT() {
        try {
            Log.i(MQTT.TAG, "Conectando al broker " + MQTT.broker);
            client = new MqttClient(MQTT.broker, "12454524524566",
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
                // Guardo la localizacion de la jaula en el momento de la sincronización
                // ya que el usuario estará cerca de la jaula, nos vale ya que la raspberry no tiene GPS)
                saveUserLocation();
                Log.d("syncstatus", "sync completada satisfactoriamente");
                escucharDeTopicMQTT("luzhampo/jaulaWifiSyncDone/" + idJaula);
                nav_mis_hampos.idJaulaToSyncWifiNFCRAWTAG = idJaula;
                ((Aplicacion) getApplication()).idJaulaApplication = idJaula;
                //nav_mis_hampos.syncStatus = true;
            } else if (topic.equalsIgnoreCase("luzhampo/jaulaWifiSyncDone/" + idJaula)){
                nav_mis_hampos.guardarEnFicheroCache(true, this);
                //Intent intent = getActivity().getIntent();
                //getActivity().finish();
                startActivity(new Intent(this, MainActivity.class));
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d("LocationDebug", "Entrando en getLastLocation success listener");
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            ((Aplicacion) getApplication()).localizacionDeLaJaula = location;
                            Log.d("LocationDebug", "localizacion guardada con exito");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LocationDebug", "error: " + e.getMessage());
                    }
                });
    }
}