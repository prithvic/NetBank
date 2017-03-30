package com.pc.kaizer.netbank;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Random;
import java.util.regex.Pattern;

public class AddBenificiary extends Fragment {

    private EditText mPayeename;
    private EditText mPayeeaccno;
    private EditText mIFSC;
    private EditText mPayeelimit;
    private CheckBox mCheckbox;
    private AlertDialog.Builder alertDialog;
    private EditText input;
    private String accno;
    private DatabaseReference mDatabase;

    public AddBenificiary() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View addview = inflater.inflate(R.layout.fragment_add_benificiary, container, false);
        mPayeename = (EditText) addview.findViewById(R.id.payeename);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPayeeaccno = (EditText) addview.findViewById(R.id.payeeacc);
        mIFSC = (EditText) addview.findViewById(R.id.payeeifsc);
        mPayeelimit = (EditText) addview.findViewById(R.id.payeelimit);
        mCheckbox = (CheckBox) addview.findViewById(R.id.checkben);
        final Button btn = (Button) addview.findViewById(R.id.cancel);
        alertDialog = new AlertDialog.Builder(getActivity());
        View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.dialoglayout,(ViewGroup) addview.findViewById(android.R.id.content), false);
        input = (EditText) viewInflated.findViewById(R.id.input);
        alertDialog.setView(viewInflated);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View addview){
                TransferFragment transferFragment = new TransferFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, transferFragment);
                transaction.commit();
            }
        });
        final Button addpayee = (Button) addview.findViewById(R.id.addpayee);
        addpayee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    return addview;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Add Beneficiary");
    }

    public void validate()
    {
        Random r = new Random(System.currentTimeMillis());
        final String otp =String.valueOf((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
        final String payeename = mPayeename.getText().toString();
        final String payeeaccno = mPayeeaccno.getText().toString();
        final String ifsc = mIFSC.getText().toString();
        final String payeelimit = mPayeelimit.getText().toString();
        SharedPreferences settings = getActivity().getSharedPreferences("ACCDETAILS", 0);
        accno = settings.getString("acc_no","");
        mPayeename.setError(null);
        mPayeeaccno.setError(null);
        mIFSC.setError(null);
        mPayeelimit.setError(null);
        mCheckbox.setError(null);
        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(payeeaccno) || TextUtils.isEmpty(payeename) || TextUtils.isEmpty(ifsc) || TextUtils.isEmpty(payeelimit))
        {
            Toast.makeText(getActivity(),"Input should not be empty",Toast.LENGTH_SHORT).show();
        }
        if(!(!Pattern.matches("[a-zA-Z]+", payeeaccno) && (payeeaccno.length() > 0 || payeeaccno.length() < 12) && payeeaccno.length() == 11)) {
            cancel = true;
            mPayeeaccno.setError(getString(R.string.accerr));
            focusView = mPayeeaccno;
        }
        if(!Pattern.matches("[a-zA-Z]+", payeename))
        {
            cancel = true;
            mPayeename.setError(getString(R.string.invinp));
            focusView = mPayeename;
        }
        if(!(!Pattern.matches("[a-zA-Z]+", ifsc) && (ifsc.length() > 0 || ifsc.length() < 12)))
        {
            cancel = true;
            mIFSC.setError(getString(R.string.invinp));
            focusView = mIFSC;
        }
        if(TextUtils.isDigitsOnly(payeelimit) && !TextUtils.isEmpty(payeelimit))
        {
            final float limit =Float.parseFloat(payeelimit);
            if (limit % 100 != 0 && limit % 500 != 0) {
                cancel = true;
                mPayeelimit.setError(getString(R.string.inv_amt));
                focusView = mPayeelimit;
            }
        }
        if(accno.equals(payeeaccno))
        {
            cancel = true;
            Toast.makeText(getActivity(),"Cannot enter the user account number to add in benificiary",Toast.LENGTH_LONG).show();
            focusView = mPayeeaccno;
        }
        if(!mCheckbox.isChecked())
        {
            cancel = true;
            mCheckbox.setError("");
            focusView = mCheckbox;
        }
        if(cancel)
        {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }
        else
        {
            mDatabase.child("accounts").child(payeeaccno).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()==null)
                    {
                        add(payeename,payeeaccno,ifsc,payeelimit,"interbank");
                    }
                    else
                    {
                        add(payeename,payeeaccno,ifsc,payeelimit,"inbank");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Database error contact admin",Toast.LENGTH_LONG).show();
                }
            });
        }




    }

    public void add(String pname,String paccno,String ifsc,String plimit,String type)
    {
        SharedPreferences settings = getActivity().getSharedPreferences("ACCDETAILS", 0);
        final String uid = settings.getString("uid","");
        DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
        DB.child("users").child(uid).child("benificiaries").child(pname);
        DB.child("users").child(uid).child("benificiaries").child(pname).child("baccno").setValue(paccno);
        DB.child("users").child(uid).child("benificiaries").child(pname).child("ifsc").setValue(ifsc);
        DB.child("users").child(uid).child("benificiaries").child(pname).child("limit").setValue(plimit);
        DB.child("users").child(uid).child("benificiaries").child(pname).child("type").setValue(type);
        Toast.makeText(getActivity(),"Benificiary Added",Toast.LENGTH_LONG).show();
        TransferFragment transferFragment = new TransferFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, transferFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}


