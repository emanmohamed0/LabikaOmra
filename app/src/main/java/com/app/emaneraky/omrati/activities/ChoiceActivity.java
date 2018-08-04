package com.app.emaneraky.omrati.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.emaneraky.omrati.Application_config.myApplication;
import com.app.emaneraky.omrati.ConstantsLabika;
import com.app.emaneraky.omrati.constans.Global;
import com.app.emaneraky.omrati.notifications.MyFirebaseMessaging;
import com.app.emaneraky.omrati.notifications.MyVolley;
import com.app.emaneraky.omrati.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChoiceActivity extends AppCompatActivity {
    View linleader, lincustomer;
    private FirebaseAuth myAuth;
    String company_user_id;
    String mUser_Id;
    private DatabaseReference myDatabase;
    public static Query myPharmacyDatabase;
    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        lincustomer = (View) findViewById(R.id.lincustomer);
        linleader = (View) findViewById(R.id.linleader);
        myAuth = FirebaseAuth.getInstance();
        myDatabase = myApplication.getDatabaseReference();
        myDatabase = FirebaseDatabase.getInstance().getReference();

        myPharmacyDatabase = myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_USERS);
        lincustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTypeUser();

            }
        });

        linleader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTypeCompany();

            }
        });
    }


    public void createDialog(final int r) {
        AlertDialog alertDialog = new AlertDialog.Builder(ChoiceActivity.this).create();
        alertDialog.setTitle(getString(R.string.alert));
        alertDialog.setMessage(getString(R.string.dialog));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        if (r == R.id.lincustomer) {
                            Intent searchIntent = new Intent(ChoiceActivity.this, Home.class);
                            startActivity(searchIntent);
                        } else {
                            Intent searchIntent = new Intent(ChoiceActivity.this, CompanyLoginActivity.class);
                            startActivity(searchIntent);
                        }

                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void getTypeUser() {
        if (myAuth.getCurrentUser() != null) {
            myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_USERS).child(myAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        createDialog(R.id.lincustomer);
                    } else {
                        String type = dataSnapshot.child("type").getValue().toString();
                        if (type.equals("user")) {
                            Intent searchIntent = new Intent(ChoiceActivity.this, Home.class);
                            Global.set_UserId(ChoiceActivity.this,"mUser_Id", myAuth.getCurrentUser().getUid());
                            startActivity(searchIntent);
                        } else {
                            Intent searchIntent = new Intent(ChoiceActivity.this, Home.class);
                            Global.set_UserId(ChoiceActivity.this, "mUser_Id",myAuth.getCurrentUser().getUid());
                            startActivity(searchIntent);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else { Global.set_UserId(ChoiceActivity.this,"mUser_Id", mUser_Id);
            Intent searchIntent = new Intent(ChoiceActivity.this, Home.class);
            startActivity(searchIntent);

        }


    }


    public void getTypeCompany() {
        if (myAuth.getCurrentUser() != null) {
            myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_COMPANY).child(myAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        createDialog(R.id.linleader);

                    } else {
                        String type = dataSnapshot.child("type").getValue().toString();
                        if (type.equals("company")) {
                            Global.set_UserId(ChoiceActivity.this,"company_user_id",myAuth.getCurrentUser().getUid());
                            Intent searchIntent = new Intent(ChoiceActivity.this, CompanyOffersActivity.class);
                            startActivity(searchIntent);
                        } else {
                            Global.set_UserId(ChoiceActivity.this,"company_user_id",myAuth.getCurrentUser().getUid());
                            Intent searchIntent = new Intent(ChoiceActivity.this, CompanyLoginActivity.class);
                            startActivity(searchIntent);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Global.set_UserId(ChoiceActivity.this,"company_user_id","");
            Intent searchIntent = new Intent(ChoiceActivity.this, CompanyLoginActivity.class);
            startActivity(searchIntent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_language, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.language_en) {
            language = "en";
            Global.setLocale(ChoiceActivity.this, "en");
            refresh();
            finish();
        } else if (item.getItemId() == R.id.language_ar) {
            language = "ar";
            Global.setLocale(ChoiceActivity.this, "ar");
            refresh();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh() {
        Intent loginIntent = new Intent(ChoiceActivity.this, SplachActivity.class);
        startActivity(loginIntent);
    }

}