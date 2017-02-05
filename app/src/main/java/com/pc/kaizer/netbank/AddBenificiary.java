package com.pc.kaizer.netbank;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddBenificiary extends Fragment {


    public AddBenificiary() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View addview = inflater.inflate(R.layout.fragment_add_benificiary, container, false);
        final Button btn = (Button) addview.findViewById(R.id.cancel);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View addview){
                TransferFragment transferFragment = new TransferFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, transferFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    return addview;
    }

}
