package com.apps.labikaomra.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.apps.labikaomra.Application_config.myApplication;
import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.apps.labikaomra.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ChoiceActivity extends AppCompatActivity {
    View linleader, lincustomer;
    private FirebaseAuth myAuth;
    String company_user_id;
    String mUser_Id;
    private DatabaseReference myDatabase;
    public static Query myPharmacyDatabase;
    Locale myLocale;
    String locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        locale = getIntent().getStringExtra("locale");
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

    public void  createDialog(final int r ){
        AlertDialog alertDialog = new AlertDialog.Builder(ChoiceActivity.this).create();
        alertDialog.setTitle(getString(R.string.alert));
        alertDialog.setMessage(getString(R.string.dialog));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        if(r==R.id.lincustomer){
                            Intent searchIntent = new Intent(ChoiceActivity.this, Home.class);
                            searchIntent.putExtra("locale", locale);
                            startActivity(searchIntent);
                        }else{
                            Intent searchIntent = new Intent(ChoiceActivity.this, CompanyLoginActivity.class);
                            searchIntent.putExtra("locale", locale);
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
                    if(dataSnapshot.getValue() == null){
                        createDialog(R.id.lincustomer);

                    }else {
                        String type = dataSnapshot.child("type").getValue().toString();
                        if (type.equals("user")) {
                        Intent searchIntent = new Intent(ChoiceActivity.this, Home.class);
                        searchIntent.putExtra("mUser_Id", myAuth.getCurrentUser().getUid());
                        searchIntent.putExtra("locale", locale);
                        startActivity(searchIntent);
                    } else {
                        Intent searchIntent = new Intent(ChoiceActivity.this, Home.class);
                        searchIntent.putExtra("mUser_Id", myAuth.getCurrentUser().getUid());
                        searchIntent.putExtra("locale", locale);
                        startActivity(searchIntent);
                    }}
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Intent searchIntent = new Intent(ChoiceActivity.this, Home.class);
            searchIntent.putExtra("mUser_Id", mUser_Id);
            searchIntent.putExtra("locale", locale);
            startActivity(searchIntent);
        }

    }

    public void getTypeCompany() {
        if (myAuth.getCurrentUser() != null) {
            myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_COMPANY).child(myAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() == null){
                       createDialog(R.id.linleader);

                    }else {
                        String type = dataSnapshot.child("type").getValue().toString();
                        if (type.equals("company")) {
                            Intent searchIntent = new Intent(ChoiceActivity.this, CompanyOffersActivity.class);
                            searchIntent.putExtra("company_user_id", myAuth.getCurrentUser().getUid());
                            searchIntent.putExtra("locale", locale);
                            startActivity(searchIntent);
                        } else {
                            Intent searchIntent = new Intent(ChoiceActivity.this, CompanyLoginActivity.class);
                            searchIntent.putExtra("company_user_id", myAuth.getCurrentUser().getUid());
                            searchIntent.putExtra("locale", locale);
                            startActivity(searchIntent);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Intent searchIntent = new Intent(ChoiceActivity.this, CompanyLoginActivity.class);
            searchIntent.putExtra("company_user_id", company_user_id);
            searchIntent.putExtra("locale", locale);
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
            setLocale("en");
            Intent loginIntent = new Intent(ChoiceActivity.this, SplachActivity.class);
            loginIntent.putExtra("locale", "en");
            startActivity(loginIntent);
            finish();
        } else if (item.getItemId() == R.id.language_ar) {
            setLocale("ar");
            Intent loginIntent = new Intent(ChoiceActivity.this, SplachActivity.class);
            loginIntent.putExtra("locale", "ar");
            startActivity(loginIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLocale(String language) {
        myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
