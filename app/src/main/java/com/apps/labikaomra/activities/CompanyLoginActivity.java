package com.apps.labikaomra.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class CompanyLoginActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth myAuth;
    private EditText inputEmail, inputPassword;
    String company_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myAuth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference();
        inputEmail = (EditText) findViewById(R.id.email_login);
        inputPassword = (EditText) findViewById(R.id.pswrd_login);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_signup);
        myAuth = FirebaseAuth.getInstance();
//        if(myAuth.getCurrentUser() !=null) {
//            company_user_id = myAuth.getCurrentUser().getUid();
//        }
        authenticatinChecker();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CompanyLoginActivity.this, CompanyRegisterActivity.class));
            }
        });
    }

    private void authenticatinChecker() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    user.getUid();
                    Intent start = new Intent(CompanyLoginActivity.this, CompanyOffersActivity.class);
                    start.putExtra("company_user_id", user.getUid());
                    startActivity(start);
                    finish();
                    Toast.makeText(CompanyLoginActivity.this, R.string.userin, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        myAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            myAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void mLogin() {

        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(CompanyLoginActivity.this, R.string.enter_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(CompanyLoginActivity.this, R.string.enter_pass, Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(CompanyLoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.login_account));
        progressDialog.setCancelable(false);
        progressDialog.show();
//        progressBar.setVisibility(View.VISIBLE);

        //authenticate user
        myAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(CompanyLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.less6));
                            } else {
                                Toast.makeText(CompanyLoginActivity.this, R.string.faild + "&" + getString(R.string.net), Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        } else {
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CompanyLoginActivity.this);
                            SharedPreferences.Editor spe = sp.edit();
                            spe.putString(ConstantsLabika.USER_ID, myAuth.getCurrentUser().getUid()).apply();
                            spe.commit();
                            progressDialog.dismiss();
                            Intent intent = new Intent(CompanyLoginActivity.this, CompanyOffersActivity.class);
                            intent.putExtra("company_user_id", myAuth.getCurrentUser().getUid());
                            startActivity(intent);
                            finish();
                        }
                    }
                });

    }

}
