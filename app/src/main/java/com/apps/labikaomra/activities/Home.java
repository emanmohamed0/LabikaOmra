package com.apps.labikaomra.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.apps.labikaomra.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference cateagory;

    TextView searchtxt, check_out, check_in, txtFullName, txtEmail;
    Button next_btn;
    Calendar from, to;
    EditText numseat;
    String mUser_Id, local;
    String placeLevel, transLevel, nameCompany;
    Locale myLocale, locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Omrati");
        setSupportActionBar(toolbar);
        local = getIntent().getStringExtra("locale");
        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        cateagory = firebaseDatabase.getReference("Cateagory");
        nameCompany = getIntent().getStringExtra("nameCompany");

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        Booking booking = new Booking();
//        //set Full Name
//        View headerView = navigationView.getHeaderView(0);
//        txtFullName = (TextView) headerView.findViewById(R.id.full_name);
//        txtFullName.setText(booking.getFirstName());
//
//        txtEmail = (TextView) headerView.findViewById(R.id.email);
//        txtEmail.setText(booking.getEmail());
//        txtbus = (TextView) findViewById(R.id.txtbus);
//        txthotel = (TextView) findViewById(R.id.txthotel);

        mUser_Id = getIntent().getStringExtra("mUser_Id");

        numseat = (EditText) findViewById(R.id.num_seat);
        final Spinner spinnerPlace = (Spinner) findViewById(R.id.type_dest);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_places, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlace.setPrompt("Type Of Places!");
        spinnerPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sSelected = adapterView.getItemAtPosition(i).toString();
                OffersActivity.txtplace = sSelected;
//                Toast.makeText(MainActivity.this, "type hotel"+sSelected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.types_hotel, android.R.layout.simple_spinner_item);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlace.setAdapter(adapter);


        final Spinner spinnerTrans = (Spinner) findViewById(R.id.type_trans);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.type_transport, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTrans.setPrompt("Type Of Transport!");
        spinnerTrans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sSelected = adapterView.getItemAtPosition(i).toString();
                OffersActivity.txttrans = sSelected;

//                Toast.makeText(MainActivity.this, sSelected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerTrans.setAdapter(adapter2);
//////////////////////////////////////////////////////////////////////
//        Spinner spinner = (Spinner) findViewById(R.meid.type_bus);
//        ArrayAdapter<CharSequence> adapterbus = ArrayAdapter.createFromResource(this, R.array.types_bus, android.R.layout.simple_spinner_item);
//        adapterbus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setPrompt("Select Type of Bus!");
//
//        spinner.setAdapter(
//                new NothingSelectedSpinnerAdapter(
//                        adapterbus,
//                        R.layout.contact_spinner_row_nothing_selected,
//                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
//                        this));


        from = Calendar.getInstance();
        to = Calendar.getInstance();
        to.add(Calendar.DATE, 1);

        searchtxt = (TextView) findViewById(R.id.searchText);
        check_out = (TextView) findViewById(R.id.check_out);
        check_in = (TextView) findViewById(R.id.check_in);
        next_btn = (Button) findViewById(R.id.next);
        placeLevel = spinnerPlace.getSelectedItem().toString().trim();
        transLevel = spinnerTrans.getSelectedItem().toString().trim();
        OffersActivity.txttrans = transLevel;
        OffersActivity.txtplace = placeLevel;
        updateLabelfrom();
        updateLabelto();

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        searchtxt.setTypeface(face);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeLevel = spinnerPlace.getSelectedItem().toString().trim();
                transLevel = spinnerTrans.getSelectedItem().toString().trim();
//                if(choice_place.getText().toString().isEmpty()){
//                }else{
//                    OffersActivity.txtplace = choice_place.getText().toString();
//                }
                OffersActivity.txtplace = placeLevel;
                OffersActivity.txttrans = transLevel;
                OffersActivity.checkOutDate = to.getTimeInMillis();
                OffersActivity.checkInDate = from.getTimeInMillis();
                if (numseat.getText().toString().isEmpty()) {
//                    numseat.setError(getString(R.string.error_num));
//                    Toast.makeText(MainActivity.this, R.string.error_num, Toast.LENGTH_SHORT).show();
                } else {
                    OffersActivity.numseat = Integer.parseInt(numseat.getText().toString());
                }

                Intent homeIntent = new Intent(Home.this, CompleteSearchActivity.class);
                homeIntent.putExtra("mUser_Id", mUser_Id);
                startActivity(homeIntent);
            }
        });

//        choice_place.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent homeIntent = new Intent(Home.this, SearchCompanyActivity.class);
//                startActivity(homeIntent);
//            }
//        });
//        if (nameCompany == null) {
//        } else {
//            choice_place.setText(nameCompany + "");
//        }

        locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        final DatePickerDialog.OnDateSetListener datefrom = new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                view.setMinDate(System.currentTimeMillis() - 1000);
                from.set(Calendar.YEAR, year);
                from.set(Calendar.MONTH, monthOfYear);
                from.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelfrom();

                Long currentTimefrom = from.getTimeInMillis();
                Long currentTimeto = to.getTimeInMillis();

                if (currentTimefrom >= currentTimeto) {
                    getDate();
//                    check_out.setText(getDate());
                }
                OffersActivity.checkInDate = from.getTimeInMillis();
            }

        };

        final DatePickerDialog.OnDateSetListener dateto = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                view.setMinDate(System.currentTimeMillis() - 1000);
                to.set(Calendar.YEAR, year);
                to.set(Calendar.MONTH, monthOfYear);
                to.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelto();
                to.getTimeInMillis();
                OffersActivity.checkOutDate = to.getTimeInMillis();

            }

        };
        check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog mDate = new DatePickerDialog(Home.this, dateto, to
                        .get(Calendar.YEAR), to.get(Calendar.MONTH),
                        to.get(Calendar.DAY_OF_MONTH));
                mDate.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDate.show();

            }
        });
        check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDate = new DatePickerDialog(Home.this, datefrom, from
                        .get(Calendar.YEAR), from.get(Calendar.MONTH),
                        from.get(Calendar.DAY_OF_MONTH));

                mDate.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDate.show();

            }
        });
    }

    public void getDate() {
        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        Long currentTime = from.getTimeInMillis();
        long endOfTomorrow = currentTime + DateUtils.DAY_IN_MILLIS
                + (DateUtils.DAY_IN_MILLIS - currentTime % DateUtils.DAY_IN_MILLIS);

        to.setTimeInMillis(endOfTomorrow);
        String date_Out = sdf.format(to.getTime());
        check_out.setText(getString(R.string.check_out) + "\n" + date_Out);

    }

    private void updateLabelfrom() {

        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        String date_In = sdf.format(from.getTime());
        check_in.setText(getString(R.string.check_in) + "\n" + date_In);

    }

//     if (local == "en") {
//        String myFormat = "EEE, MMM d"; //In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//
////        checkOutDate = TimeUnit.MILLISECONDS.toSeconds(to.getTime().getTime());
////        checkOutDate = to.getTime().getTime();
////        OffersActivity.checkOutDate = checkOutDate;
//
//        check_out.setText(getString(R.string.check_out) + "\n" + sdf.format(to.getTime()));
//    } else {
//        String myFormat = "EEE, MMM d"; //In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("ar"));
//
////        checkOutDate = TimeUnit.MILLISECONDS.toSeconds(to.getTime().getTime());
////        checkOutDate = to.getTime().getTime();
////        OffersActivity.checkOutDate = checkOutDate;
//
//        check_out.setText(getString(R.string.check_out) + "\n" + sdf.format(to.getTime()));
//    }
    private void updateLabelto() {

        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        check_out.setText(getString(R.string.check_out) + "\n" + sdf.format(to.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }


    private void setLocale(String language) {
        myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_reservations) {
            if (mUser_Id == null) {
                Intent loginIntent = new Intent(Home.this, UserLoginActivity.class);
                startActivity(loginIntent);
            } else {
                Intent bookingIntent = new Intent(Home.this, BookingActivity.class);
                bookingIntent.putExtra("mUser_Id", mUser_Id);
                startActivity(bookingIntent);
            }
        } else if (id == R.id.nav_Favorite_Companies) {
            if (mUser_Id == null) {
                Intent loginIntent = new Intent(Home.this, UserLoginActivity.class);
                startActivity(loginIntent);
            } else {
                Intent favoriteIntent = new Intent(Home.this, FavoriteActivity.class);
                favoriteIntent.putExtra("mUser_Id", mUser_Id);
                startActivity(favoriteIntent);
            }
        } else if (id == R.id.nav_Languages) {
            Intent intentra = new Intent(Home.this, ChooseLanguageActivity.class);
            startActivity(intentra);

        } else if (id == R.id.nav_Service_Provider) {

            Intent searchIntent = new Intent(Home.this, CompanyLoginActivity.class);
            startActivity(searchIntent);
            finish();

        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("Text/plain");
            String msg = String.format("%s: %s", "حمل تطبيق لبيك عمرة الان واحجز رحلتك في دقائق", "https://play.google.com/store");
            shareIntent.putExtra(Intent.EXTRA_TEXT, msg);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.app_name)));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
