package com.apps.labikaomra.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.apps.labikaomra.R;

import java.util.Locale;

public class ChooseLanguageActivity extends AppCompatActivity {
    Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        // This will get the radiogroup
        RadioGroup rGroup = (RadioGroup) findViewById(R.id.rBGPoly1);
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    if(checkedRadioButton.getText().toString().equals("English")){
                        setLocale("en");
                        Intent loginIntent = new Intent(ChooseLanguageActivity.this, SplachActivity.class);
                        startActivity(loginIntent);
                    }else {
                        setLocale("ar");
                        Intent loginIntent = new Intent(ChooseLanguageActivity.this, SplachActivity.class);
                        startActivity(loginIntent);
                    }

                    // Changes the textview's text to "Checked: example radiobutton text"
                    Toast.makeText(ChooseLanguageActivity.this, "Checked:" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radiobuttonEn:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radiobuttonAr:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }

    public void onClickRadio(View view) {

        switch (view.getId()) {
            case R.id.radiobuttonEn:
                setLocale("en");
                break;
            case R.id.radiobuttonAr:
                setLocale("ar");
                break;
            default:
                setLocale("en");

        }
    }

    private void setLocale(String language) {
        myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);


    }
}
