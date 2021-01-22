package com.example.hampo.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.hampo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class MiPerfilFragment extends Fragment {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private String nombreUsuario;
    private String nombreUsuarioForo;
    private String correoUsuario;
    private String bioUsuario;
    private Uri imagenUsuario;
    private TextView viewNombreUsuario;
    private TextView viewCorreoUsuario;
    private TextView viewBioUsuario;
    private ImageView viewImageUsuario;
    private Button guardarCambios;


    public MiPerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //llamar actividad

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View custom = inflater.inflate(R.layout.fragment_mi_perfil, container, false);

        viewNombreUsuario = custom.findViewById(R.id.nombrePerfil);

        viewNombreUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(custom.getContext());

                alert.setTitle("Cambiar nombre usuario");
                final EditText input = new EditText(custom.getContext());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        nombreUsuario = input.getText().toString();
                        viewNombreUsuario.setText(nombreUsuario);
                        actualizarUsuario();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });


        nombreUsuario = auth.getCurrentUser().getDisplayName();
        if (nombreUsuario == null|| nombreUsuario.length()<1) {
            viewNombreUsuario.setText("Usuario");
        } else {
            viewNombreUsuario.setText(nombreUsuario);
        }


        viewCorreoUsuario = custom.findViewById(R.id.emailUsuario);
        correoUsuario = auth.getCurrentUser().getEmail();
        viewCorreoUsuario.setText(correoUsuario);

//        viewBioUsuario = custom.findViewById(R.id.bioPerfil);
//        bioUsuario = auth.getCurrentUser().getDisplayName();
//        viewBioUsuario.setText(bioUsuario);

//        viewImageUsuario = custom.findViewById(R.id.imagenPerfil);
//        imagenUsuario = auth.getCurrentUser().getPhotoUrl();
//        viewImageUsuario.setImageURI(imagenUsuario);


        return custom;
    }

    public void actualizarUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombreUsuario)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            @SuppressLint("RestrictedApi") Context context = getApplicationContext();
                            CharSequence text = "Datos guardados";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
    }


}