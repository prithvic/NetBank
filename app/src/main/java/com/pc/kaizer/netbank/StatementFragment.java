package com.pc.kaizer.netbank;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatementFragment extends Fragment {

    private List<StatementEntries> StatementList = new ArrayList<>();
    protected RecyclerView recyclerView;
    private StatementAdapter sAdapter;

    public StatementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Statement");
    }

    private void prepareStatement(){
        StatementEntries statementEntries = new StatementEntries("TXN: 85321", "Credit", "12:30 27-12-2016", "+1200");
        StatementList.add(statementEntries);
        statementEntries = new StatementEntries("TXN: 85322", "Credit", "12:30 27-01-2017", "+2200");
        StatementList.add(statementEntries);
        statementEntries = new StatementEntries("TXN: 85323", "Debit", "12:30 27-01-2017", "-1300");
        StatementList.add(statementEntries);
        statementEntries = new StatementEntries("TXN: 85324", "Credit", "12:30 27-02-2017", "+2200");
        StatementList.add(statementEntries);
        statementEntries = new StatementEntries("TXN: 85325", "Debit", "12:30 27-03-2017", "-1200");
        StatementList.add(statementEntries);

        sAdapter.notifyDataSetChanged();
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
