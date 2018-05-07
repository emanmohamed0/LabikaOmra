package com.apps.labikaomra.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.apps.labikaomra.models.DataFacility;
import com.apps.labikaomra.models.ListBookingCompany;
import com.apps.labikaomra.models.Offer;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CompanyBooking extends AppCompatActivity {
    private RecyclerView mBookingList;
    private View view;
    private FirebaseAuth auth;
    private String current_User, mUser_Id;
    static Context c;
    private List<DataFacility> model;
    String nameCompany;
    private DatabaseReference mBookingRefData;
    private DatabaseReference mOfferRefData;
    private DatabaseReference mCompanyRefData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_booking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.bookingbar);
        toolbar.setTitle(R.string.Reservations);
        setSupportActionBar(toolbar);

        c = getBaseContext();
        mBookingList = (RecyclerView) findViewById(R.id.bookingrecycle);
        mBookingList.setHasFixedSize(true);
        mBookingList.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        mUser_Id = getIntent().getStringExtra("mUser_Id");
        current_User = auth.getCurrentUser().getUid();

        mBookingRefData = FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_BOOKING_COMPANY).child(current_User);
        mOfferRefData = FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_OFFERS);
        mCompanyRefData = FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_COMPANY);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Offer, CompanyBooking.BookingViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Offer, CompanyBooking.BookingViewHolder>(
                Offer.class,
                R.layout.item_detail,
                CompanyBooking.BookingViewHolder.class,
                mBookingRefData
        ) {
            @Override
            protected void populateViewHolder(final CompanyBooking.BookingViewHolder viewHolder, final Offer model, final int position) {
                final String list_UserID = getRef(position).getKey();
                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBookingRefData.child(list_UserID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String address = dataSnapshot.child("address").getValue().toString();
                                String img = dataSnapshot.child("bookingImage").getValue().toString();
                                String email = dataSnapshot.child("email").getValue().toString();
                                String lastname = dataSnapshot.child("lastName").getValue().toString();
                                String firstname = dataSnapshot.child("firstName").getValue().toString();
                                String phone = dataSnapshot.child("phoneNum").getValue().toString();
                                ListBookingCompany bookingCompany = new ListBookingCompany(firstname, lastname, email, address, phone, img);
                                DetailBooking.bookingCompanies = bookingCompany;

                                Intent detailOpen = new Intent(CompanyBooking.this,DetailBooking.class);
                                startActivity(detailOpen);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                mOfferRefData.child(list_UserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String companyKeyId = dataSnapshot.child("companyKeyId").getValue().toString();

                        final String hotelname = dataSnapshot.child("hotelName").getValue().toString();
                        final String busLevel = dataSnapshot.child("destLevel").getValue().toString();
                        final String deals = dataSnapshot.child("deals").getValue().toString();
                        final String price = dataSnapshot.child("priceTotal").getValue().toString();
                        final String offerImage = dataSnapshot.child("offerImage").getValue().toString();

                        viewHolder.setHotels(hotelname);
                        viewHolder.setBus(busLevel);
                        viewHolder.setFood(deals);
                        viewHolder.setPrice(price);
                        viewHolder.setImage(offerImage);
                        nameCompany = dataSnapshot.child("companyKeyId").getValue().toString();

                        mCompanyRefData.child(nameCompany).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String firstName = dataSnapshot.child("firstName").getValue().toString();
                                viewHolder.setName(firstName);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        mBookingList.setAdapter(recyclerAdapter);
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        View myView;

        public BookingViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
        }

        void setImage(String image) {
            ImageView txt_image = (ImageView) myView.findViewById(R.id.imageCompany);
            Glide.with(c).load(image).into(txt_image);


        }

        void setNameHotel(String Title) {
            TextView txt_hotel = (TextView) myView.findViewById(R.id.hotels);
            txt_hotel.setText(Title);
        }

        void setName(String Title) {
            TextView txt_title = (TextView) myView.findViewById(R.id.dispaly_nameCompany);
            txt_title.setText(Title);
        }

        void setBus(String Title) {
            TextView txt_title = (TextView) myView.findViewById(R.id.bus);
            txt_title.setText(Title);
        }

        void setFood(String food) {
            TextView txt_title = (TextView) myView.findViewById(R.id.food);
            txt_title.setText(food);
        }

        void setHotels(String Title) {
            TextView txt_title = (TextView) myView.findViewById(R.id.hotels);
            txt_title.setText(Title);
        }

        void setPrice(String Title) {
            TextView txt_title = (TextView) myView.findViewById(R.id.price);
            txt_title.setText(Title);
        }


    }
}
