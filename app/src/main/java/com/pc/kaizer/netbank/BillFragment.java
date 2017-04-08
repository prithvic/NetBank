package com.pc.kaizer.netbank;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static com.pc.kaizer.netbank.R.style.Theme_AppCompat_Dialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class BillFragment extends Fragment {

    private EditText ed2;
    private RadioButton elec;
    private RadioButton tel;
    private RadioButton water;
    private Button submit;
    private DatabaseReference db;
    private String type ="";
    private String amt;
    Random r = new Random(System.currentTimeMillis());
    SharedPreferences settings;
    public BillFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Bill Payment");
        settings = getActivity().getSharedPreferences("ACCDETAILS", 0);
        db = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bill, container, false);
        ed2 = (EditText) v.findViewById(R.id.editText2);
        elec = (RadioButton) v.findViewById(R.id.electricity);
        tel = (RadioButton) v.findViewById(R.id.telephone);
        water =(RadioButton) v.findViewById(R.id.water);
        submit =(Button) v.findViewById(R.id.paybill);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        return v;
    }

    private void submit()
    {
        amt = ed2.getText().toString();
        View focus = null;
        Log.d("3",type);
        boolean cancel=false;
        if(!elec.isEnabled() && !tel.isEnabled() && !water.isEnabled())
        {
            cancel = true;
            focus = ed2;
            Toast.makeText(getActivity(),"Please select atleast one category",Toast.LENGTH_LONG);
        }
        if(elec.isEnabled())
        {
            type = "electricity";
        }
        if(tel.isEnabled())
        {
            type = "telephone";
        }
        if(water.isEnabled()) {
            type = "water";
        }
        if(amt.isEmpty())
        {
            cancel = true;
            ed2.setError("Please enter a valid amount");
            focus = ed2;
        }
        if(cancel)
        {
            Log.d("1",type);
            focus.requestFocus();
        }
        else
        {
            Log.d("GG",type);
            db.child("users").child(settings.getString("uid","")).child(type).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.getValue().toString().equals("Not assigned"))
                        {
                            db.child("accounts").child(settings.getString("acc_no","")).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot){
                                    float balance = Float.parseFloat(dataSnapshot.getValue().toString());
                                    Log.d("gg",String.valueOf(balance));
                                    balance = balance-Float.parseFloat(amt);
                                    db.child("accounts").child(settings.getString("acc_no","")).child("balance").setValue(balance);
                                    final String tid = String.valueOf((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
                                    String format = simpleDateFormat.format(new Date());
                                    db.child("transactions").child(settings.getString("uid","")).child(tid).child("amount").setValue("-" + amt);
                                    db.child("transactions").child(settings.getString("uid","")).child(tid).child("timestamp").setValue(format);
                                    db.child("transactions").child(settings.getString("uid","")).child(tid).child("type").setValue("Bill: "+type);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Bill Pay alert");
                                    builder.setMessage("Tera bill bhar diya bhai! Tension na le!");
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Please register your "+type+"customer number with the bank.", Toast.LENGTH_SHORT).show();
                        }
                    }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
