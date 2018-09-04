package com.app.emaneraky.omrati.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    TextView lbl_name;
    ImageView img_back;

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(DetailBooking.this, CompanyBooking.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_booking);


        lbl_name = (TextView) findViewById(R.id.lbl_name);
        lbl_name.setText(getString(R.string.detail));
        img_back = (ImageView) findViewById(R.id.img_back);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


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
