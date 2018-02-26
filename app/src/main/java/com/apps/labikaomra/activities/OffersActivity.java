package com.apps.labikaomra.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.apps.labikaomra.Application_config.myApplication;
import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.apps.labikaomra.adapters.OfferAdapter;
import com.apps.labikaomra.models.Offer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OffersActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static Query myPharmacyDatabase;
    public static Location mLastLocation;
    public static OfferAdapter adapter;
    public static Context mcontext;
    public static Long checkOutDate = 0l ;
    public static Long  checkInDate = 0l;
    public static String txtbus , txthotel;
    public static int numseat;
    private DatabaseReference myDatabase;
    private GoogleApiClient mGoogleApiClient;
    static List<Offer> clientList;
    static RecyclerView mCategoriesRecyclerView;
    static String mUser_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mcontext = getBaseContext();

        mUser_Id = getIntent().getStringExtra("mUser_Id");

        mCategoriesRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_company_offer);
        setMainList();
    }


    public static void recyclering(Query qmyDatabase, final String search) {
        qmyDatabase.keepSynced(true);

        qmyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                HashMap<String, Doctor> results = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Doctor>>() {
//                });
//                if (results != null) {
//                Toast.makeText(mcontext, "data"+dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                clientList = new ArrayList<Offer>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Toast.makeText(mcontext, "count"+postSnapshot.getValue(), Toast.LENGTH_LONG).show();
                    Offer offer = postSnapshot.getValue(Offer.class);
                    if (checkOutDate != 0l && checkInDate !=01 ) {
                        if (offer.getBackDay() <= checkOutDate && checkInDate <= offer.getStartDay()
                                || offer.getBusLevel() ==txtbus || offer.getHotelLevel()== txthotel || offer.getNumOfChairs()== numseat) {
                            clientList.add(offer);
                            Toast.makeText(mcontext, "list count" + clientList.size(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mcontext, "No Data Match this Date", Toast.LENGTH_LONG).show();

                        }

                    } else {
//                        if (pharmacy.getName().contains(search) || pharmacy.getPhoneNumber().contains(search) ||
//                                pharmacy.getStreet().contains(search) ||
//                                pharmacy.getLocation().contains(search) ||
//                                pharmacy.getLocationPlace().contains(search))
//                        clientList.add(pharmacy);
                    }
                }
                adapter = new OfferAdapter(mcontext, clientList, mUser_Id);
                mCategoriesRecyclerView.setAdapter(adapter);
//                } else {
//                    Toast.makeText(mcontext, R.string.nodataload, Toast.LENGTH_SHORT).show();
//                }
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
        OfferAdapter adapters = new OfferAdapter(getBaseContext(), categoriesModel , mUser_Id);
        mCategoriesRecyclerView.setAdapter(adapters);
        firebaseCode();
    }

    private void firebaseCode() {
        myDatabase = myApplication.getDatabaseReference();
        myDatabase.keepSynced(true);
        myPharmacyDatabase = myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_OFFERS);
//        Toast.makeText(mcontext, "list "+myPharmacyDatabase.getRef(), Toast.LENGTH_SHORT).show();
        myPharmacyDatabase.keepSynced(true);
        adapter = new OfferAdapter(getBaseContext(), null,mUser_Id);
        recyclering(myPharmacyDatabase, getString(R.string.fi));

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

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
