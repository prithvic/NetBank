package com.pc.kaizer.netbank;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;
import java.util.regex.Pattern;

import static com.pc.kaizer.netbank.LoginActivity.CRED;

public class AddBenificiary extends Fragment {

    private EditText mPayeename;
    private EditText mPayeeaccno;
    private EditText mPayeeadd;
    private EditText mIFSC;
    private EditText mPayeelimit;
    private CheckBox mCheckbox;
    private AlertDialog.Builder alertDialog;
    private View viewInflated;
    private EditText input;
    private addPayee addp;


    public AddBenificiary() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View addview = inflater.inflate(R.layout.fragment_add_benificiary, container, false);
        mPayeename = (EditText) addview.findViewById(R.id.payeename);
        mPayeeaccno = (EditText) addview.findViewById(R.id.payeeacc);
        mPayeeadd = (EditText) addview.findViewById(R.id.payeeaddr);
        mIFSC = (EditText) addview.findViewById(R.id.payeeifsc);
        mPayeelimit = (EditText) addview.findViewById(R.id.payeelimit);
        mCheckbox = (CheckBox) addview.findViewById(R.id.checkben);
        final Button btn = (Button) addview.findViewById(R.id.cancel);
        alertDialog = new AlertDialog.Builder(getActivity());
        viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.dialoglayout,(ViewGroup) addview.findViewById(android.R.id.content), false);
        input = (EditText) viewInflated.findViewById(R.id.input);
        alertDialog.setView(viewInflated);
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
        final Button addpayee = (Button) addview.findViewById(R.id.addpayee);
        addpayee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    return addview;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void validate()
    {
        Random r = new Random(System.currentTimeMillis());
        final String otp =String.valueOf((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
        final String payeename = mPayeename.getText().toString();
        final String payeeaccno = mPayeeaccno.getText().toString();
        final String payeeadd = mPayeeadd.getText().toString();
        final String ifsc = mIFSC.getText().toString();
        final String payeelimit = mPayeelimit.getText().toString();
        mPayeename.setError(null);
        mPayeeaccno.setError(null);
        mIFSC.setError(null);
        mPayeeadd.setError(null);
        mPayeelimit.setError(null);
        mCheckbox.setError(null);
        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(payeeaccno) || TextUtils.isEmpty(payeename) || TextUtils.isEmpty(payeeadd) || TextUtils.isEmpty(ifsc) || TextUtils.isEmpty(payeelimit))
        {
            Toast.makeText(getActivity(),"Input should not be empty",Toast.LENGTH_SHORT).show();
        }
        if(!(!Pattern.matches("[a-zA-Z]+", payeeaccno) && (payeeaccno.length() > 0 || payeeaccno.length() < 12) && payeeaccno.length() == 11)) {
            cancel = true;
            mPayeeaccno.setError(getString(R.string.accerr));
            focusView = mPayeeaccno;
        }
        if(!Pattern.matches("[a-zA-Z]+", payeename))
        {
            cancel = true;
            mPayeename.setError(getString(R.string.invinp));
            focusView = mPayeename;
        }
        if(!(payeeadd.length() > 15))
        {
            cancel = true;
            mPayeeadd.setError(getString(R.string.adderr));
            focusView = mPayeeadd;
        }
        if(!(!Pattern.matches("[a-zA-Z]+", ifsc) && (ifsc.length() > 0 || ifsc.length() < 12)))
        {
            cancel = true;
            mIFSC.setError(getString(R.string.invinp));
            focusView = mIFSC;
        }
        if(TextUtils.isDigitsOnly(payeelimit) && !TextUtils.isEmpty(payeelimit))
        {
            final float limit =Float.parseFloat(payeelimit);
            if (limit % 100 != 0 && limit % 500 != 0) {
                cancel = true;
                mPayeelimit.setError(getString(R.string.inv_amt));
                focusView = mPayeelimit;
            }
        }
        if(!mCheckbox.isChecked())
        {
            cancel = true;
            mCheckbox.setError("");
            focusView = mCheckbox;
        }
        if(cancel)
        {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }
        else
        {
            SharedPreferences settings = getActivity().getSharedPreferences(CRED, 0);
            String mobile = settings.getString("mobile", null);
            otpRequest req;
            req = new otpRequest(mobile,otp,getActivity().getApplicationContext());
            req.execute((Void) null);

            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String OTP = input.getText().toString();
                    if (OTP.equals(otp)) {
                        addp = new addPayee(payeename,payeeaccno,payeeadd,ifsc,payeelimit,getActivity());
                        addp.execute();
                    }
                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(), "OTP verification failed", Toast.LENGTH_LONG).show();
                }
            });
            alertDialog.show();
        }




    }

}
class addPayee extends AsyncTask<Void, Void, String>
{

    private String name;
    private String accno;
    private String add;
    private String ifsc;
    private String limit;
    private Context mContext;
    addPayee(String payeename,String payeeaccno,String payeeadd,String ifsc,String payeelimit,Context ctx ){
        name=payeename;
        accno=payeeaccno;
        add=payeeadd;
        this.ifsc=ifsc;
        limit = payeelimit;
        mContext=ctx;
    }


    @Override
    protected String doInBackground(Void... params) {
        try
        {
            String response;
            String addben = "http://aa12112.16mb.com/main_modules/addben.php";
            URL url = new URL(addben);
            SharedPreferences settings = mContext.getSharedPreferences(CRED, 0);
            String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(settings.getString("uid", null), "UTF-8")
                            +"&"+URLEncoder.encode("bname", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                            +"&"+URLEncoder.encode("baccno", "UTF-8") + "=" + URLEncoder.encode(accno, "UTF-8")
                            +"&"+URLEncoder.encode("baddr", "UTF-8") + "=" + URLEncoder.encode(add, "UTF-8")
                            +"&"+URLEncoder.encode("bifsc", "UTF-8") + "=" + URLEncoder.encode(ifsc, "UTF-8")
                            +"&"+URLEncoder.encode("blimit", "UTF-8") + "=" + URLEncoder.encode(limit, "UTF-8");
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
        if(s.equals("added"))
        {
            Toast.makeText(mContext,"Benificiary added",Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
        }
    }
}

