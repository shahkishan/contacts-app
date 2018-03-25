package com.example.kishanshah.contactsapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/*Load Map into dialog box*/

public class MapDialog extends DialogFragment {

    private Contact contact;
    public MapDialog(){}



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.map_dialog,container,false);

        contact=getArguments().getParcelable("contact");

        SupportMapFragment mfragment=SupportMapFragment.newInstance();

        getChildFragmentManager().beginTransaction().replace(R.id.frame,mfragment).commit();

        mfragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng location = new LatLng(contact.getLatitude(),contact.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(location).title(contact.getName())).showInfoWindow();
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                googleMap.setBuildingsEnabled(true);
                googleMap.setMinZoomPreference(8);
                googleMap.setMaxZoomPreference(15);
            }
        });

        return v;
    }
}
