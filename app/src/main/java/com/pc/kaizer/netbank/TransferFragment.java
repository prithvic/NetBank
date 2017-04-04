package com.pc.kaizer.netbank;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static com.pc.kaizer.netbank.R.style.Theme_AppCompat_Dialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransferFragment extends Fragment {

    private Spinner list;
    private Button trns;
    private EditText amt;
    private DatabaseReference mDatabase;
    private ArrayList<String> payees = new ArrayList<String>();
    private ArrayList<String> limit = new ArrayList<String>();
    private ArrayList<String> type = new ArrayList<String>();
    private ArrayList<String> baccno = new ArrayList<String>();
    private String balance;
    private String benuid = "not_found";
    Random r = new Random(System.currentTimeMillis());
    SharedPreferences settings;
    FloatingActionMenu fam;
    FloatingActionButton fab1, fab2;

    public TransferFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_transfer, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        trns = (Button) v.findViewById(R.id.transfer);
        amt = (EditText) v.findViewById(R.id.trnsamt);
        list = (Spinner) v.findViewById(R.id.benlist);
        fam = (FloatingActionMenu) v.findViewById(R.id.ben);
        fam.setIconAnimated(false);
        fab1 = (FloatingActionButton) v.findViewById(R.id.addben);
        fab2 = (FloatingActionButton) v.findViewById(R.id.remben);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBenificiary addBenificiary = new AddBenificiary();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, addBenificiary);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove Beneficiary code.
            }
        });
        trns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transfer();
            }
        });
    return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Transfer");
        settings = getActivity().getSharedPreferences("ACCDETAILS", 0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populate_list();
    }

    void populate_list()
    {
        try {
            payees.add("Select benificiary");
            mDatabase.child("users").child(settings.getString("uid", "")).child("benificiaries").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            payees.add(dsp.getKey());
                            limit.add(dsp.child("limit").getValue().toString());
                            type.add(dsp.child("type").getValue().toString());
                            baccno.add(dsp.child("baccno").getValue().toString());
                        }
                        list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, payees));
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
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

    void transfer() {
        final String amount = amt.getText().toString();
        final String benlimit;
        final String bentype;
        final String benacc;

        try {
            if (TextUtils.isDigitsOnly(amount) && !TextUtils.isEmpty(amount) && (Float.parseFloat(amount) % 100 == 0 || Float.parseFloat(amount) % 500 == 0)) {
                if (list != null && list.getSelectedItem() != null && !list.getSelectedItem().toString().equals("Select benificiary")) {
                    int pos = payees.indexOf(list.getSelectedItem()) - 1;
                    benlimit = limit.get(pos);
                    bentype = type.get(pos);
                    benacc = baccno.get(pos);

                    mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                if(benacc.equals(ds.child("account_no").getValue().toString()))
                                {
                                    benuid = ds.getKey().toString();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    if (Float.parseFloat(amount) < Float.parseFloat(benlimit)) {
                        final String accno = settings.getString("acc_no", "");
                        mDatabase.child("accounts").child(accno).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                balance = dataSnapshot.getValue().toString();
                                if (Float.parseFloat(balance) > Float.parseFloat(amount) && Float.parseFloat(balance) > 1000) {
                                    if (bentype.equals("inbank")) {
                                        mDatabase.child("accounts").child(benacc).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String payeebal = dataSnapshot.getValue().toString();
                                                String update1 = String.valueOf(Float.parseFloat(balance) - Float.parseFloat(amount));
                                                String update2 = String.valueOf(Float.parseFloat(payeebal) + Float.parseFloat(amount));
                                                DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
                                                DB.child("accounts").child(accno).child("balance").setValue(update1);
                                                DB.child("accounts").child(benacc).child("balance").setValue(update2);
                                                final String tid = String.valueOf((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
                                                final String tid1 = String.valueOf((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
                                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
                                                String format = simpleDateFormat.format(new Date());
                                                DB.child("transactions").child(settings.getString("uid","")).child(tid).child("amount").setValue("-" + amount);
                                                DB.child("transactions").child(settings.getString("uid","")).child(tid).child("timestamp").setValue(format);
                                                DB.child("transactions").child(settings.getString("uid","")).child(tid).child("type").setValue("Transfer: Debit");
                                                DB.child("transactions").child(benuid).child(tid1).child("amount").setValue("+"+amount);
                                                DB.child("transactions").child(benuid).child(tid1).child("timestamp").setValue(format);
                                                DB.child("transactions").child(benuid).child(tid1).child("type").setValue("Transfer: Credit");
                                                alert("Transfer Successful");
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    } else {
                                        String update1 = String.valueOf(Float.parseFloat(balance) - Float.parseFloat(amount));
                                        DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
                                        DB.child("accounts").child(accno).child("balance").setValue(update1);
                                        final String tid = String.valueOf((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
                                        String format = simpleDateFormat.format(new Date());
                                        DB.child("transactions").child(settings.getString("uid","")).child(tid).child("amount").setValue("-" + amount);
                                        DB.child("transactions").child(settings.getString("uid","")).child(tid).child("timestamp").setValue(format);
                                        DB.child("transactions").child(settings.getString("uid","")).child(tid).child("type").setValue("Transfer: Debit");
                                        alert("Transfer will be done in 7 working days");
                                    }
                                } else {
                                    alert("Account balance is not sufficient");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else {
                        alert("Amount greater than beneficiary preset limit");
                    }
                } else {
                    Toast.makeText(getActivity(), "Select Benificiary first!!!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "Enter valid amount", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void alert(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),Theme_AppCompat_Dialog);
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

}



