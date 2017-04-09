package com.pc.kaizer.netbank;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFDRFragment extends Fragment {
    private List<FDREntries> fdrEntriesList = new ArrayList<>();
    protected RecyclerView frecyclerView;
    private ViewFDRAdapter fAdapter;

    public ViewFDRFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View frootView = inflater.inflate(R.layout.fragment_view_fdr, container, false);
        frecyclerView = (RecyclerView) frootView.findViewById(R.id.recyclerViewFDR);
        fAdapter = new ViewFDRAdapter(fdrEntriesList);
        frecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        frecyclerView.setAdapter(fAdapter);
        frecyclerView.setItemAnimator(new DefaultItemAnimator());
        prepareFDR();
        return frootView;
    }

    private void prepareFDR(){
        FDREntries fdrEntries = new FDREntries("TID","TIME","AMT");
        fdrEntriesList.add(fdrEntries);
        fdrEntries = new FDREntries("TID","TIME","AMT");
        fdrEntriesList.add(fdrEntries);
        fdrEntries = new FDREntries("TID","TIME","AMT");
        fdrEntriesList.add(fdrEntries);
        fdrEntries = new FDREntries("TID","TIME","AMT");
        fdrEntriesList.add(fdrEntries);
        fdrEntries = new FDREntries("TID","TIME","AMT");
        fdrEntriesList.add(fdrEntries);
        fdrEntries = new FDREntries("TID","TIME","AMT");
        fdrEntriesList.add(fdrEntries);
        fdrEntries = new FDREntries("TID","TIME","AMT");
        fdrEntriesList.add(fdrEntries);
        fdrEntries = new FDREntries("TID","TIME","AMT");
        fdrEntriesList.add(fdrEntries);
    }


    //Recycler View Adapter
    public class ViewFDRAdapter extends RecyclerView.Adapter<ViewFDRAdapter.MyViewHolderr> {
        private List<FDREntries> fdrEntriesList;

        public class MyViewHolderr extends RecyclerView.ViewHolder {
            public TextView fid, ftimestamp, famt;

            public MyViewHolderr(View view) {
                super(view);
                fid = (TextView) view.findViewById(R.id.fid);
                ftimestamp = (TextView) view.findViewById(R.id.ftime);
                famt = (TextView) view.findViewById(R.id.famt);
            }
        }

        public ViewFDRAdapter(List<FDREntries> fdrEntries) {
            this.fdrEntriesList = fdrEntries;
        }

        @Override
        public MyViewHolderr onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fdr_cards, parent, false);

            return new MyViewHolderr(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolderr holder, int position) {
            FDREntries fdrE = fdrEntriesList.get(position);
            holder.fid.setText(fdrE.getFid());
            holder.ftimestamp.setText(fdrE.getFTimestamp());
            holder.famt.setText(fdrE.getFAmt());
        }

        @Override
        public int getItemCount() {
            return fdrEntriesList.size();
        }
    }

    //Recycler View Entries
    public class FDREntries {
        private String fid, ftimestamp, famt;

        public FDREntries() {
        }

        public FDREntries(String fid, String ftimestamp, String famt) {
            this.fid = fid;
            this.ftimestamp = ftimestamp;
            this.famt = famt;
        }

        public String getFid() {
            return fid;
        }

        public void setFid(String fid) {
            this.fid = fid;
        }

        public String getFTimestamp() {
            return ftimestamp;
        }

        public void setFTimestamp(String ftimestamp) {
            this.ftimestamp = ftimestamp;
        }

        public String getFAmt() {
            return famt;
        }

        public void setFAmt(String famt) {
            this.famt = famt;
        }
    }
}
