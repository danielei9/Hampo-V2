package com.example.hampo.presentacion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hampo.Aplicacion;
import com.example.hampo.R;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    FirebaseUser usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        login();
    }

    private void login() {
       usuario = FirebaseAuth.getInstance().getCurrentUser();

        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.login)
                .setGoogleButtonId(R.id.firebase_ui)
                .build();

        if (usuario != null) {

            Toast.makeText(this, "inicia sesión: " + usuario.getDisplayName() + " - " + usuario.getEmail(), Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, CustomLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ((Aplicacion)getApplication()).id = usuario.getUid();
            startActivity(i);

        } else {
            List<AuthUI.IdpConfig> providers = Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build());
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).setAuthMethodPickerLayout(customLayout).setTheme(R.style.AppThemeFirebaseAuth).setIsSmartLockEnabled(true).build(), RC_SIGN_IN);

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                login();
                //((Aplicacion)getApplication()).id = usuario.getUid();
                ((Aplicacion)getApplication()).refrescar();
                finish();
            } else {
                String s;
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) {
                    s = "Cancelado";
                    finish();
                } else switch (response.getError().getErrorCode()) {
                    case ErrorCodes.NO_NETWORK:
                        s = "Sin conexión a Internet";
                        break;
                    case ErrorCodes.PROVIDER_ERROR:
                        s = "Error en proveedor";
                        break;
                    case ErrorCodes.DEVELOPER_ERROR:
                        s = "Error desarrollador";
                        break;
                    default:
                        s = "Otros errores de autentificación";
                }
                Toast.makeText(this, s, Toast.LENGTH_LONG).show();
            }
        }
    }


}