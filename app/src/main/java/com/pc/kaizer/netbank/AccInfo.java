package com.pc.kaizer.netbank;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class AccInfo extends Fragment {

    private DatabaseReference mDatabase;
    private TextView name;
    private TextView balance;
    private TextView accno;
    private TextView lastlogin;
    SharedPreferences settings;
    Map<String,String> info =new HashMap<String,String>();

    public AccInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        settings = getActivity().getSharedPreferences("ACCDETAILS", 0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
        String format = simpleDateFormat.format(new Date());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("logintime",format);
        editor.apply();
        getActivity().setTitle("Account Info");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_acc_info, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populate_view();
    }

    public void populate_view()
    {
        name = (TextView) getActivity().findViewById(R.id.accinfo_name);
        balance = (TextView) getActivity().findViewById(R.id.accinfo_Balance);
        accno = (TextView) getActivity().findViewById(R.id.accinfo_accno);
        lastlogin = (TextView) getActivity().findViewById(R.id.accinfo_label4);
        String uid = settings.getString("uid","");
        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        info.put(dsp.getKey(),dsp.getValue().toString());
                    }
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("acc_no",info.get("account_no"));
                    editor.putString("mobile",info.get("mobile"));
                    editor.putString("email",info.get("email"));
                    editor.putString("address",info.get("address"));
                    editor.apply();
                    accno.setText("Acc no: "+info.get("account_no"));
                    name.setText("Name: "+info.get("name"));
                    lastlogin.setText(info.get("last_login"));
                    mDatabase.child("accounts").child(info.get("account_no")).child("balance").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null)
                                balance.setText("Balance :" + dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(getActivity(),"Something Went Wrong",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


}

