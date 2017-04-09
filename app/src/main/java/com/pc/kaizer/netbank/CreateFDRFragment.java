package com.pc.kaizer.netbank;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateFDRFragment extends Fragment {
    private EditText fdramt;
    String fdamt;
    String dur;
    private EditText duration;
    private TextView interest;
    private Button fdrreq;
    private DatabaseReference db;
    SharedPreferences settings;
    Random Ran = new Random(System.currentTimeMillis());



    public CreateFDRFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseDatabase.getInstance().getReference();
        settings = getActivity().getSharedPreferences("ACCDETAILS", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_fdr, container, false);
        fdramt = (EditText)v.findViewById(R.id.fdramt);
        duration = (EditText)v.findViewById(R.id.duration);
        interest = (TextView)v.findViewById(R.id.interest);
        interest.setText("Interest Rate: 7.25%");
        fdrreq = (Button) v.findViewById(R.id.ReqFDR);
        fdrreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });
        return v;
    }
    private void request()
    {
        fdamt = fdramt.getText().toString();
        dur = duration.getText().toString();
        Boolean cancel = false;
        View focus = null;
        if(fdamt.isEmpty() && dur.isEmpty())
        {
            cancel=true;
            focus=fdramt;
            fdramt.setError("Input cannot be Blank");
        }
        if(Float.parseFloat(fdamt) % 100 != 0 && Float.parseFloat(fdamt) % 500 != 0 && Float.parseFloat(fdamt)<=0)
        {
            cancel=true;
            focus=fdramt;
            fdramt.setError("Invalid amount");
        }
        if(Integer.parseInt(dur)>5)
        {
            cancel=true;
            focus=duration;
            duration.setError("Duration cannot be greater than 5");
        }
        if(Integer.parseInt(dur)==0)
        {
            cancel=true;
            focus=duration;
            duration.setError("Duration cannot be 0");
        }
        if(cancel)
        {
            focus.requestFocus();
        }
        else
        {
            try
            {
                db.child("accounts").child(settings.getString("acc_no","")).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null)
                        {
                            float balance = Float.parseFloat(dataSnapshot.getValue().toString());
                            if(balance >1000 && Float.parseFloat(fdamt)<balance)
                            {
                                int t = Integer.parseInt(dur);
                                t =t*4;
                                float r = (float)(0.0725/4)+1;
                                float A = Float.parseFloat(fdamt)*(float)Math.pow((double)r,(double)t);
                                A= A-Float.parseFloat(fdamt);
                                balance = balance - Float.parseFloat(fdamt);
                                db.child("accounts").child(settings.getString("acc_no","")).child("balance").setValue(balance);
                                final String fid = String.valueOf((1 + Ran.nextInt(2)) * 10000 + Ran.nextInt(10000));
                                final String tid = String.valueOf((1 + Ran.nextInt(2)) * 10000 + Ran.nextInt(10000));
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
                                String format = simpleDateFormat.format(new Date());
                                db.child("requests").child("fdr").child(settings.getString("uid","")).child(fid).child("amount").setValue(fdamt);
                                db.child("requests").child("fdr").child(settings.getString("uid","")).child(fid).child("timestamp").setValue(format);
                                db.child("requests").child("fdr").child(settings.getString("uid","")).child(fid).child("quarterly").setValue(String.valueOf(A));
                                db.child("transactions").child(settings.getString("uid","")).child(tid).child("amount").setValue("-" + fdamt);
                                db.child("transactions").child(settings.getString("uid","")).child(tid).child("timestamp").setValue(format);
                                db.child("transactions").child(settings.getString("uid","")).child(tid).child("type").setValue("FDR");
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"Insufficient balance",Toast.LENGTH_LONG).show();

                            }
                        }
                        else
                        {
                            Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

}
