package com.example.kishanshah.contactsapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private Button btnSignup;
    private EditText etEmail,etName,etPassword;
    private FirebaseAuth mAuth;
    private static final String TAG="Signup Fragment";
    private OnUserCreatedListener mListener;
    private ProgressBar pb;
    public SignupFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.fragment_signup, container, false);

        btnSignup=view.findViewById(R.id.email_sign_up_button);
        etEmail=view.findViewById(R.id.email);
        etPassword=view.findViewById(R.id.password);
        etName=view.findViewById(R.id.name);
        pb=view.findViewById(R.id.pb);
        progressBarVisibility(false);
        mAuth=FirebaseAuth.getInstance();
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarVisibility(true);
                final String name=etName.getText().toString();
                final String email=etEmail.getText().toString();
                String password=etPassword.getText().toString();

                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    final FirebaseUser currentUser=mAuth.getCurrentUser();


                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();
                                    currentUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                mListener.onUserCreated(currentUser);
                                            } else {
                                                Snackbar.make(view,"Error Creating User!",Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                    Log.d(TAG, "createUserWithEmail: Success ");


                                } else {
                                    Log.d(TAG, "createUserWithEmail: Failure");
                                    Snackbar.make(view,"Error Creating User!",Snackbar.LENGTH_LONG).show();

                                }
                            }
                        });
                progressBarVisibility(false);
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserCreatedListener) {
            mListener = (OnUserCreatedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnUserCreatedListener {
        // TODO: Update argument type and name
        void onUserCreated(FirebaseUser user);
    }

    public void progressBarVisibility(boolean flag){

        if(flag)
            pb.setVisibility(View.VISIBLE);
        else
            pb.setVisibility(View.INVISIBLE);

    }
}
