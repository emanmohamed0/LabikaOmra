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
import android.widget.Toast;

import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.apps.labikaomra.models.Favorite;
import com.apps.labikaomra.models.Offer;
import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView recChatList;
    private View view;
    private FirebaseAuth auth;
    private String current_User, mUser_Id ,nameCompany;
    static Context c;

    private DatabaseReference mChatRefData;
    private DatabaseReference mOfferRefData;
    private DatabaseReference mCompanyRefData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = (Toolbar) findViewById(R.id.favoritebar);
        toolbar.setTitle("FavoriteOffers");
        setSupportActionBar(toolbar);

        c =getBaseContext();
        recChatList = (RecyclerView) findViewById(R.id.favoriterecycle);
        recChatList.setHasFixedSize(true);
        recChatList.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        mUser_Id = getIntent().getStringExtra("mUser_Id");
        current_User = auth.getCurrentUser().getUid();
        mChatRefData = FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_FAVORITE).child(mUser_Id);
        mOfferRefData = FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_OFFERS);
        mCompanyRefData = FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_COMPANY);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Offer, FavoriteViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Offer, FavoriteViewHolder>(
                Offer.class,
                R.layout.item_detail,
                FavoriteViewHolder.class,
                mChatRefData
        ) {
            @Override
            protected void populateViewHolder(final FavoriteViewHolder viewHolder, Offer model, int position) {
                final String list_UserID = getRef(position).getKey();

                mChatRefData.child(list_UserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String keyId = dataSnapshot.child("keyId").getValue().toString();
                        mOfferRefData.child(keyId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Offer offer = dataSnapshot.getValue(Offer.class);
                                viewHolder.setBus(offer.getDestLevel());
                                viewHolder.setFood(offer.getDeals());
                                viewHolder.setHotels(offer.getTransLevel());
                                viewHolder.setPrice(offer.getPriceTotal());
                                viewHolder.setImage(offer.getOfferImage());
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        recChatList.setAdapter(recyclerAdapter);
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        View myView;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setImage(final String profile) {
            final ImageView userProfile = (ImageView) myView.findViewById(R.id.imageCompany);

            Glide.with(c).load(profile).into(userProfile);


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
