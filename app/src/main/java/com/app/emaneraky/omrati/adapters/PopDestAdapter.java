package com.app.emaneraky.omrati.adapters;

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

import com.app.emaneraky.omrati.ConstantsLabika;
import com.app.emaneraky.omrati.R;
import com.app.emaneraky.omrati.activities.OfferDetailActivity;
import com.app.emaneraky.omrati.models.Offer;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PopDestAdapter extends RecyclerView.Adapter<PopDestAdapter.postViewHolder> {

    Context mContext;
    DatabaseReference myDatabase;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    private List<Offer> model;
    private DatabaseReference mDatabaseRef;
    String seat, namechoice;
    public String mUser_Id;
    //firstName
    private int lastPosition = -1;

    public PopDestAdapter(Context mContext, List<Offer> model, String mUser_Id) {
        this.model = model;
        this.mUser_Id = mUser_Id;
        this.mContext = mContext;
        myDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public postViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_popdest, parent, false);

        return new postViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final postViewHolder viewHolder, final int position) {

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mDatabaseRef.child(ConstantsLabika.FIREBASE_LOCATION_OFFERS).child(model.get(position).getKeyId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("companyKeyId")) {

                } else {
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
                }
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
//                                OfferDetail.putExtra("mUser_Id", mUser_Id);
                                OfferDetail.putExtra("mNumSeat", "1");
                                OfferDetail.putExtra("value", "value");
                                OfferDetail.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                                Global.set_UserId(mContext,mUser_Id);
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
//        viewHolder.setOrderDocBtn(model.get(position));


//        viewHolder.setProfileImage(model.get(position).get());
//        if (ListDoctorFragment.mLastLocation != null) {
//            viewHolder.setDistance(model.get(position).getLat(), model.get(position).getLng());
//        }

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

        /*public void setOrderDocBtn(final Doctor mDoctor) {
            Button orderDocBtn = (Button) myView.findViewById(R.id.btn_order_doctor);
            orderDocBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OrderDoctorActivity.name = mDoctor.getName();
                    OrderDoctorActivity.home = mDoctor.getLocation();
                    OrderDoctorActivity.street = mDoctor.getStreet();
                    OrderDoctorActivity.specialty = mDoctor.getSpecialty();
                    OrderDoctorActivity.keyId = mDoctor.getKeyId();
                    OrderDoctorActivity.photo = mDoctor.getProfileImg();
                    Intent i = new Intent(mContext, OrderDoctorActivity.class);
                    i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                }
            });
        }
*/
        void setImage(String image) {
            ImageView txt_image = (ImageView) myView.findViewById(R.id.imageCompany);
            Glide.with(mContext).load(image).into(txt_image);

//            Picasso.with(mContext).load(image).into(txt_image);

        }

        //        void setofferImage(String image){
//            CircleImageView img = (CircleImageView)myView.findViewById(R.id.profilrImg);
//            Picasso.with(mContext).load(image).into(img);
//
//        }
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
            txt_title.setText(mContext.getString(R.string.coin)+" "+Title);
        }

    }
}