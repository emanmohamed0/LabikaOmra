package com.apps.labikaomra.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.apps.labikaomra.models.Booking;
import com.apps.labikaomra.models.Favorite;
import com.apps.labikaomra.models.Offer;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;

public class OfferDetailActivity extends AppCompatActivity
        implements OnFABMenuSelectedListener
        , OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int TAB_COUNT = 4;
    public static Offer mPharmacy;
    private static GoogleMap mMap;
    FABRevealMenu fabMenu;
    BannerSlider bannerSlider;
    ProgressDialog progressDialog;
    DatabaseReference myDatabase;
    FirebaseAuth auth;
    MapView mapView;
    private GoogleApiClient googleApiClient;
    CircleImageView profilrImg;
    ImageView imgOffer;
    String nameCompany, mUser_Id;
    TextView mTxtPlace, mTxtPrice, mTxtPriceBus, mTxtPricePlace, mTxtPriceTotal, mTxtHotel,
            mTxtFood, mTxtBus, mTxtSeat, txtPricetotalChair, mTxtTime, mTxtTimecheckin, descr, status, trans;
    String email, name, phoneNum;
    Booking booking;
    int numseat, seatback;
    String value, seat;

    @Override
    public void onBackPressed() {
        if (fabMenu.isShowing())
            fabMenu.closeMenu();
        else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);

        nameCompany = getIntent().getStringExtra("nameCompany");
        mUser_Id = getIntent().getStringExtra("mUser_Id");
        numseat = getIntent().getIntExtra("numseat", 1);
        value = getIntent().getStringExtra("value");
        seat = getIntent().getStringExtra("mNumSeat");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(nameCompany);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FABRevealMenu fabMenu = (FABRevealMenu) findViewById(R.id.fabMenu);

        try {
            if (fab != null && fabMenu != null) {
                setFabMenu(fabMenu);
                fabMenu.bindAncherView(fab);
                fabMenu.setOnFABMenuSelectedListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bannerSlider = (BannerSlider) findViewById(R.id.slider);
        DemoSliderM();
        myDatabase = FirebaseDatabase.getInstance().getReference();
        myDatabase.keepSynced(true);
        auth = FirebaseAuth.getInstance();

        mapView = (MapView) findViewById(R.id.mapview);
//        mapView.onCreate(savedInstanceState);
        try {
            // Temporary fix for crash issue
            mapView.onCreate(savedInstanceState);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);

        imgOffer = (ImageView) findViewById(R.id.imageCompany);
        mTxtPlace = (TextView) findViewById(R.id.locationTxt);

        mTxtPrice = (TextView) findViewById(R.id.price);
        mTxtPriceBus = (TextView) findViewById(R.id.pricebus);
        mTxtPricePlace = (TextView) findViewById(R.id.priceplace);
        mTxtPriceTotal = (TextView) findViewById(R.id.pricetotal);
        txtPricetotalChair = (TextView) findViewById(R.id.pricetotalChair);

        mTxtSeat = (TextView) findViewById(R.id.seat);
        mTxtHotel = (TextView) findViewById(R.id.hotels);
        mTxtFood = (TextView) findViewById(R.id.food);
        mTxtBus = (TextView) findViewById(R.id.bus);
        mTxtTime = (TextView) findViewById(R.id.timecheckout);
        mTxtTimecheckin = (TextView) findViewById(R.id.timecheckin);
        TextView name = (TextView) findViewById(R.id.dispaly_nameCompany);
        descr = (TextView) findViewById(R.id.descr);
        status = (TextView) findViewById(R.id.status);
        trans = (TextView) findViewById(R.id.trans);

        profilrImg = (CircleImageView) findViewById(R.id.profilrImg);

        mTxtPlace.setText(mPharmacy.getLocation());


        mTxtPrice.setText("Ryial " + mPharmacy.getPriceTotal());
        if (value.equals("value")) {
            mTxtPriceBus.setVisibility(View.GONE);
            mTxtPricePlace.setVisibility(View.GONE);
            mTxtPriceTotal.setText(getString(R.string.pricetotal) + " = " + mPharmacy.getPriceTotal() + " Ryial ");
            if (numseat == 0) {
                numseat = 1;
                txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceTotal()))));
            } else {
                txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceTotal()))));
            }
        } else {
            if (value.equals("transonly")) {
                mTxtPriceBus.setVisibility(View.GONE);
                mTxtPriceTotal.setVisibility(View.GONE);
                mTxtPricePlace.setText(getString(R.string.priceplace) + " = " + mPharmacy.getPricePlace() + " Ryial ");
//                mTxtPriceTotal.setText(getString(R.string.pricetotal) + " = " + mPharmacy.getPriceTotal() + " Ryial ");
                if (numseat == 0) {
                    numseat = 1;
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceTotal()))));
                } else {
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceTotal()))));
                }
            } else if (value.equals("transonly2")) {
                mTxtPricePlace.setVisibility(View.GONE);
                mTxtPriceTotal.setVisibility(View.GONE);
                mTxtPriceBus.setText(getString(R.string.pricebus) + " = " + mPharmacy.getPriceBus() + " Ryial ");
                if (numseat == 0) {
                    numseat = 1;
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceTotal()))));
                } else {
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceTotal()))));
                }
            } else if (value.equals("transonly3")) {
                mTxtPriceBus.setVisibility(View.GONE);
                mTxtPricePlace.setVisibility(View.GONE);
                mTxtPriceTotal.setText(getString(R.string.pricetotal) + " = " + mPharmacy.getPriceTotal() + " Ryial ");
                if (numseat == 0) {
                    numseat = 1;
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceTotal()))));
                } else {
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceTotal()))));
                }
            } else {
                mTxtPriceBus.setText(getString(R.string.pricebus) + " = " + mPharmacy.getPriceBus() + " Ryial ");
                mTxtPricePlace.setVisibility(View.GONE);
                mTxtPriceTotal.setVisibility(View.GONE);
                if (numseat == 0) {
                    numseat = 1;
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceBus())) + " Ryial "));

                } else {
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceBus())) + " Ryial "));
                }
            }

        }

        mTxtHotel.setText(mPharmacy.getHotelName());

        if (mPharmacy.getDeals().equals("")) {
            mTxtFood.setVisibility(View.GONE);

        } else {
            mTxtFood.setText(mPharmacy.getDeals());

        }
        mTxtTime = (TextView) findViewById(R.id.timecheckout);
        mTxtTimecheckin = (TextView) findViewById(R.id.timecheckin);
        mTxtBus.setText(mPharmacy.getDestLevel());
        mTxtSeat.setText(getString(R.string.chairs_count_hint) + " " + mPharmacy.getNumOfChairs());
        status.setText(mPharmacy.getValue_onehouse());
        trans.setText(mPharmacy.getValue_twotrans());
        name.setText(nameCompany);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Raleway-ExtraBold.ttf");
        descr.setTypeface(face);

        mTxtTime.setText(getString(R.string.check_out) + "\n" + getDate(mPharmacy.getBackDay(), "EEE, MMM d"));
        mTxtTimecheckin.setText(getString(R.string.check_in) + "\n" + getDate(mPharmacy.getStartDay(), "EEE, MMM d"));
//        Picasso.with(CompanyOfferDetailActivity.this).load(mPharmacy.getOfferImage()).into(profilrImg);

        if (mPharmacy.getOfferImage() != null) {
            Glide.with(OfferDetailActivity.this).load(mPharmacy.getOfferImage()).into(imgOffer);
            Glide.with(OfferDetailActivity.this).load(mPharmacy.getOfferImage()).into(profilrImg);

//            Picasso.with(com.apps.labikaomra.activities.OfferDetailActivity.this).load(mPharmacy.getOfferImage()).into(imgOffer);
//            Picasso.with(com.apps.labikaomra.activities.OfferDetailActivity.this).load(mPharmacy.getOfferImage()).into(profilrImg);

        }

//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(this, R.string.gps_enable, Toast.LENGTH_SHORT).show();
//        } else {
//            showGPSDisabledAlertToUser();
//        }
        if ((googleApiClient == null)) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void DemoSliderM() {
        //add banner using image url

        for (int i = 0; i < mPharmacy.getContentImagesList().size(); i++) {
            bannerSlider.addBanner(new RemoteBanner(mPharmacy.getContentImagesList().get(i)));
        }
//        RatingBar mRatingBar = (RatingBar) findViewById(R.id.profile_pharmacy_rate);
        TextView mTxtName = (TextView) findViewById(R.id.hotelNameTxt);
        Button mWordBtn = (Button) findViewById(R.id.about_button);
        final View mWordTxt = findViewById(R.id.detailOffer);

        mTxtName.setText(mPharmacy.getHotelName());

//        mRatingBar.setRating(Float.parseFloat(String.valueOf(mPharmacy.getRate() / mPharmacy.getNumOfRaters())));
        mWordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWordTxt.getVisibility() == View.VISIBLE)
                    mWordTxt.setVisibility(View.GONE);
                else
                    mWordTxt.setVisibility(View.VISIBLE);
            }
        });
//        mWordTxt.setText(mPharmacy.getDetailWord());


    }


    @Override
    public void onMenuItemSelected(View view) {
        int id = (int) view.getTag();
        if (id == R.id.menu_book) {

            if (auth.getCurrentUser() != null) {
                if (seat == null) {
                    Intent booking = new Intent(OfferDetailActivity.this, DialogBooking.class);
                    booking.putExtra("CompanyKeyId", mPharmacy.getCompanyKeyId());
                    booking.putExtra("mUser_Id", mUser_Id);
                    booking.putExtra("numseat", numseat);
                    booking.putExtra("KeyId", mPharmacy.getKeyId());
                    startActivity(booking);
                } else {
                    if (seat.equals("1")) {
                        showSeatDialog();
                    }
                }

            } else {
                Intent logingoogle = new Intent(OfferDetailActivity.this, UserLoginActivity.class);
                logingoogle.putExtra("CompanyKeyId", mPharmacy.getCompanyKeyId());
                logingoogle.putExtra("KeyId", mPharmacy.getKeyId());
                logingoogle.putExtra("mNumSeat", seat);
                startActivity(logingoogle);
//                startActivityForResult(new Intent(OfferDetailActivity.this, UserLoginActivity.class), 1);

            }
        } else if (id == R.id.menu_favorite) {
            if (auth.getCurrentUser() != null) {
                addToFavorite();
            } else {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Toast.makeText(getBaseContext(), "You must login before submit", Toast.LENGTH_SHORT).show();
                Intent intentLogin = new Intent(OfferDetailActivity.this, UserLoginActivity.class);
                startActivity(intentLogin);

            }

        }
    }

    private void addToFavorite() {
        progressDialog = new ProgressDialog(OfferDetailActivity.this);
        progressDialog.setMessage(getString(R.string.adding_fav));
        progressDialog.setCancelable(false);
        progressDialog.show();

        HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put(getString(R.string.timestamp), ServerValue.TIMESTAMP);

        DatabaseReference client_database = myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_FAVORITE)
                .child(auth.getCurrentUser().getUid());

        String clientId = mPharmacy.getKeyId();
        Favorite favorite = new Favorite(mPharmacy.getKeyId(), timestampJoined);
        client_database.child(clientId).setValue(favorite)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), R.string.add_fav, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(getBaseContext(), R.string.error_notadd, Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure: Not add to fav: " + e.getMessage());
                Toast.makeText(getBaseContext(), R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    public FABRevealMenu getFabMenu() {
        return fabMenu;
    }

    public void setFabMenu(FABRevealMenu fabMenu) {
        this.fabMenu = fabMenu;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mPharmacy.getLat(), mPharmacy.getlang()), 14);
        map.animateCamera(cameraUpdate);
        map.addMarker(new MarkerOptions()
                .position(new LatLng(mPharmacy.getLat(), mPharmacy.getlang()))
                .title("Name Company: " + nameCompany))
                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap = map;
    }

    public void showSeatDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.seat_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText edtchair = (EditText) dialogView.findViewById(R.id.chairField);

        dialogBuilder.setTitle(getString(R.string.enterseat));
        dialogBuilder.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edtchair.getText().toString().isEmpty()) {
                    Toast.makeText(OfferDetailActivity.this, "Please Enter All Data ^_^ ", Toast.LENGTH_LONG).show();
                } else {
//                    seatback = Integer.parseInt(edtchair.getText().toString());
                    Intent booking = new Intent(OfferDetailActivity.this, DialogBooking.class);
                    booking.putExtra("CompanyKeyId", mPharmacy.getCompanyKeyId());
                    booking.putExtra("mUser_Id", mUser_Id);
                    booking.putExtra("numseat", numseat);
                    booking.putExtra("mNumSeat", edtchair.getText().toString());
                    booking.putExtra("KeyId", mPharmacy.getKeyId());
                    startActivity(booking);
                }

                //do something with edt.getText().toString();
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (userLocation != null) {
            MarkerOptions currentUserLocation = new MarkerOptions();

            final LatLng currentUserLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            currentUserLocation.position(currentUserLatLng)
                    .title(getString(R.string.your_location));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLng, 14));

            if (currentUserLatLng != null) {
                GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                        .from(new LatLng(mPharmacy.getLat(), mPharmacy.getlang()))
                        .to(currentUserLatLng)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                drawPath(new LatLng(mPharmacy.getLat(), mPharmacy.getlang()), currentUserLatLng);
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                Log.e("Direction :", "Failure :" + t.getLocalizedMessage());
                            }
                        });
            }
        }
    }

    public void drawPath(final LatLng currentLocation, final LatLng placeLocation) {
        final LatLng latLng = new LatLng(currentLocation.latitude, currentLocation.longitude);
        GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                .from(latLng)
                .to(placeLocation)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(OfferDetailActivity.this, directionPositionList, 2, Color.BLUE);
                            mMap.addPolyline(polylineOptions);
                            mMap.addMarker(new MarkerOptions().position(placeLocation)
                                    .title("Current_Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mPharmacy.getLat(), mPharmacy.getlang())));
                            Log.e("Direction :", "Direction Seccuss");
                        } else {
                            Log.e("Direction :", direction.getErrorMessage());
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.e("Direction :", "Failure :" + t.getLocalizedMessage());
                        Toast.makeText(OfferDetailActivity.this, "Check_Network", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onConnected(null);
        } else {
            Toast.makeText(getBaseContext(), R.string.no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                final String name = data.getStringExtra("name");
                final String email = data.getStringExtra("email");
                final EditText edtText = new EditText(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Prompt dialog demo !");
                builder.setMessage("What is your name?");
                builder.setCancelable(false);
                builder.setView(edtText);
                builder.setNeutralButton("Prompt", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNum = edtText.getText().toString();
                        Toast.makeText(com.apps.labikaomra.activities.OfferDetailActivity.this, "Hello " + edtText.getText() + " ! how are you?", Toast.LENGTH_LONG).show();
                        completeBooking(name, email, phoneNum);
                    }
                });
                builder.show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

    }//onActivityResult

    private void completeBooking(String name, String email, String phoneNum) {
        progressDialog = new ProgressDialog(com.apps.labikaomra.activities.OfferDetailActivity.this);
        progressDialog.setMessage(getString(R.string.adding_book));
        progressDialog.setCancelable(false);
        progressDialog.show();
        HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put(getString(R.string.timestamp), ServerValue.TIMESTAMP);

        DatabaseReference client_database = myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_BOOKING).child(auth.getCurrentUser().getUid());
        String clientId = mPharmacy.getKeyId();
        Booking booking = new Booking(mPharmacy.getKeyId(), name, email, phoneNum, timestampJoined);

        client_database.child(clientId).setValue(booking)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), R.string.add_book, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), R.string.error_notadd, Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure: Not add to fav: " + e.getMessage());
                Toast.makeText(getBaseContext(), R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void completeBooking() {
        progressDialog = new ProgressDialog(com.apps.labikaomra.activities.OfferDetailActivity.this);
        progressDialog.setMessage(getString(R.string.adding_book));
        progressDialog.setCancelable(false);
        progressDialog.show();


        final HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put(getString(R.string.timestamp), ServerValue.TIMESTAMP);

        final DatabaseReference client_database = myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_BOOKING).child(mUser_Id);
        final String clientId = mPharmacy.getKeyId();
        final EditText edtText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Prompt dialog demo !");
        builder.setMessage("What is your name?");
        builder.setCancelable(false);
        builder.setView(edtText);
        builder.setNeutralButton("Prompt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                phoneNum = edtText.getText().toString();
                myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_USERS).child(mUser_Id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        email = dataSnapshot.child("email").getValue().toString();
                        name = dataSnapshot.child("fullName").getValue().toString();

                        booking = new Booking(mPharmacy.getKeyId(), name, email, phoneNum, timestampJoined);
                        client_database.child(clientId).setValue(booking)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            bookingCompany(booking);
                                            Toast.makeText(getBaseContext(), R.string.add_book, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getBaseContext(), R.string.error_notadd, Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "onFailure: Not add to fav: " + e.getMessage());
                                Toast.makeText(getBaseContext(), R.string.error, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Toast.makeText(com.apps.labikaomra.activities.OfferDetailActivity.this, "Hello " + edtText.getText() + " ! how are you?", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();

//...............................................................................................................//

    }

    public void bookingCompany(Booking booking) {
        DatabaseReference client_database1 = myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_BOOKING_COMPANY).child(mPharmacy.getCompanyKeyId());
        String clientId1 = mPharmacy.getKeyId();

        client_database1.child(clientId1).setValue(booking)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            Toast.makeText(getBaseContext(), R.string.add_book, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), R.string.error_notadd, Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure: Not add to fav: " + e.getMessage());
                Toast.makeText(getBaseContext(), R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
