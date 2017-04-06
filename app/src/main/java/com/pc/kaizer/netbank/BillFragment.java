package com.pc.kaizer.netbank;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
        String amt = ed2.getText().toString();
        View focus = null;
        boolean cancel=false;
        if(!elec.isSelected() && !tel.isSelected() && !water.isSelected())
        {
            cancel = true;
            Toast.makeText(getActivity(),"Please select atleast one category",Toast.LENGTH_LONG);
        }
        if(elec.isSelected())
        {
            type = "electricity";
        }
        if(tel.isSelected())
        {
            type = "telephone";
        }
        if(water.isSelected()) {
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
            focus.requestFocus();
        }
        else
        {
            db.child("users").child(type).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.getValue().equals(null))
                    {
                        if(dataSnapshot.getValue().toString().equals("Not assigned"))
                        {
                            db.child("accounts").child(settings.getString("acc_no","")).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot){
                                    
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
                    else
                    {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
