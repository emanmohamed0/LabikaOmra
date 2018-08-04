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
import android.widget.Toast;

import com.app.emaneraky.omrati.ConstantsLabika;
import com.app.emaneraky.omrati.R;
import com.app.emaneraky.omrati.activities.CompanyOfferDetailActivity;
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


public class CompanyOfferAdapter extends RecyclerView.Adapter<CompanyOfferAdapter.postViewHolder> {

    FirebaseAuth auth;
    private int lastPosition = -1;
    private DatabaseReference mDatabaseRef;
    String name;
    Context context;
    List<Offer> model;

    public CompanyOfferAdapter(Context context, List<Offer> model) {
        this.model = model;
        this.context = context;
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }
    @Override
    public postViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
        postViewHolder viewHolder = new postViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final postViewHolder holder, int position) {
        Offer UploadInfo = model.get(position);
        setAnimation(holder.myView, position);

        holder.setImage(UploadInfo.getOfferImage());
        holder.setNameHotel(UploadInfo.getHotelName());
        holder.setBus(UploadInfo.getDestLevel());
        holder.setFood(UploadInfo.getDeals());
        holder.setPrice(UploadInfo.getPriceTotal());


        UploadInfo.getCompanyKeyId();

        mDatabaseRef.child(ConstantsLabika.FIREBASE_LOCATION_COMPANY).child(UploadInfo.getCompanyKeyId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("firstName").getValue().toString();
                holder.setName(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompanyOfferDetailActivity.mPharmacy = model.get(holder.getAdapterPosition());
                Intent OfferDetail = new Intent(context, CompanyOfferDetailActivity.class);
                OfferDetail.putExtra("nameCompany", name);
                OfferDetail.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(OfferDetail);
            }
        });
        //Loading image from Glide library.
    }
    @Override
    public int getItemCount() {
        return model.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.slide_down : R.anim.slide_down);
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
            if(food.equals("")){
                txt_food.setVisibility(View.GONE);
            }else {
                txt_food.setText(food);
            }
        }

        void setBus(String Bus) {
            TextView txt_bus = (TextView) myView.findViewById(R.id.bus);
            txt_bus.setText(Bus);
        }

        void setPrice(String price) {
            TextView txt_price = (TextView) myView.findViewById(R.id.price);
            txt_price.setText("Ryial "+price);
        }

        void setImage(String image) {
            ImageView txt_image = (ImageView) myView.findViewById(R.id.imageCompany);
            Glide.with(context).load(image).into(txt_image);
        }
    }
}
