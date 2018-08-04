package com.app.emaneraky.omrati.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.app.emaneraky.omrati.R;
import com.app.emaneraky.omrati.constans.Global;
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
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ChooseLocationActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    public static Double lat = 0.0;
    public static Double lang = 0.0;
    Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, R.string.gps_enable, Toast.LENGTH_SHORT).show();
        } else {
//            showGPSDisabledAlertToUser();
            getLocation();
        }


        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }


    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
                                    int permissionLocation = ContextCompat.checkSelfPermission(ChooseLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                        if (myLocation != null) {
                                            MarkerOptions currentUserLocation = new MarkerOptions();
                                            LatLng currentUserLatLang = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                                            double lat = currentUserLatLang.latitude;
                                            double lang = currentUserLatLang.longitude;

                                            currentUserLocation.position(currentUserLatLang);
                                            mMap.addMarker(currentUserLocation);
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLang, 16));
                                            finishing(currentUserLatLang);
                                        }
                                        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                                            @Override
                                            public void onMapLongClick(LatLng latLng) {
                                                mMap.clear();
                                                Geocoder geocoder = new Geocoder(ChooseLocationActivity.this);
                                                List<Address> list = null;
                                                try {
                                                    list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    return;
                                                }
                                                MarkerOptions markerOptions = new MarkerOptions()
                                                        .position(latLng).title(getString(R.string.tabed_loc));
                                                mMap.addMarker(markerOptions);
                                                finishing(latLng);
                                            }
                                        });

                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(ChooseLocationActivity.this, Global.REQUEST_CHECKSETTING_GPS);
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            getLocation();
//            Location userCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//            if (userCurrentLocation != null) {
//                MarkerOptions currentUserLocation = new MarkerOptions();
//                LatLng currentUserLatLang = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
//                double lat = currentUserLatLang.latitude;
//                double lang = currentUserLatLang.longitude;
//
//                currentUserLocation.position(currentUserLatLang);
//                mMap.addMarker(currentUserLocation);
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLang, 16));
//                finishing(currentUserLatLang);
//            }
//            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//                @Override
//                public void onMapLongClick(LatLng latLng) {
//                    mMap.clear();
//                    Geocoder geocoder = new Geocoder(ChooseLocationActivity.this);
//                    List<Address> list = null;
//                    try {
//                        list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        return;
//                    }
////                    Address add = list.get(0);
//                    MarkerOptions markerOptions = new MarkerOptions()
//                            .position(latLng).title(getString(R.string.tabed_loc));
//                    mMap.addMarker(markerOptions);
//                    finishing(latLng);
//                }
//            });
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onConnected(null);
        } else {
            Toast.makeText(ChooseLocationActivity.this, R.string.no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Global.REQUEST_CHECKSETTING_GPS) {
            getLocation();
        }

    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.gps_disable)
                .setCancelable(false)
                .setPositiveButton(R.string.enable_gps,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void finishing(LatLng latLng) {
        CompanyRegisterActivity.lat = latLng.latitude;
        CompanyRegisterActivity.lang = latLng.longitude;
        lat = latLng.latitude;
        lang = latLng.longitude;


    }

    @Override
    public void onLocationChanged(Location location) {

    }
}