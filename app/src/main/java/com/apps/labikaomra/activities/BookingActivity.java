package com.apps.labikaomra.activities;

import android.content.Context;
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
import com.apps.labikaomra.models.Offer;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class BookingActivity extends AppCompatActivity {
    private RecyclerView mBookingList;
    private View view;
    private FirebaseAuth auth;
    private String current_User, mUser_Id;
    static Context c;
    String nameCompany;
    private DatabaseReference mBookingRefData;
    private DatabaseReference mOfferRefData;
    private DatabaseReference mCompanyRefData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.bookingbar);
        toolbar.setTitle(R.string.Reservations);
        setSupportActionBar(toolbar);

        c =getBaseContext();
        mBookingList = (RecyclerView) findViewById(R.id.bookingrecycle);
        mBookingList.setHasFixedSize(true);
        mBookingList.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        mUser_Id = getIntent().getStringExtra("mUser_Id");
        current_User = auth.getCurrentUser().getUid();

        mBookingRefData = FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_BOOKING).child(mUser_Id);
        mOfferRefData = FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_OFFERS);
        mCompanyRefData = FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_COMPANY);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Offer, BookingActivity.BookingViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Offer, BookingActivity.BookingViewHolder>(
                Offer.class,
                R.layout.item_detail,
                BookingActivity.BookingViewHolder.class,
                mBookingRefData
        ) {
            @Override
            protected void populateViewHolder(final BookingActivity.BookingViewHolder viewHolder, final Offer model, final int position) {
                final String list_UserID = getRef(position).getKey();

                mOfferRefData.child(list_UserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String hotelname = dataSnapshot.child("hotelName").getValue().toString();
                        final String busLevel = dataSnapshot.child("busLevel").getValue().toString();
                        final String deals =dataSnapshot.child("deals").getValue().toString();
                        final String price = dataSnapshot.child("price").getValue().toString();
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
//                                Toast.makeText(BookingActivity.this, firstName, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
//                        Toast.makeText(BookingActivity.this, dataSnapshot.child("companyKeyId").getValue().toString(), Toast.LENGTH_SHORT).show();
//                        viewHolder.myView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent openDetail = new Intent(BookingActivity.this,BookingDetailActivity.class);
//                                openDetail.putExtra("hotelname",hotelname);
//                                openDetail.putExtra("busLevel",busLevel);
//                                openDetail.putExtra("deals",deals);
//                                openDetail.putExtra("price",price);
//                                openDetail.putExtra("location",location);
//                                startActivity(openDetail);
//                            }
//                        });

                        //                        mOfferRefData.child(keyId).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                Offer offer = dataSnapshot.getValue(Offer.class);
//                                viewHolder.setBus(offer.getBusLevel());
//                                viewHolder.setFood(offer.getDeals());
//                                viewHolder.setHotels(offer.getHotelLevel());
//                                viewHolder.setPrice(offer.getPrice());
//                                viewHolder.setImage(offer.getOfferImage());
//
////                                mCompanyRefData.child(offer.getCompanyKeyId()).addValueEventListener(new ValueEventListener() {
////                                    @Override
////                                    public void onDataChange(DataSnapshot dataSnapshot) {
////                                        String  name = dataSnapshot.child("firstName").getValue().toString();
////                                        viewHolder.setName(name);
////                                    }
////
////                                    @Override
////                                    public void onCancelled(DatabaseError databaseError) {
////
////                                    }
////                                });
//                            }
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                            }
//                        });
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

        public void setImage(final String profile) {
            final ImageView userProfile = (ImageView) myView.findViewById(R.id.imageCompany);
//            Picasso.with(c).load(profile).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(userProfile);

            Picasso.with(c).load(profile).networkPolicy(NetworkPolicy.OFFLINE).into(userProfile, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    //in case online
                    Picasso.with(c).load(profile).into(userProfile);

                }
            });
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
