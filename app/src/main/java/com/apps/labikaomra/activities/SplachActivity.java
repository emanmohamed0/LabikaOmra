package com.apps.labikaomra.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;


public class SplachActivity extends FragmentActivity {
    private Animation animation;
    private ImageView logo;
    private TextView appTitle;
    private TextView appSlogan;
    private FirebaseAuth auth;
    public String language;
    private DatabaseReference myCompanyDatabase;
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

//        locale = getIntent().getStringExtra("locale");
        logo = (ImageView) findViewById(R.id.logo_img);
        appTitle = (TextView) findViewById(R.id.track_txt);
        appSlogan = (TextView) findViewById(R.id.pro_txt);
        myCompanyDatabase = FirebaseDatabase.getInstance().getReference();

//        language = sharedPreferences.getString("LANGUAGE", Locale.getDefault().getDisplayLanguage().toLowerCase().substring(0,2));
//
//        if (language.equals("en")) {
//            forceLocale(getApplicationContext(), "en");
//        }else if(language.equals("ال") || language.equals("ar")){
//            forceLocale(getApplicationContext(), "ar");
//            language = "ar";
//        }

        // Font path
        String fontPath = getString(R.string.font_path);
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        appSlogan.setTypeface(face);
        // Applying font
        appTitle.setTypeface(tf);
//        appSlogan.setTypeface(tf);

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
                                                   Intent start =new Intent(SplachActivity.this, ChoiceActivity.class);
                                                   start.putExtra("company_user_id",uid);
                                                   SplachActivity.this.startActivity(start);
//                                                   SplachActivity.this.startActivity(new Intent(SplachActivity.this, ChoiceActivity.class));
                                                   SplachActivity.this.finish();
                                               } else {
//                                                   String key = myCompanyDatabase.child(ConstantsLabika.FIREBASE_LOCATION_COMPANY).push().getKey();
//                                                   String uid = auth.getCurrentUser().getUid();
                                                   Intent start =new Intent(SplachActivity.this, ChoiceActivity.class);
//                                                   start.putExtra("company_user_id",key);
                                                   SplachActivity.this.startActivity(start);
//                                                   SplachActivity.this.startActivity(new Intent(SplachActivity.this, ChoiceActivity.class));
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

    @SuppressWarnings("deprecation")
    public static void forceLocale(Context context, String localeCode) {
        String localeCodeLowerCase = localeCode.toLowerCase();

        Resources resources = context.getApplicationContext().getResources();
        Configuration overrideConfiguration = resources.getConfiguration();
        Locale overrideLocale = new Locale(localeCodeLowerCase);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            overrideConfiguration.setLocale(overrideLocale);
        } else {
            overrideConfiguration.locale = overrideLocale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.getApplicationContext().createConfigurationContext(overrideConfiguration);
        } else {
            resources.updateConfiguration(overrideConfiguration, null);
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
