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
    ChoiceActivity choiceActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        choiceActivity = new ChoiceActivity();

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
                        choiceActivity.setLocale(ChooseLanguageActivity.this,"en");
//                        setLocale("en");
                        Intent loginIntent = new Intent(ChooseLanguageActivity.this, SplachActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }else {
//                        setLocale("ar");
                        choiceActivity.setLocale(ChooseLanguageActivity.this,"ar");
                        Intent loginIntent = new Intent(ChooseLanguageActivity.this, SplachActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }

                    // Changes the textview's text to "Checked: example radiobutton text"
//                    Toast.makeText(ChooseLanguageActivity.this, "Checked:" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
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

    private void setLocale(String language) {
        myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }
}
