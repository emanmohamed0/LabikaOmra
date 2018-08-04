package com.app.emaneraky.omrati.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.emaneraky.omrati.R;
import com.app.emaneraky.omrati.models.ListBookingCompany;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailBooking extends AppCompatActivity {
    TextView txtemail, txtphone, txtname, txtaddress;
    ImageView img;
    public static ListBookingCompany bookingCompanies;
    DatabaseReference myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_booking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.bardetail);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.detail));

        myDatabase = FirebaseDatabase.getInstance().getReference();
        myDatabase.keepSynced(true);
        txtaddress = (TextView) findViewById(R.id.address);
        txtemail = (TextView) findViewById(R.id.email);
        txtphone = (TextView) findViewById(R.id.phone);
        txtname = (TextView) findViewById(R.id.name);
        img = (ImageView) findViewById(R.id.putimg);

        txtaddress.setText(bookingCompanies.getAddress());
        txtemail.setText(bookingCompanies.getEmail());
        txtname.setText(bookingCompanies.getFirstName() + " " + bookingCompanies.getLastName());
        txtphone.setText(bookingCompanies.getPhoneNum());
        Glide.with(DetailBooking.this).load(bookingCompanies.getBookingImage()).into(img);

    }
}
