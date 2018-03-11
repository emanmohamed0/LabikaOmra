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
import com.apps.labikaomra.activities.CompanyOfferDetailActivity;
import com.apps.labikaomra.models.Offer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Mahmoud Sadek on 2/12/2018.
 */
public class CompanyOfferAdapter extends RecyclerView.Adapter<CompanyOfferAdapter.postViewHolder> {

    Context mContext;
    DatabaseReference myDatabase;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    private List<Offer> model;
    private int lastPosition = -1;
    private DatabaseReference mDatabaseRef;
    String name;
    public CompanyOfferAdapter(Context mContext, List<Offer> model) {
        this.model = model;
        this.mContext = mContext;
        myDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
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

        setAnimation(viewHolder.myView, position);
        viewHolder.setNameHotel(model.get(position).getTransLevel());
        viewHolder.setBus(model.get(position).getDestLevel());
        viewHolder.setFood(model.get(position).getDeals());
        viewHolder.setPrice(model.get(position).getPrice());
        viewHolder.setImage(model.get(position).getOfferImage());

        mDatabaseRef.child(ConstantsLabika.FIREBASE_LOCATION_OFFERS).child(model.get(position).getKeyId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String companyKeyId = dataSnapshot.child("companyKeyId").getValue().toString();
                mDatabaseRef.child(ConstantsLabika.FIREBASE_LOCATION_COMPANY).child(companyKeyId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                         name = dataSnapshot.child("firstName").getValue().toString();
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

        viewHolder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompanyOfferDetailActivity.mPharmacy = model.get(viewHolder.getAdapterPosition());
                Intent OfferDetail = new Intent(mContext, CompanyOfferDetailActivity.class);
                OfferDetail.putExtra("nameCompany",name);
                OfferDetail.addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(OfferDetail);
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

        void setProfileImage(String profileImage) {
            ImageView img_profile = (ImageView) myView.findViewById(R.id.item_doctor_img);
            Picasso.with(mContext)
                    .load(profileImage)
                    .error(R.drawable.picture)         // optional
                    .into(img_profile);
        }
*/
        void setName(String Title) {
            TextView txt_title = (TextView) myView.findViewById(R.id.dispaly_nameCompany);
            txt_title.setText(Title);
        }

        void setNameHotel(String Title) {
            TextView txt_hotel = (TextView) myView.findViewById(R.id.hotels);
            txt_hotel.setText(Title);
        }


        void setFood(String food) {
            TextView txt_food = (TextView) myView.findViewById(R.id.food);
            txt_food.setText(food);
        }

        void setBus(String Bus) {
            TextView txt_bus = (TextView) myView.findViewById(R.id.bus);
            txt_bus.setText(Bus);
        }
        void setPrice(String price) {
            TextView txt_price = (TextView) myView.findViewById(R.id.price);
            txt_price.setText(price);
        }
        void setImage(String image) {
            ImageView txt_image = (ImageView) myView.findViewById(R.id.imageCompany);
            Picasso.with(mContext).load(image).into(txt_image);

        }
/*

        void setRate(String rate) {
            float rateF = Float.parseFloat(rate);
            RatingBar ratingBar = (RatingBar) myView.findViewById(R.id.item_doctor_rate);
            ratingBar.setRating(rateF);
        }



        void setStreet(String Title) {
            TextView txt_title = (TextView) myView.findViewById(R.id.item_doctor_street);
            txt_title.setText(Title);
        }

        void setExperience(String Title) {
            TextView txt_title = (TextView) myView.findViewById(R.id.item_doctor_experience);
            txt_title.setText(Title);
        }

        public void setDistance(double lat, double lang) {
            TextView txt_Date = (TextView) myView.findViewById(R.id.item_doctor_distance);
            Location latLng = new Location(mContext.getString(R.string.client));
            latLng.setLatitude(lat);
            latLng.setLongitude(lang);
            int n = (int) ListDoctorFragment.mLastLocation.distanceTo(latLng);
            String dis = mContext.getString(R.string.m);
            if (n > 1000) {
                n = n / 1000;
                dis = mContext.getString(R.string.km);
            }
            txt_Date.setText((int) ListDoctorFragment.mLastLocation.distanceTo(latLng) + dis);
        }

        public void setAddFav(final String keyId) {
            ImageView orderDocBtn = (ImageView) myView.findViewById(R.id.btn_add_fav);
            orderDocBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog = new ProgressDialog(mContext);
                    progressDialog.setMessage(mContext.getString(R.string.adding_fav));
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(mContext.getString(R.string.timestamp), ServerValue.TIMESTAMP);
                    DatabaseReference client_database = myDatabase.child(ConstantsDoctory.FIREBASE_LOCATION_FAVORITE).child(ConstantsDoctory.FIREBASE_LOCATION_DOCTORS)
                            .child(auth.getCurrentUser().getUid());
                    Favorite favorite = new Favorite(keyId, timestampJoined);
                    client_database.child(keyId).setValue(favorite)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, R.string.add_fav, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, R.string.error_notadd, Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TAG", "onFailure: Not add to fav: " + e.getMessage());
                            Toast.makeText(mContext, R.string.error, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            });
        }

        public void setCall(final String phoneNumber) {
            ImageView orderDocBtn = (ImageView) myView.findViewById(R.id.btn_call);
            orderDocBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    callIntent.setData(Uri.parse(mContext.getString(R.string.tel) + phoneNumber));
                    mContext.startActivity(callIntent);
                }
            });
        }*/
    }
}
