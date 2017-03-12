package com.pc.kaizer.netbank;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordFragment extends Fragment {
    private EditText mOld;
    private EditText mNew;
    private EditText mOTP;
    private Button mReqOTP;
    private FirebaseAuth mFirebaseAuth;
    private AuthCredential credential;
    SharedPreferences settings;
    private Button mChange;
    public PasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Change Password");
        mFirebaseAuth = FirebaseAuth.getInstance();
        settings = getActivity().getSharedPreferences("ACCDETAILS", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_password, container, false);
        mOld = (EditText) v.findViewById(R.id.oldpwd);
        mNew = (EditText) v.findViewById(R.id.newpwd);
        mOTP = (EditText) v.findViewById(R.id.textInputLayout3);
        mReqOTP = (Button) v.findViewById(R.id.reqOTP);
        mChange = (Button) v.findViewById(R.id.changepwd);
        mReqOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random(System.currentTimeMillis());
                otpRequest req = new otpRequest(settings.getString("mobile",""), String.valueOf((1 + r.nextInt(2)) * 10000 + r.nextInt(10000)), getActivity().getApplicationContext());
                req.execute((Void) null);
            }
        });

        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    void validation()
    {
        boolean cancel = false;
        View focusView = null;
        mOld.setError(null);
        mNew.setError(null);
        mOTP.setError(null);
        final String oldpass = mOld.getText().toString();
        final String newpass = mNew.getText().toString();
        final String otp = mOTP.getText().toString();
        if(empty_chk(oldpass))
        {
            cancel=true;
            focusView = mOld;
            mOld.setError("Fields should not be left empty");
        }
        if(empty_chk(newpass))
        {
            cancel=true;
            focusView = mNew;
            mNew.setError("Fields should not be left empty");
        }
        if(empty_chk(otp))
        {
            cancel=true;
            focusView = mOTP;
            mOTP.setError("Request OTP first!!");
        }
        if(oldpass.equals(newpass))
        {
            cancel=true;
            focusView = mNew;
            mNew.setError("Both the passwords match");
        }
        if(oldpass.length()<7 && newpass.length()<7)
        {
            cancel=true;
            focusView = mNew;
            mNew.setError("New password should be more than 6 characters");
        }
        if(cancel)
        {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }
        else
        {
            final FirebaseUser user = mFirebaseAuth.getInstance().getCurrentUser();
            credential = EmailAuthProvider.getCredential(settings.getString("email",""),oldpass);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful())
                 {
                     user.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful())
                             {
                                 Toast.makeText(getActivity(),"Password updated succesfully. Please Login Again",Toast.LENGTH_LONG).show();
                                 DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
                                 DB.child("users").child(settings.getString("uid","")).child("last_login").setValue(settings.getString("logintime",""));
                                 Intent intent = new Intent(getActivity(), LoginActivity.class);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                 FirebaseAuth.getInstance().signOut();
                                 startActivity(intent);
                             }
                             else
                             {
                                 Toast.makeText(getActivity(),"Password update failed",Toast.LENGTH_LONG).show();
                             }
                         }
                     });

                 }
                 else
                 {
                     Toast.makeText(getActivity(),"Old Password does not match",Toast.LENGTH_LONG).show();
                 }
                }
            });
        }
    }

    private boolean empty_chk(String pass)
    {
        return TextUtils.isEmpty(pass);
    }
}
