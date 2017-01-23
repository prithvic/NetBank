/*
created by admin-786
 */

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import static com.pc.kaizer.netbank.getSecurePassword.getPassword;


public class LoginActivity extends AppCompatActivity {
    private EditText mUseridView;
    private EditText mPassView;
    private View mProgressView;
    private View mLoginFormView;
    private UserLoginTask auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUseridView = (EditText) findViewById(R.id.editText);
        mPassView = (EditText) findViewById(R.id.editText1);

        Button LoginButton = (Button) findViewById(R.id.button);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
        Button Reg = (Button) findViewById(R.id.Register);
                Reg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goToNextActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(goToNextActivity);
                    }
                });
        mLoginFormView = findViewById(R.id.loginform);
        mProgressView = findViewById(R.id.login_progress);
    }
    public void Login() {
        if(auth!= null)
            return;
        boolean cancel = false;
        View focusView = null;
        String userid = mUseridView.getText().toString();
        String password = mPassView.getText().toString();
        mUseridView.setError(null);
        mPassView.setError(null);
        if (TextUtils.isEmpty(userid)) {
            cancel = true;
            mUseridView.setError(getString(R.string.recog));
            focusView = mUseridView;
        }
        if(!TextUtils.isDigitsOnly(userid))
        {
            cancel = true;
            mUseridView.setError(getString(R.string.invalid));
            focusView = mUseridView;
        }
        if (pass_chk(password)) {
            cancel = true;
            mPassView.setError(getString(R.string.invalid));
            focusView = mPassView;
        }
        if (cancel)
        {
            focusView.requestFocus();
        }
        else
        {
            showProgress(true);
            auth = new UserLoginTask(userid, password);
            auth.execute((Void) null);
        }
    }
    private boolean pass_chk(String pass)
    {
        return TextUtils.isEmpty(pass);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mUID;
        private final String mPassword;
        private String response = null;
        private String login;
        private String data;

        UserLoginTask(String uid, String password) {
            mUID = uid;
            mPassword = getPassword(password);
        }

        @Override
        protected String doInBackground(Void... params) {

            try
            {
                login = "http://aa12112.freevar.com/login.php";
                data = URLEncoder.encode("uid","UTF-8") + "=" + URLEncoder.encode(mUID,"UTF-8") + "&" + URLEncoder.encode("pass","UTF-8") + "=" + URLEncoder.encode(mPassword,"UTF-8");
                URL url = new URL(login);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while((response  = reader.readLine())!= null)
                {
                    sb.append(response);
                }
                return sb.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String success) {
            auth=null;
            showProgress(false);
            if (success.equals("Login Success.")) {
                finish();
                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goToNextActivity);
                Toast.makeText(getApplicationContext(),success,Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),success,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {

            auth=null;
            showProgress(false);
        }
    }


}
