package com.example.hampo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.hampo.casos_uso.CasosUsoActividades;
import com.example.hampo.presentacion.PreferenciasActivity;
import com.example.hampo.R;


public class nav_config extends Fragment {

    private CasosUsoActividades usoActividades;

    public nav_config() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(getContext(), PreferenciasActivity.class));

    }



}