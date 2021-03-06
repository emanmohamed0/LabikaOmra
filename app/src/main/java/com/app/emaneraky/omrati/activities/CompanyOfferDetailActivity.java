package com.app.emaneraky.omrati.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.app.emaneraky.omrati.models.Company;
import com.app.emaneraky.omrati.models.Offer;
import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
//import ss.com.bannerslider.banners.RemoteBanner;
//import ss.com.bannerslider.views.BannerSlider;

import static android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;

public class CompanyOfferDetailActivity extends AppCompatActivity
        implements OnFABMenuSelectedListener
        , OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int TAB_COUNT = 4;
    public static Offer mPharmacy;
    public static Company mCompany;
    ImageView imgOffer;
    private static GoogleMap mMap;
    FABRevealMenu fabMenu;
    //    BannerSlider bannerSlider;
    ProgressDialog progressDialog;
    DatabaseReference myDatabase;
    FirebaseAuth auth;
    MapView mapView;
    private GoogleApiClient googleApiClient;
    //    CircleImageView profilrImg;
    TextView lbl_name;
    ImageView img_back;
    TextView mTxtPlace, mTxtPrice, mTxtPriceBus, mTxtPricePlace, mTxtPriceTotal, mTxtHotel, mTxtFood, mTxtBus, mTxtSeat, mTxtTime, mTxtTimecheckin, descr, status, trans;
    String nameCompany;
    SliderLayout sliderImage;
    ArrayList<String> imgList;
//    @Override
//    public void onBackPressed() {
////        if (fabMenu.isShowing())
////            fabMenu.closeMenu();
////        else {
//        super.onBackPressed();
//            Intent intent = new Intent(CompanyOfferDetailActivity.this, CompanyOffersActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
////        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.animation1, R.anim.animation2);
        setContentView(R.layout.activity_company_offer_detail);
        imgList = new ArrayList<>();
        nameCompany = getIntent().getStringExtra("nameCompany");

        lbl_name = (TextView) findViewById(R.id.lbl_name);
        lbl_name.setText(nameCompany);
        img_back = (ImageView) findViewById(R.id.img_back);

        img_back.setVisibility(View.GONE);

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
        imgOffer = (ImageView) findViewById(R.id.imageCompany);
        mTxtPlace = (TextView) findViewById(R.id.locationTxt);

        mTxtPrice = (TextView) findViewById(R.id.price);

        mTxtPriceBus = (TextView) findViewById(R.id.pricebus);
        mTxtPricePlace = (TextView) findViewById(R.id.priceplace);
        mTxtPriceTotal = (TextView) findViewById(R.id.pricetotal);

        mTxtHotel = (TextView) findViewById(R.id.hotels);
        mTxtFood = (TextView) findViewById(R.id.food);
        mTxtBus = (TextView) findViewById(R.id.bus);
        mTxtSeat = (TextView) findViewById(R.id.seat);
        mTxtTime = (TextView) findViewById(R.id.timecheckout);
        mTxtTimecheckin = (TextView) findViewById(R.id.timecheckin);
        descr = (TextView) findViewById(R.id.descr);
        TextView name = (TextView) findViewById(R.id.dispaly_nameCompany);
//        profilrImg = (CircleImageView) findViewById(R.id.profilrImg);
        status = (TextView) findViewById(R.id.status);
        trans = (TextView) findViewById(R.id.trans);

        mTxtPlace.setText(mPharmacy.getLocation());

        status.setText(mPharmacy.getValue_onehouse());
        trans.setText(mPharmacy.getValue_twotrans());


        mTxtPrice.setText(getString(R.string.coin) + mPharmacy.getPriceTotal());
        mTxtPriceBus.setText(getString(R.string.pricebus) + " = " + mPharmacy.getPriceBus() + " " + getString(R.string.coin));
        mTxtPricePlace.setText(getString(R.string.priceplace) + " = " + mPharmacy.getPricePlace() + " " + getString(R.string.coin));
        mTxtPriceTotal.setText(getString(R.string.pricetotal) + " = " + mPharmacy.getPriceTotal() + " " + getString(R.string.coin));


        mTxtHotel.setText(mPharmacy.getHotelName());
        if (mPharmacy.getDeals().equals("")) {
            mTxtFood.setVisibility(View.GONE);
        } else {
            mTxtFood.setText(mPharmacy.getDeals());

        }
        mTxtBus.setText(mPharmacy.getDestLevel());
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Raleway-ExtraBold.ttf");
        descr.setTypeface(face);

        mTxtTime.setText(getString(R.string.check_out) + "\n" + getDate(mPharmacy.getBackDay(), "EEE, MMM d"));
        mTxtTimecheckin.setText(getString(R.string.check_in) + "\n" + getDate(mPharmacy.getStartDay(), "EEE, MMM d"));
        mTxtSeat.setText(getString(R.string.chairs_count_hint) + " " + mPharmacy.getNumOfChairs());

        name.setText(nameCompany);

        if (mPharmacy.getOfferImage() != null) {
//            Glide.with(CompanyOfferDetailActivity.this).load(mPharmacy.getOfferImage()).into(profilrImg);
            Glide.with(CompanyOfferDetailActivity.this).load(mPharmacy.getOfferImage()).into(imgOffer);
//            Picasso.with(CompanyOfferDetailActivity.this).load(mPharmacy.getOfferImage()).into(profilrImg);
//            Picasso.with(CompanyOfferDetailActivity.this).load(mPharmacy.getOfferImage()).into(imgOffer);
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
        }else {
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
        TextView mTxtPlace = (TextView) findViewById(R.id.locationTxt);
        mTxtPlace.setText(mPharmacy.getLocation());
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

//    private void DemoSliderM() {
//        //add banner using image url
//        for (int i = 0; i < mPharmacy.getContentImagesList().size(); i++) {
//            bannerSlider.addBanner(new RemoteBanner(mPharmacy.getContentImagesList().get(i)));
//        }
////        RatingBar mRatingBar = (RatingBar) findViewById(R.id.profile_pharmacy_rate);
//        TextView mTxtName = (TextView) findViewById(R.id.hotelNameTxt);
//        Button mWordBtn = (Button) findViewById(R.id.about_button);
//        final View mWordTxt = findViewById(R.id.detailOffer);
//
//        mTxtName.setText(nameCompany);
////        mRatingBar.setRating(Float.parseFloat(String.valueOf(mPharmacy.getRate() / mPharmacy.getNumOfRaters())));
//        mWordBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mWordTxt.getVisibility() == View.VISIBLE)
//                    mWordTxt.setVisibility(View.GONE);
//                else
//                    mWordTxt.setVisibility(View.VISIBLE);
//            }
//        });
////        mWordTxt.setText(mPharmacy.getDetailWord());
//
//
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
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        finishActivity(FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        Intent back = new Intent(CompanyOfferDetailActivity.this, CompanyOffersActivity.class);
        startActivity(back);
        finish();
    }

    @Override
    public void onMenuItemSelected(View view) {
        int id = (int) view.getTag();
        if (id == R.id.menu_delet) {
            AlertDialog diaBox = AskOption();
            diaBox.show();
//            RatePharmacyDialogFragment.activity = PharmacyProfileActivity.this;
//            RatePharmacyDialogFragment.mPharmacy = mPharmacy;
//            DialogFragment dialog = RatePharmacyDialogFragment.newInstance();
//            dialog.show(PharmacyProfileActivity.this.getFragmentManager(), "RatePharmacyDialogFragment");
        }
    }

    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.ic_delete_black_24dp)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code

                        FirebaseDatabase.getInstance().getReference()
                                .child(ConstantsLabika.FIREBASE_LOCATION_OFFERS).child(mPharmacy.getKeyId()).removeValue();
                        Toast.makeText(CompanyOfferDetailActivity.this, "This Offer Deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                })


                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;
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
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mPharmacy.getLat(), mPharmacy.getlang()), 18);
        map.animateCamera(cameraUpdate);
        map.addMarker(new MarkerOptions()
                .position(new LatLng(mPharmacy.getLat(), mPharmacy.getlang()))
                .title(nameCompany));
        mMap = map;
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
            return;
        }

        Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (userLocation != null) {
            MarkerOptions currentUserLocation = new MarkerOptions();

            final LatLng currentUserLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            currentUserLocation.position(currentUserLatLng)
                    .title(getString(R.string.your_location));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLng, 16));

            if (currentUserLatLng != null) {
                GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                        .from(new LatLng(mPharmacy.getLat(), mPharmacy.getlang()))
                        .to(currentUserLatLng)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                if (direction.isOK()) {
                                    Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                                    ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(getBaseContext(), directionPositionList, 3, Color.RED);
                                    mMap.addPolyline(polylineOptions);
                                    mMap.addMarker(new MarkerOptions().position(currentUserLatLng)
                                            .title(getString(R.string.your_location)));
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mappp)));
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(mPharmacy.getLat(), mPharmacy.getlang()))
                                            .title(nameCompany));
//                                            .icon(bitmapDescriptorFromVector(CompanyOfferDetailActivity.this,R.drawable.ic_location_on_black_24dp)));
                                    Log.e("Direction :", "Direction Seccuss");
                                } else {
//                                Log.e("Direction :", direction.getErrorMessage());
                                }
                            }

                            //    }
//
                            @Override
                            public void onDirectionFailure(Throwable t) {
                                Log.e("Direction :", "Failure :" + t.getLocalizedMessage());
                            }
                        });
            }
        }
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


}

