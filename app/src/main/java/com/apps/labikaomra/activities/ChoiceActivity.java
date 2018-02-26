package com.apps.labikaomra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.apps.labikaomra.Application_config.myApplication;
import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.apps.labikaomra.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ChoiceActivity extends AppCompatActivity {
    View linleader, lincustomer;
    private FirebaseAuth myAuth;
    String company_user_id;
    String mUser_Id;
    private DatabaseReference myDatabase;
    public static Query myPharmacyDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        lincustomer = (View) findViewById(R.id.lincustomer);
        linleader = (View) findViewById(R.id.linleader);
        myAuth = FirebaseAuth.getInstance();
        myDatabase = myApplication.getDatabaseReference();
        myPharmacyDatabase = myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_USERS);
//         company_user_id = myAuth.getCurrentUser().getUid();
        lincustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myAuth.getCurrentUser() != null) {
                    mUser_Id = myAuth.getCurrentUser().getUid();
                    Intent searchIntent = new Intent(ChoiceActivity.this, MainActivity.class);
                    searchIntent.putExtra("mUser_Id", mUser_Id);
                    startActivity(searchIntent);
                    finish();
                } else {
                    Intent searchIntent = new Intent(ChoiceActivity.this, MainActivity.class);
                    searchIntent.putExtra("mUser_Id", mUser_Id);
                    startActivity(searchIntent);
                    finish();
                }

            }
        });

        linleader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myAuth.getCurrentUser() != null) {
                    company_user_id = myAuth.getCurrentUser().getUid();
                    Intent searchIntent = new Intent(ChoiceActivity.this, CompanyOffersActivity.class);
                    searchIntent.putExtra("company_user_id", company_user_id);
                    startActivity(searchIntent);
                    finish();
                } else {
                    Intent searchIntent = new Intent(ChoiceActivity.this, CompanyLoginActivity.class);
                    searchIntent.putExtra("company_user_id", company_user_id);
                    startActivity(searchIntent);
                    finish();
                }

            }
        });
    }
}
