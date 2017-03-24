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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_fdr, container, false);
        mAddress = (TextView) v.findViewById(R.id.addrcontent);
        mAddress.setText(settings.getString("address",""));
        mReqno =(TextView) v.findViewById(R.id.chqno);
        db.child("requests").child("chequebook").child(settings.getString("uid","")).child("reqno").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null)
                {
                    mReqno.setText(dataSnapshot.getValue().toString());
                }
                else
                {
                    mReqno.setText("NULL");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mReq = (Button) v.findViewById(R.id.requestchqbk);
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

    }

}
