package com.pc.kaizer.netbank;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    private EditText mFnameView;
    private EditText mLnameView;
    private EditText mEmailView;
    private EditText mMobileView;
    private View mProgressView;
    private View mRegFormView;
    private RegLoginTask reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFnameView = (EditText) findViewById(R.id.fname);
        mLnameView = (EditText) findViewById(R.id.lname);
        mEmailView = (EditText) findViewById(R.id.newemail);
        mMobileView = (EditText) findViewById(R.id.mobile);

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

        mProgressView = findViewById(R.id.reg_progress);
        mRegFormView = findViewById(R.id.reg_form);

    }
    public void Register() throws Exception {
        if(reg != null) {
            return;
        }
        String Fname = mFnameView.getText().toString();
        String Lname = mLnameView.getText().toString();
        String Email = mEmailView.getText().toString();
        String Mobile = mMobileView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        mFnameView.setError(null);
        mLnameView.setError(null);
        mEmailView.setError(null);
        mMobileView.setError(null);
        if(!name_chk(Fname))
        {
            cancel=true;
            mFnameView.setError(getString(R.string.invalid));
            focusView = mFnameView;
        }
        if(!name_chk(Lname))
        {
            cancel=true;
            mLnameView.setError(getString(R.string.invalid));
            focusView = mLnameView;
        }

        if (!email_chk(Email)) {
            cancel = true;
            mEmailView.setError(getString(R.string.recog));
            focusView = mEmailView;
        }

        if(!mob_chk(Mobile))
        {
            cancel = true;
            mMobileView.setError(getString(R.string.mobileinv));
            focusView = mMobileView;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            reg = new RegLoginTask(Fname,Lname,Email,Mobile);
            reg.execute((Void) null);
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
    private boolean mob_chk(String mob)
    {
        return TextUtils.isDigitsOnly(mob);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class RegLoginTask extends AsyncTask<Void, Void, String>
    {
        private final String fname;
        private final String lname;
        private final String email;
        private final String mobile;
        private String response = null;

        RegLoginTask(String Fname,String Lname,String Email,String Mobile)
        {
            fname=Fname;
            lname=Lname;
            email=Email;
            mobile=Mobile;
        }
        @Override
        protected String doInBackground(Void... params) {
            try {
                String otp = "http://aa12112.freevar.com/SMS/sendsms.php";
                String data = URLEncoder.encode("to", "UTF-8") + "=" + URLEncoder.encode(mobile, "UTF-8") + "&" + URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode("OTP here", "UTF-8");
                URL url = new URL(otp);
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
                wr.close();
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
            showProgress(false);
            if(success.equals("SENT OTP."))
            {
                finish();
                Intent goToNextActivity = new Intent(getApplicationContext(),RegSuc.class);
                startActivity(goToNextActivity);
                Toast.makeText(getApplicationContext(),success,Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),success,Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled()
        {
            reg=null;
            showProgress(false);
        }
    }
}
