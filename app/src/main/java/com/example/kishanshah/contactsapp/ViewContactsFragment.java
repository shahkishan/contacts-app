package com.example.kishanshah.contactsapp;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dexafree.materialList.card.Action;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.view.MaterialListView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.RequestCreator;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


/**
 * A simple Fragment to view contacts
 */
public class ViewContactsFragment extends Fragment {

    private MaterialListView mlv;
    private ArrayList<Contact> contacts;
    private DatabaseReference ref;
    private ValueEventListener myListner;
    private OnContactActionLister mListner;

    public ViewContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_view_contacts, container, false);
        mlv = view.findViewById(R.id.materialListView);


        mListner = (MainActivity) getActivity();

        //Listner to load contacts from firebase database
        myListner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contacts = new ArrayList<>();
                mlv.getAdapter().clearAll();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    contacts.add(ds.getValue(Contact.class));
                }
                if (contacts.size() == 0) {
                    Snackbar.make(view, "NO CONTACTS FOUND!", Snackbar.LENGTH_LONG).show();
                } else {
                    Collections.sort(contacts);
                    for (final Contact contact : contacts) {
                        //3rd party library to use cards
                        Card card = new Card.Builder(getContext())
                                .setTag("BASIC_IMAGE_CARDS_BUTTON")
                                .withProvider(new CardProvider<>())
                                .setTitle(contact.getName())
                                .setTitleColor(Color.WHITE)
                                .setDescription(contact.getContactno())
                                .setLayout(R.layout.contacts_card_layout)
                                .setDrawable(new ColorDrawable(contact.getColor()))
                                .addAction(R.id.center_text_button, new Action(getActivity()) { //Edit Contact action
                                    @Override
                                    protected void onRender(@NonNull View view, @NonNull Card card) {
                                        TextView tv = (TextView) view;
                                        tv.setText("Edit");
                                        view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mListner.onContactEdit(contact);
                                            }
                                        });
                                    }
                                })
                                .addAction(R.id.right_text_button, new Action(getActivity()) { //Delete contact action
                                    @Override
                                    protected void onRender(@NonNull final View view, @NonNull Card card) {
                                        TextView tv = (TextView) view;
                                        tv.setText("Delete");
                                        view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                                                dialog.setTitle("Confirm Delete!");
                                                dialog.setMessage("You can not revert the changes. Press Yes to delete.");

                                                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contacts").child(contact.getContactno());
                                                        ref.removeValue();
                                                        Snackbar.make(view, "Contact Deleted!", Snackbar.LENGTH_LONG).show();
                                                    }
                                                });
                                                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });

                                                dialog.show();


                                            }
                                        });
                                    }
                                })
                                .addAction(R.id.left_text_button, new Action(getActivity()) { //View Location Action
                                    @Override
                                    protected void onRender(@NonNull View view, @NonNull Card card) {
                                        TextView tv = (TextView) view;
                                        tv.setText("View Map");
                                        view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DialogFragment dialogFragment = new MapDialog();

                                                Bundle bundle = new Bundle();
                                                Log.d("onCreateView: ", contact.getLatitude() + " " + contact.getLongitude());
                                                bundle.putParcelable("contact", contact);
                                                dialogFragment.setArguments(bundle);
                                                dialogFragment.setCancelable(true);
                                                dialogFragment.show(getChildFragmentManager(), "MapFragment");
                                            }
                                        });
                                    }
                                })
                                .endConfig()
                                .build();
                        mlv.getAdapter().add(card);
                    }

                }

//                recyclerView.setAdapter(new ContactsAdapter(contacts));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        contacts = new ArrayList<>();

        //Get Firebase Database Instance
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        ref = db.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contacts");
        ref.addValueEventListener(myListner);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        ref.removeEventListener(myListner);
    }


    public interface OnContactActionLister {
        public void onContactEdit(Contact contact);
    }

}

