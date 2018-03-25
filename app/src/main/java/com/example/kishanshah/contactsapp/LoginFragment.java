package com.example.kishanshah.contactsapp;


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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private OnUserLoggedInListner mListner;
    public static final String TAG = "LoginFragment";
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        etEmail=view.findViewById(R.id.email);
        etPassword=view.findViewById(R.id.password);
        btnLogin=view.findViewById(R.id.email_sign_in_button);
        mAuth=FirebaseAuth.getInstance();
        mListner= (OnUserLoggedInListner) getActivity();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String email=etEmail.getText().toString();
                String password=etPassword.getText().toString();

                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "Success");
                                    mListner.onUserLoggedIn(mAuth.getCurrentUser());
                                } else {
                                    Log.d(TAG, "Failure");
                                    Snackbar.make(view,"Invalid Email or Password!",Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

        return view;
    }

    public interface OnUserLoggedInListner{
        void onUserLoggedIn(FirebaseUser user);
    }

}
