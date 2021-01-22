package com.example.hampo.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hampo.Aplicacion;
import com.example.hampo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    public MapsActivity() {
        // Requiere un constructor vacío
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.activity_maps, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        /*SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);*/
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return vista;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (((Aplicacion) getActivity().getApplication()).localizacionDeLaJaula != null) {
            mMap = googleMap;
            mMap.setMinZoomPreference(17.0f);

            Location localizacionJaula = ((Aplicacion) getActivity().getApplication()).localizacionDeLaJaula;
            LatLng latLngJaula = new LatLng(localizacionJaula.getLatitude(), localizacionJaula.getLongitude());

            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(-34, 151);

            mMap.addMarker(new MarkerOptions().position(latLngJaula).title("Mi jaula"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngJaula));
        }
    }
}