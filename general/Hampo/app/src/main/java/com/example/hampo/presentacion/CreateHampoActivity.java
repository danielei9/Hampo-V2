package com.example.hampo.presentacion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import com.example.hampo.Aplicacion;
import com.example.hampo.R;
import com.example.hampo.datos.HamposFirestore;
import com.example.hampo.modelo.Hampo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.comun.MQTT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static org.example.comun.MQTT.topicRoot;

public class CreateHampoActivity extends AppCompatActivity implements MqttCallback {
    public static MqttClient client = null;

    private ImageView imagenHampo;
    private Uri uriUltimaFoto;
    private ConstraintLayout layout;
    private int spinnerSelectedItem = -1;
    private HamposFirestore db;
    private StorageReference mStorageRef;
    private Uri downloadUri;
    private EditText nombreEditText;
    private String sexOptionSelected;
    private String id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crearConexionMQTT();

        setContentView(R.layout.create_hampo);
        id = ((Aplicacion) getApplication()).id;
        db = new HamposFirestore(id);
        nombreEditText = findViewById(R.id.editTextNombre);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        getRazas();
        layout = findViewById(R.id.myConstraintLayout);
        findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
        imagenHampo = findViewById(R.id.imagenHampo);
        //Cuando clica sobre el frameLayout para añadir una imagen
        findViewById(R.id.frameLayout1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.VISIBLE);
                layout.setBackgroundColor(Color.parseColor("#00000000"));
            }
        });

        //Cuando clica sobre el layout con el desplegable visible
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setBackgroundColor(Color.parseColor("#00000000"));
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
            }
        });
        //Cuando clica sobre la imagen de galeria
        findViewById(R.id.cardGaleria).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setBackgroundColor(Color.parseColor("#00000000"));
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
                ponerDeGaleria(1);
            }
        });

        //Cuando clica sobre la imagen de la camara
        findViewById(R.id.cardCamara).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setBackgroundColor(Color.parseColor("#00000000"));
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
                uriUltimaFoto = manejadorFotoHampo();
            }
        });

        //Cuando clica sobre el boton crear hampo
        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText nombreEditText = findViewById(R.id.editTextNombre);
                subirImagenDeHampo(uriUltimaFoto);
                //Hampo hampoToAdd = new Hampo(nombreEditText.getText().toString(), downloadUri.toString(), Aplicacion.getId(), String.valueOf(spinnerSelectedItem));
                //db.anade(hampoToAdd);
                //finish();
            }
        });

        /*
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.arrayRazasHamster, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        final TextView textViewComida = findViewById(R.id.textViewComida);
        final TextView textViewBebida = findViewById(R.id.textViewBebida);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // guardo el item seleccinado en la variable global
                spinnerSelectedItem = position;
                switch (position) {
                    case 0:
                        textViewComida.setText("🍖 400 gr/dia");
                        textViewBebida.setText("💧 350 ml/dia");
                        break;
                    case 1:
                        textViewComida.setText("🍖 200 gr/dia");
                        textViewBebida.setText("💧 50 ml/dia");
                        break;
                    case 2:
                        textViewComida.setText("🍖 300 gr/dia");
                        textViewBebida.setText("💧 150 ml/dia");
                        break;
                    case 3:
                        textViewComida.setText("🍖 450 gr/dia");
                        textViewBebida.setText("💧 350 ml/dia");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });*/
        findViewById(R.id.femaleCard).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                femaleOptionSelected(v);
            }
        });
        findViewById(R.id.maleCard).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                maleOptionSelected(v);
            }
        });
        /*mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_mis_hampos, R.id.nav_mi_perfil, R.id.nav_config, R.id.nav_tinder, R.id.nav_FAQ)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
*/
    }

    public void visualizarFoto(ImageView imageView, String uri) {
        imageView.setImageBitmap(reduceBitmap(this, uri, 2048, 2048));
    }

    private Uri manejadorFotoHampo() {
        return tomarFoto(2);
    }

    private Bitmap reduceBitmap(Context contexto, String uri,
                                int maxAncho, int maxAlto) {
        try {
            InputStream input = null;
            Uri u = Uri.parse(uri);
            if (u.getScheme().equals("http") || u.getScheme().equals("https")) {
                input = new URL(uri).openStream();
            } else {
                input = contexto.getContentResolver().openInputStream(u);
            }
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = (int) Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(input, null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(contexto, "Fichero/recurso de imagen no encontrado",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Toast.makeText(contexto, "Error accediendo a imagen: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }

    public Uri tomarFoto(int codigoSolicitud) {
        try {
            Uri uriUltimaFoto;
            File file = File.createTempFile(
                    "img_" + (System.currentTimeMillis() / 1000), ".jpg",
                    this.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            if (Build.VERSION.SDK_INT >= 24) {
                uriUltimaFoto = FileProvider.getUriForFile(
                        this, "com.example.hampo", file);
            } else {
                uriUltimaFoto = Uri.fromFile(file);
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriUltimaFoto);
            this.startActivityForResult(intent, codigoSolicitud);
            return uriUltimaFoto;
        } catch (IOException ex) {
            Toast.makeText(this, "Error al crear fichero de imagen",
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public void femaleOptionSelected(View view) {
        layout.setBackgroundColor(Color.parseColor("#ffffff"));
        findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
        CardView maleCard = findViewById(R.id.maleCard);
        maleCard.setCardBackgroundColor(12);
        CardView femaleCard = findViewById(R.id.femaleCard);
        femaleCard.setCardBackgroundColor(getResources().getColor(R.color.femaleColor));
        sexOptionSelected = "female";
    }

    public void maleOptionSelected(View view) {
        layout.setBackgroundColor(Color.parseColor("#ffffff"));
        findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
        CardView femaleCard = findViewById(R.id.femaleCard);
        femaleCard.setCardBackgroundColor(12);
        CardView maleCard = findViewById(R.id.maleCard);
        maleCard.setCardBackgroundColor(getResources().getColor(R.color.maleColor));
        sexOptionSelected = "male";
    }

    public void returnBtnFn(View view) {
        finish();
    }

    public void createBtnFn(View view) {

    }

    // FOTOGRAFÍAS
    public void ponerDeGaleria(int codigoSolicitud) {
        String action;
        if (Build.VERSION.SDK_INT >= 19) { // API 19 - Kitkat
            action = Intent.ACTION_OPEN_DOCUMENT;
        } else {
            action = Intent.ACTION_PICK;
        }
        Intent intent = new Intent(action,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, codigoSolicitud);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                imagenHampo.setImageURI(Uri.parse(data.getDataString()));
                uriUltimaFoto = Uri.parse(data.getDataString());
            } else {
                Toast.makeText(this, "Foto no cargada", Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == 2) {
            //Log.e(Mqtt.TAG, "uri: " + uriUltimaFoto.toString());
            visualizarFoto(imagenHampo, uriUltimaFoto.toString());
        }
    }

    public void getRazas() {
        db.db.collection("raza").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> arrayListRazas = new ArrayList<>();
                final HashMap<String, String> hashMapComida = new HashMap<String, String>();
                final HashMap<String, String> hashMapBebida = new HashMap<String, String>();

                int contador = 0;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        arrayListRazas.add(document.getId());
                        hashMapComida.put(String.valueOf(contador), document.get("comida").toString());
                        hashMapBebida.put(String.valueOf(contador), document.get("bebida").toString());
                        //Log.d("Razas", document.getId() + " => " + document.getData().toString());
                        //Log.d("Razas", contador + " => " + hashMapComida.get(String.valueOf(contador)));
                        contador++;
                    }
                    String[] arrayRazas = new String[arrayListRazas.size()];
                    arrayRazas = arrayListRazas.toArray(arrayRazas);
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, arrayRazas);
                    Spinner spinner = (Spinner) findViewById(R.id.spinnerRaza);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(arrayAdapter);

                    final TextView textViewComida = findViewById(R.id.textViewComida);
                    final TextView textViewBebida = findViewById(R.id.textViewBebida);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            // guardo el item seleccinado en la variable global
                            spinnerSelectedItem = position;
                            textViewComida.setText(hashMapComida.get(String.valueOf(position)));
                            textViewBebida.setText(hashMapBebida.get(String.valueOf(position)));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } else {
                    Log.e("Razas", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    //Sube la foto a fire storage y obtiene la download URI
    public void subirImagenDeHampo(Uri uriFoto) {
        Uri file = uriFoto;
        final StorageReference riversRef = mStorageRef.child("hampoimages/" + id + ".jpg");
        UploadTask uploadTask = riversRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                Log.d("Subirfoto", riversRef.getDownloadUrl().toString());
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUri = task.getResult();
                    Log.d("Subirfoto", downloadUri.toString());
                    //Creo el hampo una vez tengo la uri de descargar (asincrona)
                    Hampo hampoToAdd = new Hampo(nombreEditText.getText().toString(), downloadUri.toString(), String.valueOf(spinnerSelectedItem), sexOptionSelected);
                    db.anade(hampoToAdd);
                    sendIdJaulaDatabase();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    /// =========================================MQTT==========================================================
    //Crear conexion con el broker MQTT
    public void crearConexionMQTT() {
        try {
            Log.i(MQTT.TAG, "Conectando al broker " + MQTT.broker);
            client = new MqttClient(MQTT.broker, "84561",
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

    //Publicar mensaje en un topic
    public void enviarMensajeMQTT(String data, String subTopic) {
        try {
            Log.i(MQTT.TAG, "Publicando mensaje: " + data);
            MqttMessage message = new MqttMessage(data.getBytes());
            message.setQos(MQTT.qos);
            message.setRetained(false);
            client.publish(topicRoot + subTopic, message);
        } catch (
                MqttException e) {
            Log.e(MQTT.TAG, "Error al publicar.", e);
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

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(MQTT.TAG, "Conexión perdida: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(MQTT.TAG, "Entrega completa");
    }

    public void sendIdJaulaDatabase() {
        Log.d("idJaulaOnDatabase", "Dentro de sendIdJaulaDatabase");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String idUser = ((Aplicacion) getApplication()).id;

        db.collection(idUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("idJaulaOnDatabase", "id de la jaula autogenerado -> " + document.getId());
                                String idJaulaOnDatabase = document.getId();
                                // Enviar idJaulaDatabase
                                String idJaulaNfcRawTag = ((Aplicacion) getApplication()).idJaulaApplication;
                                Log.d("idJaulaOnDatabase", "id de la jaula nfc raw tag -> " + idJaulaNfcRawTag);
                                ((Aplicacion) getApplication()).idJaulaOnDatabase = idJaulaOnDatabase;
                                enviarMensajeMQTT(idJaulaOnDatabase, "idJaulaOnDatabaseUpdate/" + idJaulaNfcRawTag);
                            }
                        } else {
                            Log.w("idJaulaOnDatabase", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
