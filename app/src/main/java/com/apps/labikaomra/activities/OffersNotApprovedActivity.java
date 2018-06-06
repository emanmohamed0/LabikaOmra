package com.apps.labikaomra.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.apps.labikaomra.adapters.BookingsAdapter;
import com.apps.labikaomra.models.Booking;
import com.apps.labikaomra.models.ListBookingCompany;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OffersNotApprovedActivity extends AppCompatActivity {

    static Context c;
    BookingsAdapter bookingsAdapter;
    private RecyclerView mBookingList;
    private View view;
    private FirebaseAuth auth;
    private String current_User, mUser_Id;
    private List<ListBookingCompany> bookingList;
    private DatabaseReference mBookingRefData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        c = getBaseContext();
        mBookingList = (RecyclerView) findViewById(R.id.bookingrecycle);
        mBookingList.setHasFixedSize(true);
        mBookingList.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        mUser_Id = auth.getCurrentUser().getUid().toString();
        current_User = auth.getCurrentUser().getUid();

        mBookingRefData = FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_TEMBOOKING_COMPANY).child(current_User);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mBookingRefData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, ListBookingCompany> results = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, ListBookingCompany>>() {
                });
                if (results != null) {
                    bookingList = new ArrayList<ListBookingCompany>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        ListBookingCompany booking = postSnapshot.getValue(ListBookingCompany.class);
//                        HashMap<String, Object> timestampCreated = dataSnapshot.child("timestampCreated").getValue(HashMap.class);
                        long stamp = (long) booking.getTimestampCreated().get("timestamp");
                        Date d = new Date(stamp);
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.HOUR, -5);
                        Date d2 = c.getTime();
                        bookingList.add(booking);

//                        if (d.compareTo(d2)<=0) {
////                            ListBookingCompany booking = postSnapshot.getValue(ListBookingCompany.class);
//                            bookingList.add(booking);
//                        }
                    }
                    bookingsAdapter = new BookingsAdapter(c, bookingList);
                    mBookingList.setAdapter(bookingsAdapter);
                } else {
                    Toast.makeText(c, R.string.nodataload, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(c, R.string.nodataload, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
