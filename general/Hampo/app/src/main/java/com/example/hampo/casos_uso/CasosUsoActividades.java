package com.example.hampo.casos_uso;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.core.app.ActivityCompat;


public class CasosUsoActividades extends ActivityCompat{

    private Activity actividad;

    public CasosUsoActividades(Activity actividad){
        this.actividad = actividad;
    }

    //String de ejemplo -> Manifest.permission.READ_EXTERNAL_STORAGEz
    public static boolean checkPermission(Activity actividad, String androidManifPermission){
        if (ActivityCompat.checkSelfPermission(actividad, androidManifPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static void askUserPermission(Activity actividad, String androidManifPermission, int requestCode){
        requestPermissions(actividad, new String[] { androidManifPermission }, requestCode);
    }
}