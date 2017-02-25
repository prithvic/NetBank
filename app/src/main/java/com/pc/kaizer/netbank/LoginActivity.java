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
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {
    public static final String CRED = "ACCDETAILS";
    private EditText mUseridView;
    private EditText mPassView;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
            final String userid = mUseridView.getText().toString();
            final String password = mPassView.getText().toString();
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
                mPassView.setError("Empty input field");
                focusView = mPassView;
            }
            if (cancel) {
                focusView.requestFocus();
            } else {
                progress = new ProgressDialog(LoginActivity.this);
                progress.setCancelable(true);
                progress.setMessage("Logging in");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.show();
                mDatabase.child("users").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null)
                        {
                            authlogin(dataSnapshot.getValue().toString(),password,userid);
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"No such user",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this,"Database error",Toast.LENGTH_LONG).show();
                    }
                });
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

    public void authlogin(String email,String pass,final String uid)
    {
        mFirebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progress.dismiss();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
                    String format = simpleDateFormat.format(new Date());
                    DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
                    DB.child("users").child(uid).child("last_login").setValue(format);
                    Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_LONG).show();
                    Intent goToNextActivity = new Intent(getApplicationContext(), Home.class);
                    startActivity(goToNextActivity);
                    SharedPreferences settings = getSharedPreferences(CRED, 0);
                    SharedPreferences.Editor editor = settings.edit();

                }
                else {
                    progress.dismiss();
                    Toast.makeText(LoginActivity.this,"Login failed check userid or password",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
