package com.apps.labikaomra.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.labikaomra.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener , NavigationView.OnNavigationItemSelectedListener {

    TextView searchtxt, choice_place, check_out, check_in;
    Button searchnow_btn;
    Calendar from, to;
    EditText numseat;
    Long checkOutDate, checkInDate;
    String mUser_Id;
    String hotelLevel , buslevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Labika.Omra");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUser_Id =getIntent().getStringExtra("mUser_Id");

//        txtbus = (TextView) findViewById(R.id.txtbus);
//        txthotel = (TextView) findViewById(R.id.txthotel);

        numseat = (EditText)findViewById(R.id.num_seat);
        Spinner spinnerhotel = (Spinner) findViewById(R.id.type_dest);
        spinnerhotel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sSelected = adapterView.getItemAtPosition(i).toString();
                OffersActivity.txthotel = sSelected;
//                Toast.makeText(MainActivity.this, "type hotel"+sSelected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_hotel, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerhotel.setAdapter(adapter);


        Spinner spinnerbus = (Spinner) findViewById(R.id.type_trans);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.types_bus, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerbus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sSelected = adapterView.getItemAtPosition(i).toString();
                OffersActivity.txtbus = sSelected;

//                Toast.makeText(MainActivity.this, sSelected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerbus.setAdapter(adapter2);


        from = Calendar.getInstance();
        to = Calendar.getInstance();
        to.add(Calendar.DATE, 1);

        searchtxt = (TextView) findViewById(R.id.searchText);
        check_out = (TextView) findViewById(R.id.check_out);
        check_in = (TextView) findViewById(R.id.check_in);
        searchnow_btn = (Button) findViewById(R.id.search);
        hotelLevel = spinnerhotel.getSelectedItem().toString().trim();
        buslevel = spinnerbus.getSelectedItem().toString().trim();
        OffersActivity.txtbus = buslevel;
        OffersActivity.txthotel = hotelLevel;
        updateLabelfrom();
        updateLabelto();

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        searchtxt.setTypeface(face);

        searchnow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_out.getText();
                check_in.getText();
                if (numseat.getText().toString().isEmpty()) {
                    numseat.setError(getString(R.string.error_num));
//                    Toast.makeText(MainActivity.this, R.string.error_num, Toast.LENGTH_SHORT).show();
                }else{
                    OffersActivity.numseat = Integer.parseInt(numseat.getText().toString());
                }

                Intent homeIntent = new Intent(com.apps.labikaomra.activities.MainActivity.this, OffersActivity.class);
                homeIntent.putExtra("mUser_Id",mUser_Id);
                startActivity(homeIntent);
            }
        });

        choice_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent homeIntent = new Intent(MainActivity.this, SearchActivity.class);
//                startActivity(homeIntent);
            }
        });
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

                DatePickerDialog mDate = new DatePickerDialog(com.apps.labikaomra.activities.MainActivity.this, dateto, to
                        .get(Calendar.YEAR), to.get(Calendar.MONTH),
                        to.get(Calendar.DAY_OF_MONTH));
                mDate.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDate.show();

            }
        });
        check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDate = new DatePickerDialog(com.apps.labikaomra.activities.MainActivity.this, datefrom, from
                        .get(Calendar.YEAR), from.get(Calendar.MONTH),
                        from.get(Calendar.DAY_OF_MONTH));

                mDate.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDate.show();

            }
        });
    }

    public void getDate() {
        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        Long currentTime = from.getTimeInMillis();
        long endOfTomorrow = currentTime + DateUtils.DAY_IN_MILLIS
                + (DateUtils.DAY_IN_MILLIS - currentTime % DateUtils.DAY_IN_MILLIS);

        to.setTimeInMillis(endOfTomorrow);
        check_out.setText("check_out \n " + sdf.format(to.getTime()));

    }

    private void updateLabelfrom() {

        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        checkInDate = TimeUnit.MILLISECONDS.toSeconds(from.getTime().getTime());
//         checkInDate = from.getTime().getTime();
//        from.getTimeInMillis();
//        OffersActivity.checkInDate = checkInDate;
        check_in.setText("check_in \n " + sdf.format(from.getTime()));

    }

    private void updateLabelto() {

        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

//        checkOutDate = TimeUnit.MILLISECONDS.toSeconds(to.getTime().getTime());
//        checkOutDate = to.getTime().getTime();
//        OffersActivity.checkOutDate = checkOutDate;

        check_out.setText("check_out \n " + sdf.format(to.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String sSelected = adapterView.getItemAtPosition(i).toString();

        Toast.makeText(this, sSelected, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}
