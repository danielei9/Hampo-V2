package org.example.androidthingsraspberry;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.example.comun.MQTT.topicRoot;

public class MainActivity extends Activity implements MqttCallback {

    // Wifi params
    String networkSSID = "buchuwifi";
    String networkPass = "Paulaaa123";
    /*
    String networkSSID = "";
    String networkPass = "";*/
    // UART Device Name
    private static final String UART_DEVICE_NAME = "UART0";
    public UartDevice mDevice;
    public static MqttClient client = null;
    //ArduinoUART UART;
    String s;
    TextView textViewUart;
    int dataSize = 8;
    String dataRAW = "";
    boolean fullDataReaded;
    private FirebaseDataController firebaseDb;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Camara mCamera;
    private StorageReference storageRef;

    //Crear en POJO llamado Jaula
    public static String idJaula = "nfcRawTag";
    public static String idJaulaOnDatabase = null;
    //Aqui podemos añadir varios usuarios
    public static String idUser;
    private boolean jaulaSyncStatus = false;

    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    /// ==================================== ACTIVITY ==============================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageRef = FirebaseStorage.getInstance().getReference();
        // =============================== WIFI ====================================================
        if (checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("WIFI", "No permission // CHANGE_WIFI_STATE");
            return;
            // Creates new handlers and associated threads for camera and networking operations.
        }
        if (!checkWifiOnAndConnected()) {

            Log.e("WIFI", "Setting wifi SSID:" + networkSSID + " PASS: " + networkPass);

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes

            // CUANDO RECIBE EL MQTT CON LOS DATOS TAMBIEN LE TIENE QUE PASAR QUE TIPO DE PASS UTILIZA
            // EN CADA CASO SE ELIGIRÁ UNA DE ESTAS OPCIONES

            // WEP
            // WPA
            Log.e("WIFI", "Setting wifi Password as WPA");
            conf.preSharedKey = "\"" + networkPass + "\"";
            // FREE
            //Log.e("WIFI","Setting wifi Password as OPEN");
            //conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            // ADD TO WIFI MANAGER
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
            wifiManager.addNetwork(conf);
            Log.e("WIFI", "WIFI Added to wifiManager");

            //ENABLE WIFI
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    break;
                }
            }
        }

        /// ==================================== CAMARA==========================================
        // We need permission to access the camera
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // A problem occurred auto-granting the permission
            Log.e("CAMARA", "No permission");
            return;
            // Creates new handlers and associated threads for camera and networking operations.
        }

        mCameraThread = new HandlerThread("CameraBackground");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
        // Camera code is complicated, so we've shoved it all in this closet class for you.
        mCamera = Camara.getInstance();
        mCamera.initializeCamera(this, mCameraHandler, mOnImageAvailableListener);
        /// ====================================
        /// ====================================
        //firebaseDb = new FirebaseDataController();


        firebaseDb = new FirebaseDataController();
        //UART = new ArduinoUART("UART0", 9600);
        Log.e("TEST", "ANTES DE crearConexionMQTT()");
        crearConexionMQTT();
        //escucharDeTopicMQTT("luzhampo/on");
        escucharDeTopicMQTT("luzhampo/off");
        escucharDeTopicMQTT("luzhampo/top/on");
        escucharDeTopicMQTT("luzhampo/down/on");
        escucharDeTopicMQTT("luzhampo/both/on");
        escucharDeTopicMQTT("luzhampo/both/off");
        escucharDeTopicMQTT("luzhampo/alimentar");
        escucharDeTopicMQTT("luzhampo/uv/on");
        escucharDeTopicMQTT("luzhampo/uv/off");
        escucharDeTopicMQTT("luzhampo/adiestramiento");
        escucharDeTopicMQTT("luzhampo/jaulaSync/" + idJaula);
        escucharDeTopicMQTT("camarahampo/mkfoto");
        escucharDeTopicMQTT("luzhampo/idJaulaOnDatabaseUpdate/" + idJaula);
        escucharDeTopicMQTT("luzhampo/wifiUpdate/" + idJaula);

        //UART = new ArduinoUART("UART0", 9600);
        textViewUart = findViewById(R.id.textView1);
        try {
            //mDevice = PeripheralManager.getInstance().openUartDevice(UART_DEVICE_NAME);
            /*mDevice = PeripheralManager.getInstance().openUartDevice(UART_DEVICE_NAME);
            configureUartFrame(mDevice);
            uartCallback.onUartDeviceDataAvailable(mDevice);
            writeUartData(mDevice);*/
            //Log.e("UART", "DataRAW = " + dataRAW);
            PeripheralManager manager = PeripheralManager.getInstance();
            mDevice = manager.openUartDevice(UART_DEVICE_NAME);
            configureUartFrame(mDevice);
        } catch (IOException e) {
            Log.w("UART", "Error en openUartDevice: ", e);
        }
        if (checkWifiOnAndConnected()) {
            Log.e("WIFI", "WIFI CONNECTED");
        }

    }//==============================================================================================

    private boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) getSystemService(getApplicationContext().WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON
            Log.e("WIFI", "Wifi Adapter ON");
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if (wifiInfo.getNetworkId() == -1) {
                Log.e("WIFI", "Wifi Not Connected ");
                return false; // Not connected
            }
            Log.e("WIFI", "Wifi  Connected ");
            return true; // Connected
        } else {
            Log.e("WIFI", "Wifi Adapter OFF ");
            return false; // Wi-Fi OFF
        }
    }

    //==============================================================================================
    public void writeUartData(UartDevice uart, String str) throws IOException {
        byte[] buffer = str.getBytes();
        int count = uart.write(buffer, buffer.length);
        Log.d("UART", "Wrote " + count + " bits to peripheral");
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            // Begin listening for interrupt events
            mDevice.registerUartDeviceCallback(uartCallback);

            //writeUartData(mDevice, "kk");
        } catch (IOException e) {
            Log.w("UART", "Unable to access UART device, error: ", e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Interrupt events no longer necessary
        mDevice.unregisterUartDeviceCallback(uartCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Make sure to release the camera resources.
        mCamera.shutDown();

        mCameraThread.quitSafely();
        if (mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                Log.w("UART", "Unable to close UART device", e);
            }
        }
    }

    /// =========================================MQTT==========================================================
    //Crear conexion con el broker MQTT
    public void crearConexionMQTT() {
        try {
            Log.i(MQTT.TAG, "Conectando al broker " + MQTT.broker);
            client = new MqttClient(MQTT.broker, "7865",
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
        Log.e("mqttDebug", "message arrived");
        try {
            String payload = new String(message.getPayload());
            Log.d(MQTT.TAG, "Recibiendo: " + topic + "->" + payload);
            if (topic.equalsIgnoreCase("luzhampo/top/on")) {
                Log.d(MQTT.TAG, "dentro top/on");
                //UART.escribir("tt");
                writeUartData(mDevice, "tt");
            } else if (topic.equalsIgnoreCase("luzhampo/down/on")) {
                Log.d(MQTT.TAG, "dentro down/on");
                //UART.escribir("ss");
                writeUartData(mDevice, "ss");
            } else if (topic.equalsIgnoreCase("luzhampo/alimentar")) {
                Log.d(MQTT.TAG, "dentro alimentar");
                //UART.escribir("hh");
                writeUartData(mDevice, "hh");
            } else if (topic.equalsIgnoreCase("luzhampo/adiestramiento")) {
                Log.d(MQTT.TAG, "dentro adiestramiento");
                //UART.escribir("aa");
                writeUartData(mDevice, "aa");
            } else if (topic.equalsIgnoreCase("luzhampo/both/on")) {
                Log.d(MQTT.TAG, "dentro both/on");
                //UART.escribir("pp");
                writeUartData(mDevice, "pp");
            } else if (topic.equalsIgnoreCase("luzhampo/both/off")) {
                Log.d(MQTT.TAG, "dentro both/off");
                //UART.escribir("qq");
                writeUartData(mDevice, "qq");
            } else if (topic.equalsIgnoreCase("luzhampo/uv/on")) {
                Log.d(MQTT.TAG, "dentro uv/on");
                //UART.escribir("gg");
                writeUartData(mDevice, "gg");
            } else if (topic.equalsIgnoreCase("luzhampo/uv/off")) {
                Log.d(MQTT.TAG, "dentro uv/off");
                //UART.escribir("ff");
                writeUartData(mDevice, "ff");
            } else if (topic.equalsIgnoreCase("luzhampo/sendnudes")) {
                Log.d(MQTT.TAG, "nudes");
                //UART.escribir("kk");
                writeUartData(mDevice, "kk");
            } else if (topic.equalsIgnoreCase("luzhampo/jaulaSync/" + idJaula)) {
                String[] msgData = msgDataToArray(payload);
                idUser = msgData[0];
                // Obtengo el id autogenerado de la jaula
                //getIdJaulaDatabase();
                jaulaSyncStatus = true;// Mando las credenciales de la red, esto inicializa la comunicación con el arduino
                //writeUartData(mDevice, "+" + networkSSID + '*' + networkPass + "$");
                //Log.d("wifiUpdate", "Wifi params -> SSID = " + networkSSID + " PASSWORD = " + networkPass);
                Log.d("SyncStatus", "Sync status = TRUE -> idUser = " + idUser + " idJaula = " + idJaula);
                // Aqui tengo que enviar el mensaje para confirmar la sync
                enviarMensajeMQTT("Sync done", "jaulaSyncDone/" + idJaula);
                // writeUartData(mDevice, "kk");
            } else if (topic.equalsIgnoreCase("camarahampo/mkfoto")) {
                // Doorbell rang!
                Log.d("CAMARA", "MQTT HA LLEGADO...Creando foto");
                // Si lanzo el takePicture de forma síncrona -> Error: no handler given, and current thread has no looper! mqtt
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCamera.takePicture();
                    }
                });
            } else if (topic.equalsIgnoreCase("luzhampo/idJaulaOnDatabaseUpdate/" + idJaula)) {
                // Obtengo el id autogenerado de la jaula que viene en el data (data=payload)
                idJaulaOnDatabase = payload;
                Log.d("idJaulaOnDatabase", "idJaulaOnDatabase = " + idJaulaOnDatabase);
            } else if (topic.equalsIgnoreCase("luzhampo/wifiUpdate/" + idJaula)) {
                String[] msgData = msgDataToArray(payload);
                networkSSID = msgData[0];
                networkPass = msgData[1];
                enviarMensajeMQTT("wifiSyncDone/" + idJaula, "jaulaWifiSyncDone/" + idJaula);
                // Mando las credenciales de la red, esto inicializa la comunicación con el arduino
                writeUartData(mDevice, "+" + networkSSID + '*' + networkPass + "$");
                Log.d("wifiUpdate", "Wifi params -> SSID = " + networkSSID + " PASSWORD = " + networkPass);
                //setNewWifiConnection();
                // Updateamos los parametros de conexion al wifi y reinicializamos la conexión
            }
            // Uv g on
            // UV f off
            // Alimentar h
            // Obtener valores sensores k
            // Adistramiento a
            // Ahorro energia m on
            // Ahorro energia n off
            //s = UART.leer();
            //Log.d(MQTT.TAG, "Recibido en uart: "+s);

            //textViewUart.setText(s);
        } catch (Exception e) {
            Log.d(MQTT.TAG, "Error en message arrived callback: " + e.getMessage());
        }
    }

    // Returns { "idUser", "nfcRawTag" }
    private String[] msgDataToArray(String msgData) {
        String[] parts = msgData.split("&");
        return parts;
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(MQTT.TAG, "Entrega completa");
    }

    /// ============================ UART =========================================================
    public void configureUartFrame(UartDevice uart) throws IOException {
        // Configure the UART port
        uart.setBaudrate(9600);
        uart.setDataSize(dataSize);
        uart.setParity(UartDevice.PARITY_NONE);
        uart.setStopBits(1);
    }

    /*  public void writeUartData(UartDevice uart) throws IOException {
          String str = "k";
          byte[] buffer = str.getBytes();
          int count = uart.write(buffer, buffer.length);
          Log.d("UART", "Wrote " + count + " bytes to peripheral");
      }*/
    private UartDeviceCallback uartCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            // Read available data from the UART device
            Log.e("UART", "uart data avaiable broooo");
            try {
                readUartBuffer(uart);
            } catch (IOException e) {
                Log.w("UART", "Unable to access UART device", e);
            }
            /* Despues de entrar all callback lee los datos y
             *  en este scope tenemos el string que recibimos por la uart
             * */
            if (fullDataReaded) {
                Log.e("UART", "DataRAW = " + dataRAW);
                Log.e("UART", "DataFormatted =" + formatToJSON(dataRAW));
                convertFromJSONToPOJO(formatToJSON(dataRAW));
            }
            // Continue listening for more interrupts
            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w("UART", uart + ": Error event " + error);
        }

    };

    // Limpia la string de datos para que no coja caracteres extraños como **********
    public String formatToJSON(String dataToFormat) {
        Log.w("UART", "formatting to JSON string");
        String res = "";
        String itr;
        for (int i = 0; i < dataToFormat.length(); i++) {
            itr = Character.toString(dataToFormat.charAt(i));
            if (itr.matches("[a-zA-Z{}\":.,0-9]*")) {
                res += dataToFormat.charAt(i);
            }
        }
        return res;
    }

    public boolean checkFinalBit(String str) {
        String itr;
        int contador = 0;
        for (int i = 0; i < str.length(); i++) {
            itr = Character.toString(str.charAt(i));
            if (itr.matches("#")) {
                contador++;
            }
        }
        if (contador == 2) {
            return true;
        } else {
            return false;
        }
    }

    public void readUartBuffer(UartDevice uart) throws IOException {
        // Maximum amount of data to read at one time
        final int maxCount = dataSize;
        byte[] buffer = new byte[maxCount];

        int count;
        while ((count = uart.read(buffer, buffer.length)) > 0) {
            Log.e("UART", "Read " + count + " bytes from peripheral");
            String str = new String(buffer, StandardCharsets.UTF_8);
            if (checkFinalBit(str)) {
                dataRAW += formatToJSON(str);
                fullDataReaded = true;
            } else {
                dataRAW += str;
                fullDataReaded = false;
            }
            Log.e("UART", "String converted from buffer " + str + " buffer length = " + buffer.length);
        }
        //Log.e("UART", "DataRAW = " + dataRAW);
        //int str = uart.read(buffer,maxCount);
    }

    //// DESCOMENTAR LO DE FIREBASE
    // Convierto el JSON en un POJO para luego enviarlo -> bdd
    public void convertFromJSONToPOJO(String strToConvert) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SensorsData datos = objectMapper.readValue(strToConvert, SensorsData.class);
            Log.e("UART", "DATOS OBJETO = " + datos.toString());
            Log.d("checkSendSensorsData", "jaulaSyncStatus = " + jaulaSyncStatus + "idJaulaOnDatabase = " + idJaulaOnDatabase);
            if ((jaulaSyncStatus) && (idJaulaOnDatabase != null)) {
                firebaseDb.sendSensorsData(datos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /// ================================ CAMARA =====================================================
    /**
     * A {@link Handler} for running Camera tasks in the background.
     */
    private Handler mCameraHandler;

    /**
     * An additional thread for running Camera tasks that shouldn't block the UI.
     */
    private HandlerThread mCameraThread;

    void camaraInit() {


    }

    /**
     * Listener for new camera images.
     */
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Log.e("CAMARA", " IMAGE AVAILABLE");

                    Image image = reader.acquireLatestImage();
                    // get image bytes
                    ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
                    final byte[] imageBytes = new byte[imageBuf.remaining()];
                    imageBuf.get(imageBytes);
                    image.close();

                    onPictureTaken(imageBytes);
                }
            };

    /**
     * Upload image data to Firebase as a doorbell event.
     */
    private void registrarImagen(String subida_por_movil, String toString) {
        Log.e("CAMARA", "REGISTRANDO IMAGEN");

        Imagen imagen = new Imagen(subida_por_movil, toString);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //CollectionReference collection = db.collection("imagenes"); //FIREBASE
        CollectionReference collection = db.collection(idUser).document(idJaulaOnDatabase).collection("imagenes");
        collection.document().set(imagen);
    }

    private void onPictureTaken(final byte[] imageBytes) {
        Log.e("CAMARA", "onPictureTaken");

        if (imageBytes != null) {
            Log.e("CAMARA", "Image Byte Taked");

            String nombreFichero = UUID.randomUUID().toString();
            final StorageReference ref = storageRef.child("Gallery/" + nombreFichero);
            UploadTask uploadTask = ref.putBytes(imageBytes);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                        throw task.getException();
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e("CAMARA", "URL: " + downloadUri.toString());
                        registrarImagen("Subida por Móvil", downloadUri.toString());
                    } else {
                        Log.e("CAMARA", "ERROR: subiendo fichero");
                    }
                }
            });


            /*
            final DatabaseReference log = mDatabase.getReference("logs").push();
            final StorageReference imageRef = mStorage.getReference().child(log.getKey());

            // upload image to storage
            UploadTask task = imageRef.putBytes(imageBytes);
            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Uri downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    // mark image in the database
                    Log.i("CAMARA", "Image upload successful");
                    log.child("timestamp").setValue(ServerValue.TIMESTAMP);
                    //log.child("image").setValue(downloadUrl.toString());
                    // process image annotations
                    //annotateImage(log, imageBytes);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // clean up this entry
                    Log.w("CAMARA", "Unable to upload image to Firebase");
                    log.removeValue();
                }
            });
        }*/
        }
    }

    public void getIdJaulaDatabase() {
        Log.d("idJaulaOnDatabase", "Dentro de getIdJaulaDatabase");
        db.collection(idUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("idJaulaOnDatabase", document.getId() + " => " + document.getData());
                                idJaulaOnDatabase = document.getId();
                            }
                            // Enviar sync de vuelta a la app
                            enviarMensajeMQTT("Sync done", "jaulaSyncDone/" + idJaula);
                        } else {
                            Log.w("idJaulaOnDatabase", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void setNewWifiConnection() {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes

        // CUANDO RECIBE EL MQTT CON LOS DATOS TAMBIEN LE TIENE QUE PASAR QUE TIPO DE PASS UTILIZA
        // EN CADA CASO SE ELIGIRÁ UNA DE ESTAS OPCIONES
        // WEP
        // WPA
        Log.e("WIFI", "Setting wifi Password as WPA");
        conf.preSharedKey = "\"" + networkPass + "\"";
        // FREE
        // ADD TO WIFI MANAGER
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        Log.e("WIFI", "WIFI Added to wifiManager");

        //ENABLE WIFI
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }
    /// =====================================================================================

}