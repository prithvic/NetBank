package com.pc.kaizer.netbank;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class IssueChequebookFragment extends Fragment {
    private TextView mAddress;
    private TextView mReqno;
    private Button mReq;
    private DatabaseReference db;
    SharedPreferences settings;

    public IssueChequebookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Issue Chequebook");
        settings = getActivity().getSharedPreferences("ACCDETAILS", 0);
        db = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAddress = (TextView) getActivity().findViewById(R.id.addrcontent);
        mAddress.setText(settings.getString("address",""));
        mReq = (Button) getActivity().findViewById(R.id.requestchqbk);
        mReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });
        display();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_issue_chequebook, container, false);
        return v;
    }

    private void display()
    {
        mReqno =(TextView) getActivity().findViewById(R.id.chqno);
        db.child("requests").child("chequebook").child(settings.getString("uid","")).child("reqno").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null)
                {
                    mReqno.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void request()
    {
        db.child("requests").child("chequebook").child(settings.getString("uid","")).child("reqno").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("Not Yet Issued"))
                {
                    Random r = new Random(System.currentTimeMillis());
                    String reqno = String.valueOf((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
                    db.child("requests").child("chequebook").child(settings.getString("uid","")).child("reqno").setValue(reqno);
                    Toast.makeText(getActivity(),"Chequebook Requested",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getActivity(),"Chequebook aldready Requested",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
