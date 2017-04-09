package com.pc.kaizer.netbank;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Prithvi on 09-04-2017.
 */

public class ViewFDRAdapter extends RecyclerView.Adapter<ViewFDRAdapter.MyViewHolderr> {
    private List<FDREntries> fdEntriesList;

    public class MyViewHolderr extends RecyclerView.ViewHolder {
        public TextView fid, ftimestamp, famt;

        public MyViewHolderr(View view) {
            super(view);
            fid = (TextView) view.findViewById(R.id.fid);
            ftimestamp = (TextView) view.findViewById(R.id.ftime);
            famt = (TextView) view.findViewById(R.id.famt);
        }
    }

    public ViewFDRAdapter(List<FDREntries> fdEntriesList) {
        this.fdEntriesList = fdEntriesList;
    }

    @Override
    public MyViewHolderr onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fdr_cards, parent, false);
        return new MyViewHolderr(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolderr holder, int position) {
        FDREntries fdrEntries = fdEntriesList.get(position);
        holder.fid.setText(fdrEntries.getFid());
        holder.ftimestamp.setText(fdrEntries.getFTimestamp());
        holder.famt.setText(fdrEntries.getFAmt());
    }

    @Override
    public int getItemCount() {
        return fdEntriesList.size();
    }
}
