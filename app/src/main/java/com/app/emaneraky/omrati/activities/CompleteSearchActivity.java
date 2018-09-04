package com.app.emaneraky.omrati.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.emaneraky.omrati.R;
import com.app.emaneraky.omrati.constans.Global;

public class CompleteSearchActivity extends AppCompatActivity {
    String Value_one;
    String Value_two;
    String Value_three;
    private RadioGroup radio_group_one, radio_group_two, radio_group_three;
    private RadioButton radioButtonOne, radioButtonTwo, radioButtonThree;
    String mUser_Id;
    int numseat;
    Button search_btn;
    TextView lbl_name;
    ImageView img_back;

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(CompleteSearchActivity.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_search);

        search_btn = (Button) findViewById(R.id.search);
        mUser_Id = Global.get_UserID(CompleteSearchActivity.this, "mUser_Id");
        numseat = getIntent().getIntExtra("numseat", 1);

        lbl_name = (TextView) findViewById(R.id.lbl_name);
        lbl_name.setText(getString(R.string.app_name));
        img_back = (ImageView) findViewById(R.id.img_back);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        addListenerOnButton();
    }

    public void addListenerOnButton() {

        radio_group_one = (RadioGroup) findViewById(R.id.radio_group_one);
        radio_group_two = (RadioGroup) findViewById(R.id.radio_group_two);
        radio_group_three = (RadioGroup) findViewById(R.id.radio_group_three);

        search_btn = (Button) findViewById(R.id.search);

        search_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selectedIdOne = radio_group_one.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioButtonOne = (RadioButton) findViewById(selectedIdOne);
                if (radioButtonOne.getText() == null) {
                    Snackbar.make(v, "Please Choice One", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Value_one = radioButtonOne.getText().toString();
                    OffersActivity.value_one = Value_one;
                }
                // get selected radio button from radioGroup
                int selectedIdTwo = radio_group_two.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioButtonTwo = (RadioButton) findViewById(selectedIdTwo);
                if (radioButtonTwo.getText() == null) {
                    Snackbar.make(v, "Please Choice One", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Value_two = radioButtonTwo.getText().toString();
                    OffersActivity.value_two = Value_two;
                }
                // get selected radio button from radioGroup
                int selectedIdThree = radio_group_three.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioButtonThree = (RadioButton) findViewById(selectedIdThree);
                if (radioButtonThree.getText() == null) {
                    Snackbar.make(v, "Please Choice One", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Value_three = radioButtonThree.getText().toString();
                    OffersActivity.value_three = Value_three;
                }


                Intent homeIntent = new Intent(CompleteSearchActivity.this, OffersActivity.class);
//                homeIntent.putExtra("mUser_Id", mUser_Id);
                homeIntent.putExtra("numseat", numseat);
                startActivity(homeIntent);

            }

        });

    }

}
