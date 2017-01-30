package com.pc.kaizer.netbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import static com.pc.kaizer.netbank.LoginActivity.CRED;


public class AccInfo extends Fragment {

    public AccInfo() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Details().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_acc_info, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    class Details extends AsyncTask<Void, Void, Boolean> {

        private String fname;
        private String lname;
        private String bal;
        private String userid;
        private String accno;
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String response;
                SharedPreferences settings = getActivity().getSharedPreferences(CRED, 0);
                userid = settings.getString("uid", null);
                URL url = new URL("http://aa12112.16mb.com/Welcome/details.php");
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
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
                JSONObject jObject = new JSONObject(sb.toString());
                JSONObject jArray = jObject.getJSONObject("res");
                    fname = jArray.getString("fname");
                    lname = jArray.getString("lname");
                    bal = jArray.getString("bal");
                    accno = jArray.getString("accno");
                return true;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(final Boolean success) {
            if(success) {
                TextView mName;
                TextView mAccno;
                TextView mBal;
                mName= (TextView) getActivity().findViewById(R.id.accinfo_name);
                mAccno= (TextView) getActivity().findViewById(R.id.accinfo_accno);
                mBal= (TextView) getActivity().findViewById(R.id.accinfo_Balance);
                mBal.setText("Bal.:"+bal);
                mAccno.setText("A/C No.:"+accno);
                mName.setText(fname + " " + lname);
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
