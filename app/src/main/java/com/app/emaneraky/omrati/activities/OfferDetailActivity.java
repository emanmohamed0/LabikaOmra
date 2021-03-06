package com.app.emaneraky.omrati.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.app.emaneraky.omrati.ConstantsLabika;
import com.app.emaneraky.omrati.R;
import com.app.emaneraky.omrati.constans.Global;
import com.app.emaneraky.omrati.models.Booking;
import com.app.emaneraky.omrati.models.Favorite;
import com.app.emaneraky.omrati.models.Offer;
import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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

public class OfferDetailActivity extends AppCompatActivity
        implements OnFABMenuSelectedListener, OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int TAB_COUNT = 4;
    public static Offer mPharmacy;
    private static GoogleMap mMap;
    FABRevealMenu fabMenu;
    SliderLayout sliderImage;
    ProgressDialog progressDialog;
    DatabaseReference myDatabase;
    FirebaseAuth auth;
    MapView mapView;
    Location myLocation;
    private GoogleApiClient googleApiClient;
    ImageView imgOffer;
    String nameCompany, mUser_Id;
    TextView mTxtPlace, mTxtPrice, mTxtPriceBus, mTxtPricePlace, mTxtPriceTotal, mTxtHotel,
            mTxtFood, mTxtBus, mTxtSeat, txtPricetotalChair, mTxtTime, mTxtTimecheckin, descr, status, trans;
    String email, name, phoneNum;
    Booking booking;
    int numseat;
    String value, seat;
    TextView lbl_name;
    ImageView img_back;
    ArrayList<String> imgList;

    @Override
    public void onBackPressed() {
        if (fabMenu.isShowing())
            fabMenu.closeMenu();
        else {
            super.onBackPressed();
//            Intent intent = new Intent(OfferDetailActivity.this, OffersActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);

        nameCompany = getIntent().getStringExtra("nameCompany");
//        mUser_Id = getIntent().getStringExtra("mUser_Id");
        mUser_Id = Global.get_UserID(OfferDetailActivity.this, "mUser_Id");
        numseat = getIntent().getIntExtra("numseat", 1);
        value = getIntent().getStringExtra("value");
        seat = getIntent().getStringExtra("mNumSeat");

        imgList = new ArrayList<>();
        lbl_name = (TextView) findViewById(R.id.lbl_name);
        lbl_name.setText(nameCompany);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.GONE);
//        img_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

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
        sliderImage = (SliderLayout) findViewById(R.id.slider);
//        bannerSlider = (BannerSlider) findViewById(R.id.slider);
//        DemoSliderM();
        if (mPharmacy.getContentImagesList() != null) {
            if (mPharmacy.getContentImagesList().size() != 0) {
                imgList.addAll(mPharmacy.getContentImagesList());
                setUpViewPager(imgList);
            } else {
                imgList.add("https://firebasestorage.googleapis.com/v0/b/labika-omra.appspot.com/o/OfferImgs%2Frsz_21photo.jpg?alt=media&token=a70b1715-e43e-4de2-a280-a83b9bc3ced5");

                setUpViewPager(imgList);
            }
        } else {
            imgList.add("https://firebasestorage.googleapis.com/v0/b/labika-omra.appspot.com/o/OfferImgs%2Frsz_21photo.jpg?alt=media&token=a70b1715-e43e-4de2-a280-a83b9bc3ced5");
            setUpViewPager(imgList);
        }

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
        txtPricetotalChair = (TextView) findViewById(R.id.pricetotalChairoffer);

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

//        profilrImg = (CircleImageView) findViewById(R.id.profilrImg);

        mTxtPlace.setText(mPharmacy.getLocation());


        mTxtPrice.setText(getString(R.string.coin) + " " + mPharmacy.getPriceTotal());
        if (value.equals("value")) {
            mTxtPriceBus.setVisibility(View.GONE);
            mTxtPricePlace.setVisibility(View.GONE);
            mTxtPriceTotal.setText(getString(R.string.pricetotal) + " = " + mPharmacy.getPriceTotal() + " " + getString(R.string.coin));
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
                mTxtPricePlace.setText(getString(R.string.priceplace) + " = " + mPharmacy.getPricePlace() + " " + getString(R.string.coin));
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
                mTxtPriceBus.setText(getString(R.string.pricebus) + " = " + mPharmacy.getPriceBus() + " " + getString(R.string.coin));
                if (numseat == 0) {
                    numseat = 1;
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceTotal()))));
                } else {
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceTotal()))));
                }
            } else if (value.equals("transonly3")) {
                mTxtPriceBus.setVisibility(View.GONE);
                mTxtPricePlace.setVisibility(View.GONE);
                mTxtPriceTotal.setText(getString(R.string.pricetotal) + " = " + mPharmacy.getPriceTotal() + " " + getString(R.string.coin));
                if (numseat == 0) {
                    numseat = 1;
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceTotal()))));
                } else {
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceTotal()))));
                }
            } else {
                mTxtPriceBus.setText(getString(R.string.pricebus) + " = " + mPharmacy.getPriceBus() + " " + getString(R.string.coin));
                mTxtPricePlace.setVisibility(View.GONE);
                mTxtPriceTotal.setVisibility(View.GONE);
                if (numseat == 0) {
                    numseat = 1;
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceBus())) + " " + getString(R.string.coin)));

                } else {
                    txtPricetotalChair.setText(getString(R.string.pricetotalchair) + " = " + (numseat * (Integer.parseInt(mPharmacy.getPriceBus())) + " " + getString(R.string.coin)));
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
//            Glide.with(OfferDetailActivity.this).load(mPharmacy.getOfferImage()).into(profilrImg);

//            Picasso.with(com.apps.labikaomra.activities.OfferDetailActivity.this).load(mPharmacy.getOfferImage()).into(imgOffer);
//            Picasso.with(com.apps.labikaomra.activities.OfferDetailActivity.this).load(mPharmacy.getOfferImage()).into(profilrImg);

        }


        if ((googleApiClient == null)) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

//    private void DemoSliderM() {
//        for (int i = 0; i < mPharmacy.getContentImagesList().size(); i++) {
//            bannerSlider.addBanner(new RemoteBanner(mPharmacy.getContentImagesList().get(i)));
//        }
//        TextView mTxtName = (TextView) findViewById(R.id.hotelNameTxt);
//        Button mWordBtn = (Button) findViewById(R.id.about_button);
//        final View mWordTxt = findViewById(R.id.detailOffer);
//
//        mTxtName.setText(mPharmacy.getHotelName());
//        mWordBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mWordTxt.getVisibility() == View.VISIBLE)
//                    mWordTxt.setVisibility(View.GONE);
//                else
//                    mWordTxt.setVisibility(View.VISIBLE);
//            }
//        });
//    }

    private void setUpViewPager(ArrayList<String> avatar) {

        for (int i = 0; i < avatar.size(); i++) {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.description("").image(avatar.get(i)).setScaleType(BaseSliderView.ScaleType.Fit);

            sliderImage.addSlider(textSliderView);
        }
        if (avatar.size() > 1) {
            sliderImage.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
            sliderImage.startAutoCycle();
        }
        Button mWordBtn = (Button) findViewById(R.id.about_button);
        final View mWordTxt = findViewById(R.id.detailOffer);


        mWordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWordTxt.getVisibility() == View.VISIBLE)
                    mWordTxt.setVisibility(View.GONE);
                else
                    mWordTxt.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onMenuItemSelected(View view) {
        int id = (int) view.getTag();
        if (id == R.id.menu_book) {

            if (auth.getCurrentUser() != null) {
                if (seat == null) {
                    Intent booking = new Intent(OfferDetailActivity.this, DialogBooking.class);
                    booking.putExtra("CompanyKeyId", mPharmacy.getCompanyKeyId());
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

            }
        } else if (id == R.id.menu_favorite) {
            if (auth.getCurrentUser() != null) {
                addToFavorite();
            } else {

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

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                    Global.set_UserId(OfferDetailActivity.this, "mUser_Id", auth.getCurrentUser().getUid().toString());

                    Intent booking = new Intent(OfferDetailActivity.this, DialogBooking.class);
                    booking.putExtra("CompanyKeyId", mPharmacy.getCompanyKeyId());
                    booking.putExtra("numseat", numseat);
                    booking.putExtra("mNumSeat", edtchair.getText().toString());
                    booking.putExtra("KeyId", mPharmacy.getKeyId());
                    startActivity(booking);
                }

            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
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

    public void getLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback() {
                        @Override
                        public void onResult(@NonNull Result result) {
                            Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    int permissionLocation = ContextCompat.checkSelfPermission(OfferDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                        if (myLocation != null) {
                                            MarkerOptions currentUserLocation = new MarkerOptions();

                                            final LatLng currentUserLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                                            currentUserLocation.position(currentUserLatLng)
                                                    .title(getString(R.string.your_location));

                                            if (currentUserLatLng != null) {
                                                GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                                                        .from(new LatLng(mPharmacy.getLat(), mPharmacy.getlang()))
                                                        .to(currentUserLatLng)
                                                        .execute(new DirectionCallback() {
                                                            @Override
                                                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                                                if (direction.isOK()) {
                                                                    mMap.clear();
                                                                    Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                                                                    ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                                                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(getBaseContext(), directionPositionList, 3, Color.RED);
                                                                    mMap.addPolyline(polylineOptions);
                                                                    mMap.addMarker(new MarkerOptions().position(currentUserLatLng)
                                                                            .title(getString(R.string.your_location)));

                                                                    mMap.addMarker(new MarkerOptions().position(new LatLng(mPharmacy.getLat(), mPharmacy.getlang()))
                                                                            .title(getString(R.string.companyname) + nameCompany));

                                                                }
                                                            }

                                                            @Override
                                                            public void onDirectionFailure(Throwable t) {
                                                                Log.e("Direction :", "Failure :" + t.getLocalizedMessage());
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(OfferDetailActivity.this, Global.REQUEST_CHECKSETTING_GPS);
                                    } catch (IntentSender.SendIntentException e) {

                                    }
                                    break;

                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (userLocation != null) {
            MarkerOptions currentUserLocation = new MarkerOptions();

            final LatLng currentUserLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            currentUserLocation.position(currentUserLatLng)
                    .title(getString(R.string.your_location));

            if (currentUserLatLng != null) {
                GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                        .from(new LatLng(mPharmacy.getLat(), mPharmacy.getlang()))
                        .to(currentUserLatLng)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                if (direction.isOK()) {
                                    mMap.clear();
                                    Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                                    ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(getBaseContext(), directionPositionList, 3, Color.RED);
                                    mMap.addPolyline(polylineOptions);
                                    mMap.addMarker(new MarkerOptions().position(currentUserLatLng)
                                            .title(getString(R.string.your_location)));

                                    mMap.addMarker(new MarkerOptions().position(new LatLng(mPharmacy.getLat(), mPharmacy.getlang()))
                                            .title(getString(R.string.companyname) + nameCompany));

                                }
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
                        Toast.makeText(com.app.emaneraky.omrati.activities.OfferDetailActivity.this, "Hello " + edtText.getText() + " ! how are you?", Toast.LENGTH_LONG).show();
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
        progressDialog = new ProgressDialog(com.app.emaneraky.omrati.activities.OfferDetailActivity.this);
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
        progressDialog = new ProgressDialog(com.app.emaneraky.omrati.activities.OfferDetailActivity.this);
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

                Toast.makeText(com.app.emaneraky.omrati.activities.OfferDetailActivity.this, "Hello " + edtText.getText() + " ! how are you?", Toast.LENGTH_LONG).show();
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

    @Override
    public void onLocationChanged(Location location) {

    }
}
