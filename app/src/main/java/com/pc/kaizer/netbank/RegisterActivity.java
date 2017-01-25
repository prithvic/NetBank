package com.pc.kaizer.netbank;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    private EditText mFnameView;
    private EditText mLnameView;
    private EditText mEmailView;
    private EditText mMobileView;
    private EditText mAddrView;
    private EditText mAccnoView;
    private CheckBox mCheckbox;
    private RegLoginTask reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFnameView = (EditText) findViewById(R.id.fname);
        mLnameView = (EditText) findViewById(R.id.lname);
        mEmailView = (EditText) findViewById(R.id.newemail);
        mMobileView = (EditText) findViewById(R.id.mobile);
        mAddrView = (EditText) findViewById(R.id.Address);
        mAccnoView = (EditText) findViewById(R.id.Account);
        mCheckbox = (CheckBox) findViewById(R.id.checkBox);
        Button LoginButton = (Button) findViewById(R.id.login);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToNextActivity = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(goToNextActivity);
            }
        });

        Button RegisterButton = (Button) findViewById(R.id.reg_button);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Register();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void Register() throws Exception {
        if(reg != null) {
            return;
        }
        Random r = new Random(System.currentTimeMillis());
        final String otp =String.valueOf((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
        final String Fname = mFnameView.getText().toString();
        final String Lname = mLnameView.getText().toString();
        final String Email = mEmailView.getText().toString();
        final String Mobile = mMobileView.getText().toString();
        final String Address = mAddrView.getText().toString();
        final String Accno = mAccnoView.getText().toString();
        final Context mContext = this;
        boolean cancel = false;
        View focusView = null;
        mFnameView.setError(null);
        mLnameView.setError(null);
        mEmailView.setError(null);
        mMobileView.setError(null);
        mAddrView.setError(null);
        if(!name_chk(Fname))
        {
            cancel=true;
            mFnameView.setError(getString(R.string.invinp));
            focusView = mFnameView;
        }
        if(!name_chk(Lname))
        {
            cancel=true;
            mLnameView.setError(getString(R.string.invinp));
            focusView = mLnameView;
        }

        if (!email_chk(Email)) {
            cancel = true;
            mEmailView.setError(getString(R.string.emlerr));
            focusView = mEmailView;
        }

        if(!mob_chk(Mobile))
        {
            cancel = true;
            mMobileView.setError(getString(R.string.mobileinv));
            focusView = mMobileView;
        }
        if(!acchk(Accno))
        {
            cancel = true;
            mAccnoView.setError(getString(R.string.accerr));
            focusView = mAccnoView;
        }

        if(!addchk(Address))
        {
            cancel = true;
            mAddrView.setError(getString(R.string.adderr));
            focusView=mAddrView;
        }
        if(!mCheckbox.isChecked())
        {
            cancel = true;
            mCheckbox.setError("check");
        }
        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            otpRequest req;
            req = new otpRequest(Mobile,otp,getApplicationContext());
            req.execute((Void) null);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialoglayout,(ViewGroup) findViewById(android.R.id.content), false);
            final EditText input = (EditText) viewInflated.findViewById(R.id.input);
            alertDialog.setView(viewInflated);
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String OTP = input.getText().toString();
                    if (OTP.equals(otp))
                        reg = new RegLoginTask(Fname, Lname, Email, Mobile, Address, Accno,mContext);
                        reg.execute((Void) null);

                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "OTP verification failed", Toast.LENGTH_LONG).show();
                }
            });
            alertDialog.show();
        }

    }
    private boolean name_chk(String name)
    {
        Pattern p = Pattern.compile("[a-zA-Z]+");
        Matcher m = p.matcher(name);
        return !(TextUtils.isEmpty(name) || !m.matches() || name.length() == 0);
    }
    private boolean email_chk(String email)
    {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean mob_chk(String mob) {

        return !Pattern.matches("[a-zA-Z]+", mob) && (mob.length() > 0 || mob.length() < 11) && mob.length() == 10;
    }
    private boolean addchk(String add) {
        return !TextUtils.isEmpty(add) && add.length() > 15;
    }
    private boolean acchk(String accno)
    {
        return !Pattern.matches("[a-zA-Z]+", accno) && (accno.length() > 0 || accno.length() < 12) && accno.length() == 11;
    }


    public class RegLoginTask extends AsyncTask<Void, Void, String>
    {
        private final String fname;
        private final String lname;
        private final String email;
        private final String mobile;
        private final String address;
        private final String accno;
        private final Context cotx;
        RegLoginTask(String Fname,String Lname,String Email,String Mobile,String Address,String Accno,Context ctx)
        {
            fname=Fname;
            lname=Lname;
            email=Email;
            mobile=Mobile;
            address=Address;
            accno = Accno;
            this.cotx =ctx;
        }
        @Override
        protected String doInBackground(Void... params) {
            try {
                String register = "http://aa12112.16mb.com/register.php";
                String response;
                String data = URLEncoder.encode("fname", "UTF-8") + "=" + URLEncoder.encode(fname, "UTF-8")
                        + "&" + URLEncoder.encode("lname", "UTF-8") + "=" + URLEncoder.encode(lname, "UTF-8")
                        + "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
                        + "&" + URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(mobile, "UTF-8")
                        + "&" + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")
                        + "&" + URLEncoder.encode("accno", "UTF-8") + "=" + URLEncoder.encode(accno, "UTF-8");
                URL url = new URL(register);
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

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String success)
        {
            reg=null;
                if(success.equals("Register Success")) {
                    Toast.makeText(getApplicationContext(),success, Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(cotx);
                    builder.setMessage("Login details have been sent to you email.Redirecting you to login page")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent goToNextActivity = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(goToNextActivity);
                                }
                            });
                    builder.show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG).show();
                }

        }

        @Override
        protected void onCancelled()
        {
            reg=null;
        }
    }
}
