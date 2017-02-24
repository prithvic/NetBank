/*
created by admin-786
 */

package com.pc.kaizer.netbank;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Pattern;
import static com.pc.kaizer.netbank.getSecurePassword.getPassword;


public class LoginActivity extends AppCompatActivity {
    public static final String CRED = "ACCDETAILS";
    private EditText mUseridView;
    private EditText mPassView;

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
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }



    public void Login() {

        isConnectedToInternet();
        if (isConnectedToInternet()) {
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
            if (!idchk(userid)) {
                cancel = true;
                mUseridView.setError(getString(R.string.recog));
                focusView = mUseridView;
            }
            if (!TextUtils.isDigitsOnly(userid)) {
                cancel = true;
                mUseridView.setError(getString(R.string.invalid));
                focusView = mUseridView;
            }
            if (pass_chk(password)) {
                cancel = true;
                mPassView.setError(getString(R.string.invalid));
                focusView = mPassView;
            }
            if (cancel) {
                focusView.requestFocus();
            } else {

            }
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet Connection!");
            builder.setMessage("We have detected that you are not connected to Internet.\n\nPlease connect to internet and try logging in.")
                    .setCancelable(false)
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    private boolean pass_chk(String pass)
    {
        return TextUtils.isEmpty(pass);
    }
    private boolean idchk(String id)
    {
        return !Pattern.matches("[a-zA-Z]+", id) && (id.length() > 0 || id.length() < 7) && id.length() == 6;
    }



}
