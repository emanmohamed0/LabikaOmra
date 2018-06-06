package com.apps.labikaomra.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.labikaomra.R;
import com.apps.labikaomra.models.DataFacility;
import com.apps.labikaomra.models.ListBookingCompany;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DialogBooking extends AppCompatActivity {
    Button mLogin;
    TextView addfacility;
    private EditText inputName, inputlastName, inputMobile, inputaddress, inputIdcard, inputEmail;
    String CompanyKeyId, mUser_Id, KeyId;
    List<DataFacility> facilityList;
    ProgressDialog progressDialog;
    DatabaseReference myDatabase;
    FirebaseAuth auth;
    int numseat, seatback;
    String seat;
    private static final int CAM_REQUEST = 1313;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_booking);

        CompanyKeyId = getIntent().getStringExtra("CompanyKeyId");
        mUser_Id = getIntent().getStringExtra("mUser_Id");
        KeyId = getIntent().getStringExtra("KeyId");
        seat = getIntent().getStringExtra("mNumSeat");
        numseat = getIntent().getIntExtra("numseat", 1);

        myDatabase = FirebaseDatabase.getInstance().getReference();
        myDatabase.keepSynced(true);
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser().getUid() == null) {
            Intent logingoogle = new Intent(DialogBooking.this, UserLoginActivity.class);
            startActivity(logingoogle);
        } else {
            CreateView();
            mLogin = (Button) findViewById(R.id.login);

            addfacility = (TextView) findViewById(R.id.addfacility);
            inputName = (EditText) findViewById(R.id.nameField);
            inputlastName = (EditText) findViewById(R.id.lastnameField);
            inputMobile = (EditText) findViewById(R.id.mobileField);
            inputaddress = (EditText) findViewById(R.id.addressField);
            inputEmail = (EditText) findViewById(R.id.emailField);
            inputIdcard = (EditText) findViewById(R.id.idcardField);

        }
    }

    private void CreateView() {
        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.mLayout);
        if (seat == null) {
            int size = numseat; // total number of TextViews to add
            if (numseat != 0) {
                size = size - 1;
                Button[] tv = new Button[size];
                Button temp;
                facilityList = new ArrayList<>();

                for (int i = 0; i < size; i++) {
                    temp = new Button(this);
                    if (i == 0) {
                        temp.setText(getString(R.string.morafiq) + " " + 1);
                    }
                    temp.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_style));
                    final Button finalTemp = temp;
                    final int finalI = i;
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showChangeLangDialog(finalI);
                        }
                    });
                    // add the textview to the linearlayout
                    mLinearLayout.addView(temp);

                    tv[i] = temp;
                }

            }
        } else {
            int size = Integer.parseInt(seat); // total number of TextViews to add
            if (Integer.parseInt(seat) != 0) {
                size = size - 1;
                Button[] tv = new Button[size];
                Button temp;
                facilityList = new ArrayList<>();

                for (int i = 0; i < size; i++) {
                    temp = new Button(this);
                    if (i == 0) {
                        temp.setText(getString(R.string.morafiq) + " " + 1);
                    }

                    temp.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_style));
                    final Button finalTemp = temp;
                    final int finalI = i;
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showChangeLangDialog(finalI);
                        }
                    });
                    // add the textview to the linearlayout
                    mLinearLayout.addView(temp);

                    tv[i] = temp;
                }
            }
        }
//        TextView txt = new TextView(this);
//        txt.setText(getString(R.string.dataMorafiq));
//        txt.setTextColor(Color.parseColor("#FFFFFF"));
//        txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_create_black_24dp, 0, 0, 0);
        Button btn = new Button(this);
        btn.setText(getString(R.string.booking));
        btn.setTextColor(Color.parseColor("#FFFFFF"));
        btn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_style));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) {
                } else {
                    ListBookingCompany listBookingCompany = new ListBookingCompany(KeyId, CompanyKeyId, inputName.getText().toString(),
                            inputlastName.getText().toString(), inputEmail.getText().toString(), inputaddress.getText().toString(),
                            inputMobile.getText().toString(), inputIdcard.getText().toString(), facilityList);
                    ConfirmBooking.listBookingCompany = listBookingCompany;

                    Intent mConfirmIntent = new Intent(DialogBooking.this, ConfirmBooking.class);
                    mConfirmIntent.putExtra("mUser_Id", mUser_Id);
                    mConfirmIntent.putExtra("CompanyKeyId", CompanyKeyId);
                    mConfirmIntent.putExtra("KeyId", KeyId);
                    startActivity(mConfirmIntent);

                }
            }
        });
        mLinearLayout.addView(btn);
    }

    public boolean validate() {
        boolean valid = true;
        String firstName = inputName.getText().toString().trim();
        String lastName = inputlastName.getText().toString().trim();
        String phone = inputMobile.getText().toString().trim();
        String bank = inputIdcard.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String address = inputaddress.getText().toString().trim();

        if (firstName.isEmpty() || firstName.length() < 3) {
            inputName.setError("at least 3 characters");
//            Toast.makeText(DialogBooking.this, "First Name is not right", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputName.setError(null);
        }
        if (lastName.isEmpty() || lastName.length() < 3) {
            inputlastName.setError("at least 3 characters");
//            Toast.makeText(DialogBooking.this, "Last Name is not right", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputlastName.setError(null);
        }
        if (phone.isEmpty() || phone.length() < 9 || !Patterns.PHONE.matcher(phone).matches()) {
            inputMobile.setError("at least 9 characters");
//            Toast.makeText(DialogBooking.this, "phone is not right", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputMobile.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
//            Toast.makeText(DialogBooking.this, "Email is not right", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (bank.isEmpty()) {
            inputIdcard.setError("enter a valid IDCard address");
//            Toast.makeText(DialogBooking.this, "Please enter this", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputIdcard.setError(null);
        }

        if (address.isEmpty()) {
            inputaddress.setError("enter a valid Address address");
//            Toast.makeText(DialogBooking.this, "Please enter this", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputaddress.setError(null);
        }
        return valid;
    }

    public void showChangeLangDialog(final int i) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.my_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText edtname = (EditText) dialogView.findViewById(R.id.nameField);
        final EditText edtidCard = (EditText) dialogView.findViewById(R.id.idcardField);

        dialogBuilder.setTitle(getString(R.string.enter));
        dialogBuilder.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edtname.getText().toString().isEmpty() && edtidCard.getText().toString().isEmpty()) {
                    Toast.makeText(DialogBooking.this, "Please Enter All Data ^_^ ", Toast.LENGTH_LONG).show();
                } else {
                    DataFacility dataFacility = new DataFacility(edtname.getText().toString(), edtidCard.getText().toString());
                    facilityList.add(i, dataFacility);
                }

                //do something with edt.getText().toString();
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();


    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

}
