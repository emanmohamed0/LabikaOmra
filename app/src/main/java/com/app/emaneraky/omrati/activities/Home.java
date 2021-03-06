package com.app.emaneraky.omrati.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.emaneraky.omrati.ConstantsLabika;
import com.app.emaneraky.omrati.R;
import com.app.emaneraky.omrati.adapters.CustomAdapter;
import com.app.emaneraky.omrati.adapters.PopDestAdapter;
import com.app.emaneraky.omrati.constans.Global;
import com.app.emaneraky.omrati.models.Offer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity
        implements
        AdapterView.OnItemSelectedListener, com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    private com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference cateagory, mDatabase;
    TextView searchtxt, txtPop, check_out, check_in;
    Button next_btn;
    Calendar from, to;
    EditText numseat;
    ImageView menu, img_back;
    String mUser_Id, local;
    String placeLevel, transLevel;
    //    Locale locale;
    String[] countryNames;
    RecyclerView recyclerViewPopDest;
    PopDestAdapter adapters;
    List<Offer> categoriesModel;
    LinearLayoutManager horizontalLayoutManagaer;
    FirebaseAuth auth;
    TextView nav_reservations, nav_Favorite_Companies, nav_Service_Provider, nav_Languages, nav_share;
    String lang;
    DrawerLayout drawer;
    String date_Out, date_In;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    int clicked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        countryNames = new String[]{getString(R.string.transonly), getString(R.string.transonly2), getString(R.string.transonly3)};

        mUser_Id = Global.get_UserID(Home.this, "mUser_Id");
        auth = FirebaseAuth.getInstance();

        //init firebase
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        cateagory = firebaseDatabase.getReference("Cateagory");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        nav_reservations = (TextView) findViewById(R.id.nav_reservations);
        nav_Favorite_Companies = (TextView) findViewById(R.id.nav_Favorite_Companies);
        nav_Service_Provider = (TextView) findViewById(R.id.nav_Service_Provider);
        nav_Languages = (TextView) findViewById(R.id.nav_Languages);
        nav_share = (TextView) findViewById(R.id.nav_share);

        nav_reservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(Home.this, UserLoginActivity.class);
                    loginIntent.putExtra("mNumSeat", "0");
                    startActivity(loginIntent);
                } else {
                    if (mUser_Id != null) {
                        mDatabase.child(ConstantsLabika.FIREBASE_LOCATION_USERS).child(mUser_Id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String type = dataSnapshot.child("type").getValue().toString();
                                if (type.equals("user")) {
                                    Intent bookingIntent = new Intent(Home.this, BookingActivity.class);
//                                bookingIntent.putExtra("mUser_Id", auth.getCurrentUser().getUid().toString());

                                    startActivity(bookingIntent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }
        });

        nav_Favorite_Companies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(Home.this, UserLoginActivity.class);
                    loginIntent.putExtra("mNumSeat", "0");
                    startActivity(loginIntent);
                } else {
                    if (auth.getCurrentUser().getUid().toString() != null) {
                        mDatabase.child(ConstantsLabika.FIREBASE_LOCATION_USERS).child(mUser_Id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String type = dataSnapshot.child("type").getValue().toString();
                                if (type.equals("user")) {
                                    Intent favoriteIntent = new Intent(Home.this, FavoriteActivity.class);
//                                favoriteIntent.putExtra("mUser_Id", auth.getCurrentUser().getUid().toString());
                                    startActivity(favoriteIntent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }
        });
        nav_Service_Provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent searchIntent = new Intent(Home.this, CompanyLoginActivity.class);
                startActivity(searchIntent);
                finish();
            }
        });
        nav_Languages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogeLanguage();

            }
        });
        nav_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share) + " \n https://play.google.com/store");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share App"));
            }
        });


        recyclerViewPopDest = (RecyclerView) findViewById(R.id.popDest);
        recyclerViewPopDest.setHasFixedSize(true);
        horizontalLayoutManagaer = new LinearLayoutManager(Home.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPopDest.setLayoutManager(horizontalLayoutManagaer);

        categoriesModel = new ArrayList<Offer>();

        mDatabase.child(ConstantsLabika.FIREBASE_LOCATION_OFFERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Offer pharmacy = postSnapshot.getValue(Offer.class);
                    if (from.getTimeInMillis() <= pharmacy.getStartDay()) {
                        categoriesModel.add(pharmacy);
                    }
                }
                adapters = new PopDestAdapter(getBaseContext(), categoriesModel, mUser_Id);
                recyclerViewPopDest.setAdapter(adapters);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lang = Global.checkLocal(Home.this);
        numseat = (EditText) findViewById(R.id.num_seat);
        menu = (ImageView) findViewById(R.id.menu);
        img_back = (android.widget.ImageView) findViewById(R.id.img_back);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             onBackPressed();
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lang.equals("en")) {
                    drawer.openDrawer(Gravity.LEFT);
                } else
                    drawer.openDrawer(Gravity.RIGHT);

            }
        });
        final Spinner spinnerPlace = (Spinner) findViewById(R.id.type_dest);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.type_places, android.R.layout.simple_spinner_item);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlace.setPrompt("Type Of Places!");
        spinnerPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sSelected = adapterView.getItemAtPosition(i).toString();
                OffersActivity.txtplace = sSelected;
//                Toast.makeText(MainActivity.this, sSelected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerPlace.setAdapter(adapter2);

        final Spinner spinnerTrans = (Spinner) findViewById(R.id.type_trans);
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), countryNames);
        spinnerTrans.setAdapter(customAdapter);
        spinnerTrans.setOnItemSelectedListener(this);

        from = Calendar.getInstance();
        to = Calendar.getInstance();
        to.add(Calendar.DATE, 1);

        searchtxt = (TextView) findViewById(R.id.searchText);
        txtPop = (TextView) findViewById(R.id.txtPop);
        check_out = (TextView) findViewById(R.id.check_out);
        check_in = (TextView) findViewById(R.id.check_in);
        next_btn = (Button) findViewById(R.id.next);
        placeLevel = spinnerPlace.getSelectedItem().toString().trim();
        OffersActivity.txttrans = transLevel;
        OffersActivity.txtplace = placeLevel;
        updateLabelfrom();
        updateLabelto();

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        searchtxt.setTypeface(face);
        txtPop.setTypeface(face);

//                showSettingsAlert();
        // Should we show an explanation?
//        if ((ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.CAMERA))
//                && (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION))
//                && (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE))
//                && (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE))) {


        int Permision = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (Permision != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PermisionStorage, 1);
        }


//        } else {
//            // No explanation needed, we can request the permission.
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA,
//                            Manifest.permission.ACCESS_COARSE_LOCATION,
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    MY_PERMISSIONS_REQUEST_CAMERA);
//        }


        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeLevel = spinnerPlace.getSelectedItem().toString().trim();
                OffersActivity.txttrans = transLevel;
                OffersActivity.txtplace = placeLevel;
                OffersActivity.checkOutDate = to.getTimeInMillis();
                OffersActivity.checkInDate = from.getTimeInMillis();
                if (numseat.getText().toString().isEmpty()) {
                } else {
                    OffersActivity.numseat = Integer.parseInt(numseat.getText().toString());
                }
                if (Validate()) {
//                    Global.set_UserId(Home.this,mUser_Id);
                    Intent homeIntent = new Intent(Home.this, CompleteSearchActivity.class);
//                    homeIntent.putExtra("mUser_Id", mUser_Id);
                    homeIntent.putExtra("numseat", Integer.parseInt(numseat.getText().toString()));
                    startActivity(homeIntent);
                }
            }
        });


//        locale = getResources().getConfiguration().locale;
//        Locale.setDefault(locale);

//        final com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener datefrom = new com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//                view.setMinDate(System.currentTimeMillis() - 1000);
//                from.set(Calendar.YEAR, year);
//                from.set(Calendar.MONTH, monthOfYear);
//                from.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabelfrom();
//
//                Long currentTimefrom = from.getTimeInMillis();
//                Long currentTimeto = to.getTimeInMillis();
//
//                if (currentTimefrom >= currentTimeto) {
//                    getDate();
//                }
//                OffersActivity.checkInDate = from.getTimeInMillis();
//            }
//
//        };
///////////
//        final com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener dateto = new com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//                to.set(Calendar.YEAR, year);
//                to.set(Calendar.MONTH, monthOfYear);
//                to.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabelto();
//                to.getTimeInMillis();
//                OffersActivity.checkOutDate = to.getTimeInMillis();
//            }
//
//        };

        check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = 1;
                calendr();
//
//                DatePickerDialog mDate = new DatePickerDialog(Home.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, DatePickerDialog.this,dateto, to
//                        .get(Calendar.YEAR), to.get(Calendar.MONTH),
//                        to.get(Calendar.DAY_OF_MONTH));
//                mDate.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//                mDate.show();

            }
        });
        check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = 0;
                calendr();

//                DatePickerDialog mDate = new DatePickerDialog(Home.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, datefrom, from
//                        .get(Calendar.YEAR), from.get(Calendar.MONTH),
//                        from.get(Calendar.DAY_OF_MONTH));
//
//                mDate.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//                mDate.show();

            }
        });
    }

    private String[] PermisionStorage = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public boolean Validate() {
        boolean valid = true;
        String seat = numseat.getText().toString().trim();

        if (seat.isEmpty()) {
            numseat.setError(getString(R.string.error_chairCount));
            valid = false;
        }
        return valid;
    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(Home.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(Home.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                                        , Manifest.permission.CAMERA
                                        , Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},

                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                });
        alertDialog.show();
    }

    public void getDate() {
        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        Long currentTime = from.getTimeInMillis();
        long endOfTomorrow = currentTime + DateUtils.DAY_IN_MILLIS
                + (DateUtils.DAY_IN_MILLIS - currentTime % DateUtils.DAY_IN_MILLIS);

        to.setTimeInMillis(endOfTomorrow);
        date_Out = sdf.format(to.getTime());
        check_out.setText(getString(R.string.check_out) + "\n" + date_Out);

    }

    private void updateLabelfrom() {

        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        date_In = sdf.format(from.getTime());
        check_in.setText(getString(R.string.check_in) + "\n" + date_In);

    }

    private void updateLabelto() {

        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        check_out.setText(getString(R.string.check_out) + "\n" + sdf.format(to.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(Home.this, ChoiceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public void openDialogeLanguage() {
        // custom dialog
        final Dialog dialog = new Dialog(Home.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.language_dialoge);
        final String lang = Global.checkLocal(Home.this);
        final TextView lbl_language = (TextView) dialog.findViewById(R.id.lbl_language);
        if (lang.equals("en")) {
            lbl_language.setText(getString(R.string.language_ar));
        } else {
            lbl_language.setText(getString(R.string.language_en));
        }
        lbl_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lang.equals("en")) {
                    lbl_language.setText(getString(R.string.language_ar));
                    Global.setLocale(Home.this, "ar");
                } else {
                    lbl_language.setText(getString(R.string.language_en));
                    Global.setLocale(Home.this, "en");
                }
                Intent intent = new Intent(Home.this, SplachActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        transLevel = countryNames[position];
        OffersActivity.txttrans = countryNames[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void calendr() {
        Calendar now = Calendar.getInstance();

        if (dpd == null) {
            dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(Home.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    Home.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }

        dpd.setVersion(com.wdullaer.materialdatetimepicker.date.DatePickerDialog.Version.VERSION_2);

        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onResume() {
        super.onResume();
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = (com.wdullaer.materialdatetimepicker.date.DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        if (dpd != null) dpd.setOnDateSetListener(Home.this);
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//        String date = "You picked the following date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
        if (clicked == 0) {
            //Calender From
            from.set(Calendar.YEAR, year);
            from.set(Calendar.MONTH, monthOfYear);
            from.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelfrom();

            Long currentTimefrom = from.getTimeInMillis();
            Long currentTimeto = to.getTimeInMillis();

            if (currentTimefrom >= currentTimeto) {
                getDate();
            }
            OffersActivity.checkInDate = from.getTimeInMillis();
        } else {
            //Calender To
            to.set(Calendar.YEAR, year);
            to.set(Calendar.MONTH, monthOfYear);
            to.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelto();
            to.getTimeInMillis();
            OffersActivity.checkOutDate = to.getTimeInMillis();


        }


    }
}