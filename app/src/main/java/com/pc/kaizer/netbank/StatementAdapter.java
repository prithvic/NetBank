package com.pc.kaizer.netbank;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Prithvi on 02-04-2017.
 */

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.MyViewHolder> {
    private List<StatementEntries> StatementList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tid, type, timestamp, line;

        public MyViewHolder(View view){
            super(view);
            tid = (TextView) view.findViewById(R.id.tid);
            type = (TextView) view.findViewById(R.id.type);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            line = (TextView) view.findViewById(R.id.line);
        }
    }

    public StatementAdapter(List<StatementEntries> StatementList){
        this.StatementList = StatementList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.statement_cards, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        StatementEntries statementEntries = StatementList.get(position);
        holder.tid.setText(statementEntries.getTid());
        holder.type.setText(statementEntries.getType());
        holder.timestamp.setText(statementEntries.getTimestamp());
        holder.line.setText(statementEntries.getLine());
    }

    @Override
    public int getItemCount(){
        return StatementList.size();
    }
}
