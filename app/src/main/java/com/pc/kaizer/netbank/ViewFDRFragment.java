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
public class ViewFDRFragment extends Fragment {
    private List<FDREntries> fdEntriesList = new ArrayList<>();
    protected RecyclerView frecyclerView;
    private ViewFDRAdapter fAdapter;
    private DatabaseReference mDatabase;
    Map<String,String> fd =new HashMap<String,String>();
    SharedPreferences settings;

    public ViewFDRFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareFDR();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        frecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewFDR);
        fAdapter = new ViewFDRAdapter(fdEntriesList);
        frecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        frecyclerView.setAdapter(fAdapter);

        frecyclerView.setItemAnimator(new DefaultItemAnimator());
        prepareFDR();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        prepareFDR();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_fdr, container, false);
        // Inflate the layout for this fragment
        /*View frootView = inflater.inflate(R.layout.fragment_view_fdr, container, false);
        frecyclerView = (RecyclerView) frootView.findViewById(R.id.recyclerViewFDR);
        fAdapter = new ViewFDRAdapter(fdEntriesList);
        frecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        frecyclerView.setAdapter(fAdapter);
        frecyclerView.setItemAnimator(new DefaultItemAnimator());
        prepareFDR();
        return frootView;*/
    }

    private void prepareFDR() {
        try{
            mDatabase.child("requests").child("fdr").child(settings.getString("uid", "")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.getKey().toString().equals(null)) {
                        fdEntriesList.clear();
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            for (DataSnapshot dp : dsp.getChildren()) {
                                fd.put(dp.getKey(), dp.getValue().toString());
                                Log.d("GG", dp.getKey() + dp.getValue().toString());
                            }
                            FDREntries fde = new FDREntries("FID: " + dsp.getKey().toString(), "Timestamp: " + fd.get("timestamp"), "Quarterly Amount: " + fd.get("quarterly") + "Rs");
                            fdEntriesList.add(fde);
                            fd.clear();
                        }
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "No Fdr logs are logged", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        settings =getActivity().getSharedPreferences("ACCDETAILS", 0);
        Log.d("init","initialized");
    }
}