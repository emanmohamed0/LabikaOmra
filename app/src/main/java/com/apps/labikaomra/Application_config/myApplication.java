package com.apps.labikaomra.Application_config;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by sadek on 9/11/2016.
 */
public class myApplication extends Application {
    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;
    private static DatabaseReference myRef;

    public static DatabaseReference getDatabaseReference() {
        myRef = FirebaseDatabase.getInstance().getReference();
        return myRef;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            if (!FirebaseApp.getApps(this.getBaseContext()).isEmpty())
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);

//            Picasso.Builder builder = new Picasso.Builder(this.getBaseContext());
////            builder.downloader(new  OkHttpDownloader(this.getBaseContext(), Integer.MAX_VALUE));
//            Picasso built = builder.build();
//            built.setIndicatorsEnabled(true);
//            built.setLoggingEnabled(true);
//            Picasso.setSingletonInstance(built);
        } catch (Exception e) {
            Toast.makeText(myApplication.this, getString(R.string.errorapp) + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        offline();

    }

    public void offline(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //for Picasso image offline

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String currentUser = mAuth.getCurrentUser().getUid();

            userDatabase = FirebaseDatabase.getInstance()
                    .getReference().child(ConstantsLabika.FIREBASE_LOCATION_COMPANY).child(currentUser);
        }
    }


}
