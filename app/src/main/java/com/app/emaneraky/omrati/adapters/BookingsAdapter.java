package com.app.emaneraky.omrati.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.app.emaneraky.omrati.ConstantsLabika;
import com.app.emaneraky.omrati.R;
import com.app.emaneraky.omrati.models.Booking;
import com.app.emaneraky.omrati.models.ListBookingCompany;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.postViewHolder> {

    Context context;
    List<ListBookingCompany> model;
    private int lastPosition = -1;

    public BookingsAdapter(Context context, List<ListBookingCompany> model) {
        this.model = model;
        this.context = context;

    }

    @Override
    public postViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        postViewHolder viewHolder = new postViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final postViewHolder holder, int position) {
        ListBookingCompany booking = model.get(position);
        setAnimation(holder.myView, position);

        holder.setFirstName(booking.getFirstName());
        holder.setEmail(booking.getEmail());
        holder.setPhoneNum(booking.getPhoneNum());
        holder.setTimestampCreated(booking.getTimestampCreated());
        holder.setDeleteButton(booking.getBookingId());


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

        void setFirstName(String Title) {
            TextView txt_title = (TextView) myView.findViewById(R.id.dispaly_nameUser);
            txt_title.setText(Title);
        }

        void setEmail(String Title) {
            TextView txt_email = (TextView) myView.findViewById(R.id.email);
            txt_email.setText(Title);
        }


        void setPhoneNum(String PhoneNum) {
            TextView txt_PhoneNum = (TextView) myView.findViewById(R.id.phone);
            txt_PhoneNum.setText(PhoneNum);
        }

        void setTimestampCreated(HashMap<String, Object> timestampCreated) {

            long stamp = (long) timestampCreated.get("timestamp");
            Date d = new Date(stamp);
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd \n HH:mm:ss");

            TextView txt_bus = (TextView) myView.findViewById(R.id.time);
            txt_bus.setText(f.format(d));
        }

        public void setDeleteButton(final String id) {
            Button deleteBtn = (Button) myView.findViewById(R.id.delete_btn);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_TEMBOOKING_COMPANY)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child(id).removeValue();
                }
            });

        }
    }
}
