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
import android.widget.Button;
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

import com.bumptech.glide.Glide;
import com.example.hampo.Aplicacion;
import com.example.hampo.R;
import com.example.hampo.datos.EscuchadorHampo;
import com.example.hampo.datos.HamposFirestore;
import com.example.hampo.modelo.Hampo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class EditHampoActivity extends AppCompatActivity {

    private String idJaula;
    private HamposFirestore hampoDb;
    private EditText nombreEditText;
    private String sexOptionSelected;
    private ConstraintLayout layout;
    private Uri downloadUri;
    private ImageView imagenHampo;
    private Uri uriUltimaFoto;
    private int spinnerSelectedItem = -1;
    private StorageReference mStorageRef;
    private Spinner spinnerRaza;
    private Button buttonEditHampo;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_hampo);
        id = ((Aplicacion)getApplication()).id;
        hampoDb= new HamposFirestore(id);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        buttonEditHampo = findViewById(R.id.acceptBtn);
        buttonEditHampo.setText(R.string.edit_btn_hampo);
        nombreEditText = findViewById(R.id.editTextNombre);
        layout = findViewById(R.id.myConstraintLayout);
        imagenHampo = findViewById(R.id.imagenHampo);
        findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
        spinnerRaza = findViewById(R.id.spinnerRaza);
        idJaula = getIntent().getStringExtra("idJaula");
        Log.d("idJaula", "idJaula: " + idJaula);
        hampoDb.elemento(idJaula, new EscuchadorHampo(){
            @Override
            // Callback encargado de cargar los datos recibidos de la bdd en el layout
            public void onRespuesta(Hampo hampo) {
                Log.d("idJaula", "hampo uri: " + hampo.getUriFoto());
                // Cargo la imagen con la dependencia Glide
                Glide.with(EditHampoActivity.this)
                        .load(hampo.getUriFoto())
                        .into(imagenHampo);
                uriUltimaFoto = Uri.parse(hampo.getUriFoto());
                // Cargo el nombre del hampo
                nombreEditText.setText(hampo.getNombre());

                // Cargo la raza
                spinnerRaza.setSelection(Integer.parseInt(hampo.getRaza()));
                spinnerSelectedItem = Integer.parseInt(hampo.getRaza());

                // Cargo el sexo
                if (hampo.getSex().equals("male")){
                    maleOptionSelected();
                } else if (hampo.getSex().equals("female")){
                    femaleOptionSelected();
                } else {
                    // control de errores - validacion
                }
            }
        });
        getRazas();
        // Acciones
        // Cuando clica sobre el frameLayout para añadir una imagen
        findViewById(R.id.frameLayout1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.VISIBLE);
                layout.setBackgroundColor(Color.parseColor("#00000000"));
            }
        });

        // Cuando clica sobre el layout con el desplegable visible
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setBackgroundColor(Color.parseColor("#00000000"));
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
            }
        });
        // Cuando clica sobre la imagen de galeria
        findViewById(R.id.cardGaleria).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setBackgroundColor(Color.parseColor("#00000000"));
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
                ponerDeGaleria(1);
            }
        });

        // Cuando clica sobre la imagen de la camara
        findViewById(R.id.cardCamara).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setBackgroundColor(Color.parseColor("#00000000"));
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
                uriUltimaFoto = manejadorFotoHampo();
            }
        });

        // Cuando clica sobre el boton crear hampo
        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText nombreEditText = findViewById(R.id.editTextNombre);
                Log.d("uriUltimaFoto",uriUltimaFoto.toString());
                subirImagenDeHampo(uriUltimaFoto);
                //Hampo hampoToAdd = new Hampo(nombreEditText.getText().toString(), downloadUri.toString(), Aplicacion.getId(), String.valueOf(spinnerSelectedItem));
                //db.anade(hampoToAdd);
                //finish();
            }
        });

        // Cuando clica sobre un boton de sexo
        findViewById(R.id.femaleCard).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                femaleOptionSelected();
            }
        });
        findViewById(R.id.maleCard).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                maleOptionSelected();
            }
        });
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

    public void femaleOptionSelected() {
        layout.setBackgroundColor(Color.parseColor("#00000000"));
        findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
        CardView maleCard = findViewById(R.id.maleCard);
        maleCard.setCardBackgroundColor(12);
        CardView femaleCard = findViewById(R.id.femaleCard);
        femaleCard.setCardBackgroundColor(getResources().getColor(R.color.femaleColor));
        sexOptionSelected = "female";
    }

    public void maleOptionSelected() {
        layout.setBackgroundColor(Color.parseColor("#00000000"));
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
        hampoDb.db.collection("raza").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    hampoDb.actualiza(idJaula,hampoToAdd);
                    finish();
                } else {
                    // Handle failures
                    // ...
                    Toast toast = Toast.makeText(getApplicationContext(),"Error subiendo la foto vuelva a cargarla", Toast.LENGTH_LONG);
                    toast.show();
                    Log.d("Subirfoto", "Ha fallado la subida de foto");
                }
            }
        });
    }
}
