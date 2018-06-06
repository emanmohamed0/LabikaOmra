package com.apps.labikaomra.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.apps.labikaomra.Application_config.myApplication;
import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.apps.labikaomra.adapters.CompanyOfferAdapter;
import com.apps.labikaomra.models.Offer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CompanyOffersActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static Query myPharmacyDatabase;
    public static Location mLastLocation;
    public static CompanyOfferAdapter adapter;
    public static Context mcontext;
    static List<Offer> clientList;
    private FirebaseAuth myAuth;
    static RecyclerView mCategoriesRecyclerView;
    private DatabaseReference myDatabase;
    private GoogleApiClient mGoogleApiClient;
    String company_user_id;

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_offers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_company_offers));
        setSupportActionBar(toolbar);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.keepSynced(true);
        company_user_id = getIntent().getStringExtra("company_user_id");
        mcontext = getBaseContext();
        myAuth = FirebaseAuth.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addoffer = new Intent(CompanyOffersActivity.this, AddOfferActivity.class);
                addoffer.putExtra("company_user_id", company_user_id);
                startActivity(addoffer);
            }
        });
        mCategoriesRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_company_offer);

        mDatabaseRef.child(ConstantsLabika.FIREBASE_LOCATION_COMPANY).child(company_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String active = dataSnapshot.child("activation").getValue().toString();
                checkactive(active);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void checkactive(String activation) {

        if (activation.equals("notActive")) {
            createDialog();
        } else {
            setMainList();
        }

    }

    public void createDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(CompanyOffersActivity.this).create();
        alertDialog.setTitle(getString(R.string.alert));
        alertDialog.setMessage(getString(R.string.dialogactive));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent open = new Intent(CompanyOffersActivity.this, ChoiceActivity.class);
                        startActivity(open);
                        finish();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void recyclering(Query qmyDatabase, final String search) {

        qmyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                clientList = new ArrayList<Offer>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    String one = postSnapshot.child("value_onehouse").getValue().toString();
//                    Log.d("", "onDataChange:"+one);
                    Offer pharmacy = postSnapshot.getValue(Offer.class);
                    pharmacy.getCompanyKeyId();
                    clientList.add(pharmacy);

                }
//                Toast.makeText(mcontext, "list count" + clientList.size(), Toast.LENGTH_SHORT).show();
                adapter = new CompanyOfferAdapter(mcontext, clientList);
                mCategoriesRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mcontext, R.string.nodataload, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setMainList() {

        mCategoriesRecyclerView.setHasFixedSize(true);
        //Set RecyclerView type according to intent value
        mCategoriesRecyclerView.setLayoutManager(new GridLayoutManager(mcontext, 1));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(100);
        itemAnimator.setRemoveDuration(100);
        mCategoriesRecyclerView.setItemAnimator(itemAnimator);
        List<Offer> categoriesModel = new ArrayList<Offer>();
        CompanyOfferAdapter adapters = new CompanyOfferAdapter(getBaseContext(), categoriesModel);
        mCategoriesRecyclerView.setAdapter(adapters);
        firebaseCode();
    }

    private void firebaseCode() {
        myDatabase = myApplication.getDatabaseReference();
        myPharmacyDatabase = myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_OFFERS).orderByChild("companyKeyId").
                equalTo(company_user_id);
//        Toast.makeText(mcontext, "list "+myPharmacyDatabase.getRef(), Toast.LENGTH_SHORT).show();
        myPharmacyDatabase.keepSynced(true);
        adapter = new CompanyOfferAdapter(getBaseContext(), null);
        recyclering(myPharmacyDatabase, getString(R.string.fi));

//        // Create an instance of GoogleAPIClient.
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            Intent startIntent = new Intent(CompanyOffersActivity.this, CompanyLoginActivity.class);
            startActivity(startIntent);
        }
        if (item.getItemId() == R.id.listbook) {
            Intent startIntent = new Intent(CompanyOffersActivity.this, CompanyBooking.class);
            startActivity(startIntent);
        }
        if (item.getItemId() == R.id.listbooktem) {
            Intent startIntent = new Intent(CompanyOffersActivity.this, OffersNotApprovedActivity.class);
            startActivity(startIntent);
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onStart() {
//        mGoogleApiClient.connect();
//        super.onStart();
//    }
//
//    @Override
//    public void onStop() {
//        mGoogleApiClient.disconnect();
//        super.onStop();
//    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}