package com.apps.labikaomra.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.apps.labikaomra.activities.OfferDetailActivity;
import com.apps.labikaomra.models.Offer;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.postViewHolder> {

    Context mContext;
    DatabaseReference myDatabase;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    private List<Offer> model;
    private DatabaseReference mDatabaseRef;
    String name, namechoice;
    public String mUser_Id;
    String value;
    //firstName
    private int lastPosition = -1, numseatback;

    public OfferAdapter(Context mContext, List<Offer> model, String mUser_Id, int numseatback, String value) {
        this.model = model;
        this.mUser_Id = mUser_Id;
        this.mContext = mContext;
        myDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        this.numseatback = numseatback;
        this.value = value;
    }

    @Override
    public postViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail, parent, false);

        return new postViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final postViewHolder viewHolder, final int position) {

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mDatabaseRef.child(ConstantsLabika.FIREBASE_LOCATION_OFFERS).child(model.get(position).getKeyId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String companyKeyId = dataSnapshot.child("companyKeyId").getValue().toString();
                mDatabaseRef.child(ConstantsLabika.FIREBASE_LOCATION_COMPANY).child(companyKeyId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("firstName").getValue().toString();
                        viewHolder.setName(name);
//                        Toast.makeText(mContext, "name" +name, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//                Toast.makeText(mContext, "companyKeyId \n adapter" + companyKeyId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setAnimation(viewHolder.myView, position);
        viewHolder.setNameHotel(model.get(position).getHotelName());
        viewHolder.setBus(model.get(position).getDestLevel());
        viewHolder.setFood(model.get(position).getDeals());
        viewHolder.setPrice(model.get(position).getPriceTotal());
        if (model.get(position).getOfferImage() != null) {
            viewHolder.setImage(model.get(position).getOfferImage());
        }


        if (model.get(position).getContentImagesList() != null) {

            viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OfferDetailActivity.mPharmacy = model.get(viewHolder.getAdapterPosition());
//                Toast.makeText(mContext, model.get(viewHolder.getAdapterPosition()).getKeyId(), Toast.LENGTH_SHORT).show();

                    mDatabaseRef.child(ConstantsLabika.FIREBASE_LOCATION_OFFERS).child(model.get(viewHolder.getAdapterPosition()).getKeyId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String companyKeyId = dataSnapshot.child("companyKeyId").getValue().toString();
                            mDatabaseRef.child(ConstantsLabika.FIREBASE_LOCATION_COMPANY).child(companyKeyId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    namechoice = dataSnapshot.child("firstName").getValue().toString();

                                    Intent OfferDetail = new Intent(mContext, OfferDetailActivity.class);
                                    OfferDetail.putExtra("nameCompany", namechoice);
                                    OfferDetail.putExtra("mUser_Id", mUser_Id);
                                    OfferDetail.putExtra("numseat", numseatback);
                                    OfferDetail.putExtra("value", value);
                                    OfferDetail.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(OfferDetail);
//                                viewHolder.setName(namechoice);
//                        Toast.makeText(mContext, "name" +name, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
//                Toast.makeText(mContext, "companyKeyId \n adapter" + companyKeyId, Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

//

//                OfferDetailActivity.mPharmacy = model.get(viewHolder.getAdapterPosition());
//
//                Intent n = new Intent(mContext, OfferDetailActivity.class);
//                n.putExtra("nameCompany",n);
//                n.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(n);
                }
            });
        }else {
            Toast.makeText(mContext, mContext.getString(R.string.offer_expire), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.slide_down : R.anim.slide_down);
        viewToAnimate.startAnimation(animation);
        if (position > lastPosition) {
            lastPosition = position;
        }
    }

    class postViewHolder extends RecyclerView.ViewHolder {
        View myView;

        public postViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
        }

        void setImage(String image) {
            ImageView txt_image = (ImageView) myView.findViewById(R.id.imageCompany);
            Glide.with(mContext).load(image).into(txt_image);

//            Picasso.with(mContext).load(image).into(txt_image);

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
            TextView txt_food = (TextView) myView.findViewById(R.id.food);
            if (food.equals("")) {
                txt_food.setVisibility(View.GONE);
            } else {
                txt_food.setText(food);
            }
        }

        void setHotels(String Title) {
            TextView txt_title = (TextView) myView.findViewById(R.id.hotels);
            txt_title.setText(Title);
        }

        void setPrice(String Title) {
            TextView txt_title = (TextView) myView.findViewById(R.id.price);
            txt_title.setText("Ryial " + Title);
        }

    }
}
