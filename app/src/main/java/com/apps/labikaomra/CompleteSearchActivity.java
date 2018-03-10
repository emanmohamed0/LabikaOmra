package com.apps.labikaomra;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class CompleteSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainsearch);
        toolbar.setTitle("Omrati");
        setSupportActionBar(toolbar);
    }
}
