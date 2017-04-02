package com.pc.kaizer.netbank;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatementFragment extends Fragment {

    private DatabaseReference mDatabase;
    SharedPreferences settings;
    private List<StatementEntries> StatementList = new ArrayList<>();
    protected RecyclerView recyclerView;
    private StatementAdapter sAdapter;
    Map<String,String> td =new HashMap<String,String>();

    public StatementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Statement");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        settings = getActivity().getSharedPreferences("ACCDETAILS", 0);
        prepareStatement();
    }

    private void prepareStatement(){
            mDatabase.child("transactions").child(settings.getString("uid", "")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.getKey().toString().equals(null)) {
                        StatementList.clear();
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            for (DataSnapshot dp : dsp.getChildren()) {
                                td.put(dp.getKey(), dp.getValue().toString());
                            }
                            StatementEntries st = new StatementEntries("TXN: " + dsp.getKey().toString(), td.get("type"), td.get("timestamp"), td.get("amount"));
                            StatementList.add(st);
                            td.clear();
                            sAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getActivity(), "No transtions are logged", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_statement, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        sAdapter = new StatementAdapter(StatementList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(sAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        prepareStatement();
        return rootView;
    }

}
