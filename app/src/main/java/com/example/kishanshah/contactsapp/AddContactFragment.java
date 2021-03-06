package com.example.kishanshah.contactsapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddContactFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private EditText etName, etNumber;
    private Button btnSave;

    //Map Utils
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private Button btnLocation;
    private final int INVALID_LAT_LONG = -999;
    private double latitude = INVALID_LAT_LONG;
    private double longtitude = INVALID_LAT_LONG;
    private GoogleMap gmap;
    private SupportMapFragment mapFragment;

    private Contact contact;

    public AddContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_contact, container, false);

        //Initializing components
        btnSave = view.findViewById(R.id.btnSave);
        etName = view.findViewById(R.id.etName);
        etNumber = view.findViewById(R.id.etNumber);
        btnLocation = view.findViewById(R.id.btnLocation);

        //Setting Listners
        btnSave.setOnClickListener(this);
        btnLocation.setOnClickListener(this);

        //Initializing GoogleApiClient
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();


        //Initialize mapFragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.g_map);

        //Load Map asynchronously
        mapFragment.getMapAsync(this);

        //Check if we are editing existing contact or adding new contact
        if (getArguments() != null) {
            contact = getArguments().getParcelable("contact");
            etName.setText(contact.getName());
            etNumber.setText(contact.getContactno());
            latitude = contact.getLatitude();
            longtitude = contact.getLongitude();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        //If user clicks on save button
        if (v.getId() == btnSave.getId()) {
            //Get text from textboxes
            String nm = etName.getText().toString();
            String contact = etNumber.getText().toString();

            //Get user id
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //Check for values in textboxes
            if (nm.equals("") || nm == null || contact == null || contact.equals("") || longtitude == INVALID_LAT_LONG || latitude == INVALID_LAT_LONG) {
                Snackbar.make(getView(), "PLEASE ENTER ALL DETAILS!", Snackbar.LENGTH_LONG).show();
                return;
            }

            Contact c = new Contact(contact, nm, latitude, longtitude);
            try {
                //If user changed contact number while editing, remove the old contact
                if (c.getContactno() != this.contact.getContactno()) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("contacts").child(this.contact.getContactno());
                    Task task = ref.removeValue();
                    if (task.isSuccessful()) {
                        Log.d("removed", "success");
                    } else {
                        Log.d("removed", "failure");
                    }
                }
            } catch (NullPointerException e) {

            } finally {
                //Save the new Contact
                c.addOrUpdateContact();
                etName.setText("");
                etNumber.setText("");
                etName.requestFocus();
                latitude = INVALID_LAT_LONG;
                longtitude = INVALID_LAT_LONG;
                mapFragment.getMapAsync(this);
                if (getArguments() != null) {
                    Snackbar.make(getView(), "Changes saved!", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getView(), "Contact saved!", Snackbar.LENGTH_LONG).show();
                }
            }


        } else if (v.getId() == btnLocation.getId()) { //If user clicks on get location button
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    //Select Location from map
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity(), data);
                this.latitude = place.getLatLng().latitude;
                this.longtitude = place.getLatLng().longitude;
                mapFragment.getMapAsync(this);
            }
        }
    }


    //Load data into map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        LatLng location = new LatLng(latitude, longtitude);
        gmap.addMarker(new MarkerOptions().position(location).title(etName.getText().toString())).showInfoWindow();
        gmap.moveCamera(CameraUpdateFactory.newLatLng(location));
        gmap.setBuildingsEnabled(true);
        gmap.setMinZoomPreference(8);
        gmap.setMaxZoomPreference(15);
    }
}
