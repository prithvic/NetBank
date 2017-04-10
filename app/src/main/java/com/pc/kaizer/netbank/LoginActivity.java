/*
created by admin-786
 */

package com.pc.kaizer.netbank;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {
    public static final String CRED = "ACCDETAILS";
    private EditText mUseridView;
    private EditText mPassView;
    private TextView mForgotPass;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog progress;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUseridView = (EditText) findViewById(R.id.editText);
        mPassView = (EditText) findViewById(R.id.editText1);
        mForgotPass = (TextView) findViewById(R.id.forgotpass);
        mForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotpass();
            }
        });
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
            userid = mUseridView.getText().toString();
            final String password = mPassView.getText().toString();
            mUseridView.setError(null);
            mPassView.setError(null);
            if (TextUtils.isEmpty(userid)) {
                cancel = true;
                mUseridView.setError(getString(R.string.emptyfield));
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
                mPassView.setError(getString(R.string.emptyfield));
                focusView = mPassView;
            }
            if (cancel) {
                focusView.requestFocus();
            } else {
                progress = new ProgressDialog(LoginActivity.this);
                progress.setCancelable(false);
                progress.setMessage("Logging in");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.show();
                mDatabase.child("users").child(userid).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null)
                        {
                            authlogin(dataSnapshot.getValue().toString(),password,userid);
                        }
                        else
                        {
                            progress.dismiss();
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
                    isEmailVerified();
                    /*progress.dismiss();
                    SharedPreferences settings = getSharedPreferences(CRED, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("uid",uid);
                    editor.apply();
                    Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_LONG).show();
                    Intent goToNextActivity = new Intent(getApplicationContext(), Home.class);
                    startActivity(goToNextActivity);*/

                }
                else {
                    progress.dismiss();
                    Toast.makeText(LoginActivity.this,"Login failed check userid or password",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void forgotpass()
    {
        String uid = mUseridView.getText().toString();
        if(TextUtils.isEmpty(uid))
        {
            Toast.makeText(LoginActivity.this,"Enter User ID to retrieve password",Toast.LENGTH_LONG).show();
        }
        else
        {
            mDatabase.child("users").child(uid).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String email = dataSnapshot.getValue().toString();
                        mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(LoginActivity.this,"Password recovery email sent",Toast.LENGTH_LONG).show();

                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this,"Password recovery failed",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {

                        Toast.makeText(LoginActivity.this, "No such user", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this,"Database error",Toast.LENGTH_LONG).show();
                }
                });
        }
    }

    private void isEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        if(user.isEmailVerified())
        {
            progress.dismiss();
            SharedPreferences settings = getSharedPreferences(CRED, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("uid",userid);
            editor.apply();
            Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_LONG).show();
            Intent goToNextActivity = new Intent(getApplicationContext(), Home.class);
            startActivity(goToNextActivity);
        }
        else
        {
            progress.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please select one option to proceed")
                    .setTitle("Email Unverified!")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(LoginActivity.this,"Login failed(Email Unverified)",Toast.LENGTH_LONG).show();
                        }
                    })
                    .setPositiveButton("Send Confirmation", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sendVerification();
                            FirebaseAuth.getInstance().signOut();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void sendVerification()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"Email verification sent", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Email verification sending failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
