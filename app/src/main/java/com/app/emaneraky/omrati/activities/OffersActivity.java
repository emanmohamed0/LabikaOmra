package com.app.emaneraky.omrati.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.emaneraky.omrati.Application_config.myApplication;
import com.app.emaneraky.omrati.ConstantsLabika;
import com.app.emaneraky.omrati.R;
import com.app.emaneraky.omrati.adapters.OfferAdapter;
import com.app.emaneraky.omrati.constans.Global;
import com.app.emaneraky.omrati.models.Company;
import com.app.emaneraky.omrati.models.Offer;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OffersActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static Query myPharmacyDatabase;
    public static Location mLastLocation;
    public static OfferAdapter adapter;
    public static Context mcontext;
    public static Long checkOutDate = 0l;
    public static Long checkInDate = 0l;
    public static String txtplace, txttrans, value_one, value_two, value_three;
    public static int numseat;
    private DatabaseReference myDatabase, mDataCompany;
    private GoogleApiClient mGoogleApiClient;
    static List<Offer> clientList;
    static RecyclerView mCategoriesRecyclerView;
    static String mUser_Id;
    List<String> companyList;
    int numseatback;
    static List<String> priceList;
    String value;
    Date checkIn, checkOut, checkOutoffer, checkInoffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.title_activity_offers));
        mcontext = getBaseContext();
        mUser_Id= Global.get_UserID(OffersActivity.this,"mUser_Id");
//        mUser_Id = getIntent().getStringExtra("mUser_Id");
        numseatback = getIntent().getIntExtra("numseat", 1);

        mDataCompany = FirebaseDatabase.getInstance().getReference().child("company");

        companyList = new ArrayList<>();

        mDataCompany.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Company company = postSnapshot.getValue(Company.class);
                    company.getFirstName();
//                    Toast.makeText(SearchCompanyActivity.this, company + "", Toast.LENGTH_SHORT).show();
                    companyList.add(company.getFirstName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCategoriesRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_company_offer);
        setMainList();
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    public void recyclering(Query qmyDatabase, final String search) {
        qmyDatabase.keepSynced(true);

        qmyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                clientList = new ArrayList<Offer>();
                priceList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d");

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Offer offer = postSnapshot.getValue(Offer.class);
                    priceList.add(offer.getPriceTotal());
                    try {
                        checkIn = sdf.parse(getDate(checkInDate, "EEE, MMM d"));
                        checkOut = sdf.parse(getDate(checkOutDate, "EEE, MMM d"));
                        checkInoffer = sdf.parse(getDate(offer.getStartDay(), "EEE, MMM d"));
                        checkOutoffer = sdf.parse(getDate(offer.getBackDay(), "EEE, MMM d"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if ((txttrans == getString(R.string.transonly))) {
                        value = "transonly";
                        if (checkOutDate != 0l && checkInDate != 01) {

                            if ((checkIn.compareTo(checkInoffer) == 0) && ((checkOut.compareTo(checkOutoffer) == 0))
                                    || (offer.getValue_twotrans() == value_two)
                                    || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                                System.out.println("Date1 is equal to Date2");
                            } else if ((checkIn.compareTo(checkInoffer) < 0) && (checkOut.compareTo(checkOutoffer) > 0)
                                    || (offer.getValue_twotrans() == value_two) || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                            } else if ((checkIn.compareTo(checkInoffer) < 0) && (checkOut.compareTo(checkOutoffer) == 0)
                                    || (offer.getValue_twotrans() == value_two) || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                            } else if ((checkIn.compareTo(checkInoffer) == 0) && (checkOut.compareTo(checkOutoffer) > 0)
                                    || (offer.getValue_twotrans() == value_two) || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                            }

                        }
                    } else if ((txttrans == getString(R.string.transonly2))) {
                        value = "transonly2";

                        if (checkOutDate != 0l && checkInDate != 01) {
                            if ((checkIn.compareTo(checkInoffer) == 0) && ((checkOut.compareTo(checkOutoffer) == 0))
                                    || (offer.getValue_twotrans() == value_two)
                                    || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                                System.out.println("Date1 is equal to Date2");
                            } else if ((checkIn.compareTo(checkInoffer) < 0) && (checkOut.compareTo(checkOutoffer) > 0)
                                    || (offer.getValue_twotrans() == value_two) || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                            } else if ((checkIn.compareTo(checkInoffer) < 0) && (checkOut.compareTo(checkOutoffer) == 0)
                                    || (offer.getValue_twotrans() == value_two) || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                            } else if ((checkIn.compareTo(checkInoffer) == 0) && (checkOut.compareTo(checkOutoffer) > 0)
                                    || (offer.getValue_twotrans() == value_two) || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                            }

                        }
                    } else if ((txttrans == getString(R.string.transonly3))) {
                        value = "transonly3";

                        if (checkOutDate != 0l && checkInDate != 01) {

                            if ((checkIn.compareTo(checkInoffer) == 0) && ((checkOut.compareTo(checkOutoffer) == 0))
                                    || (offer.getValue_twotrans() == value_two)
                                    || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                                System.out.println("Date1 is equal to Date2");
                            } else if ((checkIn.compareTo(checkInoffer) < 0) && (checkOut.compareTo(checkOutoffer) > 0)
                                    || (offer.getValue_twotrans() == value_two) || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                            } else if ((checkIn.compareTo(checkInoffer) < 0) && (checkOut.compareTo(checkOutoffer) == 0)
                                    || (offer.getValue_twotrans() == value_two) || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                            } else if ((checkIn.compareTo(checkInoffer) == 0) && (checkOut.compareTo(checkOutoffer) > 0)
                                    || (offer.getValue_twotrans() == value_two) || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                            }
                        }
                    } else {
                        value = "false";

                        if (checkOutDate != 0l && checkInDate != 01) {
                            if ((checkIn.compareTo(checkInoffer) == 0) && ((checkOut.compareTo(checkOutoffer) == 0))
                                    || (offer.getValue_twotrans() == value_two)
                                    || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                                System.out.println("Date1 is equal to Date2");
                            } else if ((checkIn.compareTo(checkInoffer) < 0) && (checkOut.compareTo(checkOutoffer) > 0)
                                    || (offer.getValue_twotrans() == value_two) || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                            } else if ((checkIn.compareTo(checkInoffer) < 0) && (checkOut.compareTo(checkOutoffer) == 0)
                                    || (offer.getValue_twotrans() == value_two) || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                            } else if ((checkIn.compareTo(checkInoffer) == 0) && (checkOut.compareTo(checkOutoffer) > 0)
                                    || (offer.getValue_twotrans() == value_two) || (offer.getValue_threestatus() == value_three)) {
                                clientList.add(offer);
                            }
                        }
                    }

                }
                if (clientList.size() == 0) {
                    createDialog();
                }
                adapter = new OfferAdapter(mcontext, clientList, mUser_Id, numseatback, value);
                mCategoriesRecyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(mcontext, R.string.nodataload, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(OffersActivity.this).create();
        alertDialog.setTitle(getString(R.string.alert));
        alertDialog.setMessage(getString(R.string.dialog_offer));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.highprice) {
            Collections.sort(clientList);
            adapter = new OfferAdapter(mcontext, clientList, mUser_Id, numseatback, value);
            mCategoriesRecyclerView.setAdapter(adapter);

        } else if (item.getItemId() == R.id.lowprice) {
            Collections.sort(clientList);
            Collections.reverse(clientList);

            adapter.notifyDataSetChanged();
            adapter = new OfferAdapter(mcontext, clientList, mUser_Id, numseatback, value);
            mCategoriesRecyclerView.setAdapter(adapter);

        }
        return super.onOptionsItemSelected(item);
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
        OfferAdapter adapters = new OfferAdapter(getBaseContext(), categoriesModel, mUser_Id, numseatback, value);
        mCategoriesRecyclerView.setAdapter(adapters);
        firebaseCode();
    }

    private void firebaseCode() {
        myDatabase = myApplication.getDatabaseReference();
        myDatabase.keepSynced(true);
        myPharmacyDatabase = myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_OFFERS);
//        Toast.makeText(mcontext, "list "+myPharmacyDatabase.getRef(), Toast.LENGTH_SHORT).show();
        myPharmacyDatabase.keepSynced(true);
        adapter = new OfferAdapter(getBaseContext(), null, mUser_Id, numseatback, value);
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
