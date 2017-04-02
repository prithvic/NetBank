package com.pc.kaizer.netbank;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class DDFragment extends Fragment {

    private EditText mPayee;
    private EditText mDD;
    private TextView mAdd;
    private Button mReq;
    private DatabaseReference db;
    SharedPreferences settings;
    Random r = new Random(System.currentTimeMillis());
    String reqno = String.valueOf((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    String tid = String.valueOf((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));

    public DDFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Issue Demand Draft");
        settings = getActivity().getSharedPreferences("ACCDETAILS", 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dd, container, false);
        mPayee = (EditText) v.findViewById(R.id.payeename);
        mDD = (EditText) v.findViewById(R.id.ddamt);
        mAdd = (TextView) v.findViewById(R.id.addrcontent);
        mAdd.setText(settings.getString("address",""));
        mReq = (Button) v.findViewById(R.id.requestdd);
        mReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });
        return v;
    }

    private void request()
    {
        boolean cancel = false;
        View focusView = null;
        mPayee.setError(null);
        mDD.setError(null);
        final String name = mPayee.getText().toString();
        final String amt = mPayee.getText().toString();
        if(TextUtils.isEmpty(name))
        {
            cancel=true;
            mPayee.setError("Input Field should not be empty");
            focusView = mPayee;
        }
        if(TextUtils.isEmpty(amt))
        {
            cancel=true;
            mDD.setError("Input Field should not be empty");
            focusView = mDD;
        }
        if(cancel)
        {
            if(focusView!= null)
                focusView.requestFocus();
        }
        else
        {
            db = FirebaseDatabase.getInstance().getReference();
            db.child("accounts").child(settings.getString("acc_no","")).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    float balance = Float.parseFloat(dataSnapshot.getValue().toString());
                    if(balance>1000 && Float.parseFloat(amt)<balance)
                    {
                        balance = balance-Float.parseFloat(amt);
                        db.child("accounts").child(settings.getString("acc_no","")).child("balance").setValue(String.valueOf(balance));
                        db.child("requests").child("dd").child(reqno).child("amount").setValue(amt);
                        db.child("requests").child("dd").child(reqno).child("payeename").setValue(name);
                        db.child("requests").child("dd").child(reqno).child("userid").setValue(settings.getString("uid",""));
                        db.child("transactions").child(settings.getString("uid","")).child(tid).child("amount").setValue("-"+amt);
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Insufficient balance",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


}
