package com.apps.labikaomra.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.labikaomra.R;
import com.apps.labikaomra.notification.FCMRegistrationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;


public class SplachActivity extends FragmentActivity {
    private Animation animation;
    private ImageView logo;
    private TextView appTitle;
    private TextView appSlogan;
    private FirebaseAuth auth;
    public String language;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    String locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.animation1, R.anim.animation2);
        setContentView(R.layout.activity_splach);
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        locale = getIntent().getStringExtra("locale");
        logo = (ImageView) findViewById(R.id.logo_img);
        appTitle = (TextView) findViewById(R.id.track_txt);
        appSlogan = (TextView) findViewById(R.id.pro_txt);

        FirebaseMessaging.getInstance().subscribeToTopic("notification");

        startService(new Intent(this, FCMRegistrationService.class));
        // Log.e("Token is ", FirebaseInstanceId.getInstance().getToken());
        //Token is: fY0si4U-7Zc:APA91bGft0cntLNCHgXDSxUSh2e8mXkZieYvwoDOvG9fYNLmCJD7w61yJo3dTt2V0Ho37BoNLhGQHzWI3t9-glQYUw0_CuWZZ_g0LDjT0AKqQI2FwgmqVMuFaHpSGEizYpWfXTATs2JG

        // Font path
        String fontPath = getString(R.string.font_path);
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        appSlogan.setTypeface(face);
        // Applying font
        appTitle.setTypeface(tf);

        if (savedInstanceState == null) {
            flyIn();
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                endSplash();
            }
        }, 3000);
    }

    private void flyIn() {
        animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logo.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.app_name_animation);
        appTitle.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.pro_animation);
        appSlogan.startAnimation(animation);
    }

    private void endSplash() {
        animation = AnimationUtils.loadAnimation(this,
                R.anim.logo_animation_back);
        logo.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.app_name_animation_back);
        appTitle.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.pro_animation_back);
        appSlogan.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
                                           @Override
                                           public void onAnimationEnd(Animation arg0) {
                                               auth = FirebaseAuth.getInstance();
                                               if (auth.getCurrentUser() != null) {
                                                   String uid = auth.getCurrentUser().getUid();
                                                   Intent start = new Intent(SplachActivity.this, ChoiceActivity.class);
//                                                   start.putExtra("company_user_id", uid);
                                                   start.putExtra("locale", locale);
                                                   SplachActivity.this.startActivity(start);
                                                   SplachActivity.this.finish();
                                               } else {
                                                   Intent start = new Intent(SplachActivity.this, ChoiceActivity.class);
                                                   start.putExtra("locale", locale);
                                                   SplachActivity.this.startActivity(start);
                                                   SplachActivity.this.finish();
                                               }
                                               finish();
                                           }

                                           @Override
                                           public void onAnimationRepeat(Animation arg0) {
                                           }

                                           @Override
                                           public void onAnimationStart(Animation arg0) {
                                           }
                                       }

        );

    }

}
