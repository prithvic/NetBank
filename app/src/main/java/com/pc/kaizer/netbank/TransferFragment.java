package com.pc.kaizer.netbank;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.pc.kaizer.netbank.LoginActivity.CRED;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransferFragment extends Fragment {

    private Spinner list;
    private Button trns;
    private EditText amt;

    public TransferFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_transfer, container, false);
        trns = (Button) v.findViewById(R.id.transfer);
        amt = (EditText) v.findViewById(R.id.trnsamt);
        list = (Spinner) v.findViewById(R.id.benlist);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.addben);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBenificiary addBenificiary = new AddBenificiary();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, addBenificiary);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        trns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new transfer().execute();
            }
        });
    return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new populate_benificiary().execute();

    }
    class populate_benificiary extends AsyncTask<Void, Void, Boolean>
    {
        private ArrayList<String> benilist = new ArrayList<String>();

        @Override
        protected Boolean doInBackground(Void... params) {

            try
            {
                String response;
                String fetchben = "http://aa12112.16mb.com/main_modules/fetchben.php";
                URL url = new URL(fetchben);
                SharedPreferences settings = getActivity().getSharedPreferences(CRED, 0);
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(settings.getString("uid", null),"UTF-8");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while ((response = reader.readLine()) != null) {
                    sb.append(response);
                }
                JSONObject jsonobj = new JSONObject(sb.toString());
                JSONArray jarray = jsonobj.getJSONArray("benificiary");
                benilist.add("Select Benificiary");
                for(int i=0 ;i<jarray.length();i++)
                {
                    jsonobj = jarray.getJSONObject(i);

                    benilist.add(jsonobj.optString("b_name"));
                }
                return true;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if(s)
            {
                list.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,benilist));
            }
            else
            {
                Toast.makeText(getActivity(),"Failed to populate list",Toast.LENGTH_LONG).show();
            }


        }
    }

    class transfer extends AsyncTask<Void,Void,String>
    {
        private String amount;
        private String benificiary;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            amount = amt.getText().toString();
            if(TextUtils.isDigitsOnly(amount) && !TextUtils.isEmpty(amount))
            {
                float trnsamt = Float.parseFloat(amount);
                if(trnsamt%100 !=0 && trnsamt %500 !=0)
                {
                    Toast.makeText(getActivity(),"Enter valid amount",Toast.LENGTH_LONG).show();
                    cancel(true);
                }
                else
                {
                    if(list != null && list.getSelectedItem()!= null)
                    {
                        benificiary = (String) list.getSelectedItem();
                        if(benificiary!="Select Benificiary")
                        {
                            Toast.makeText(getActivity(),"Proccessing",Toast.LENGTH_LONG).show();
                        }
                        else
                            notSelect();
                    }
                    else
                        notSelect();
                }
            }
            else
            {
                Toast.makeText(getActivity(),"Enter valid amount",Toast.LENGTH_LONG).show();
                cancel(true);
            }

        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                String response;
                String transfer = "http://aa12112.16mb.com/main_modules/transfer.php";
                URL url = new URL(transfer);
                SharedPreferences settings = getActivity().getSharedPreferences(CRED, 0);
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(settings.getString("uid", null),"UTF-8")
                                +"&"+ URLEncoder.encode("b_name", "UTF-8") + "=" + URLEncoder.encode(benificiary,"UTF-8")
                                +"&"+ URLEncoder.encode("amt", "UTF-8") + "=" + URLEncoder.encode(amount,"UTF-8");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while ((response = reader.readLine()) != null) {
                    sb.append(response);
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        protected void notSelect()
        {
            Toast.makeText(getActivity(),"Select benificiary first",Toast.LENGTH_LONG).show();
            cancel(true);
        }

    }
}


