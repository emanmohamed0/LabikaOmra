package com.apps.labikaomra.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.apps.labikaomra.models.Company;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class CompanyRegisterActivity extends AppCompatActivity {

    int MAP_REQUEST = 2, GALARY_REQUEST1 = 1;
    public static double lat = 0, lang = 0;
    Button btnSignUp, btnLocation;
    ImageView imgProfile;
    StorageReference mystorage;
    String image_uri = "", location;
    Uri image_uriget = null;
    ProgressDialog progressDialogS;
    private FirebaseAuth auth;
    private DatabaseReference myUserDatabase;
    private EditText inputEmail, inputPassword, inputconfPassword, inputFirstName, inputPhone, inputMobile;
    private DatabaseReference mySUserDatabase;
    private FirebaseAuth myAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        overridePendingTransition(R.anim.animation1, R.anim.animation2);

        register();
    }
    private void register() {
//        txtbirth.setTypeface(myTypeface);
//        btnSignUp.setTypeface(myTypeface);
        inputEmail = (EditText) findViewById(R.id.emailField);
//        inputSEmail.setTypeface(myTypeface);
        inputPassword = (EditText) findViewById(R.id.passwordField);
//        inputSPassword.setTypeface(myTypeface);
        inputconfPassword = (EditText) findViewById(R.id.confpasswordField);
//        inputSconfPassword.setTypeface(myTypeface);
        inputFirstName = (EditText) findViewById(R.id.usernameField);
//        inputSFirstName.setTypeface(myTypeface);
        inputPhone = (EditText) findViewById(R.id.phoneField);
//        inputSLastName.setTypeface(myTypeface);
        inputMobile = (EditText) findViewById(R.id.mobileField);

        imgProfile = (ImageView) findViewById(R.id.imageView_profile);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnLocation = (Button) findViewById(R.id.btnLocation);

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMap = new Intent(CompanyRegisterActivity.this, ChooseLocationActivity.class);
                startActivityForResult(intentMap, MAP_REQUEST);
            }
        });
        imgProfile = (ImageView) findViewById(R.id.imageView_profile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galaryIntent.setType("image/*");
                galaryIntent.putExtra("crop", "true");
                galaryIntent.putExtra("aspectX", 0);
                galaryIntent.putExtra("aspectY", 0);
                try {
                    galaryIntent.putExtra("return-data", true);
                    startActivityForResult(Intent.createChooser(galaryIntent, getString(R.string.completeaction)), GALARY_REQUEST1);

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(CompanyRegisterActivity.this, "error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
//                startActivityForResult(galaryIntent, GALARY_REQUEST1);
            }
        });
//        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        //Get Firebase auth instance
        myAuth = FirebaseAuth.getInstance();
        mySUserDatabase = FirebaseDatabase.getInstance().getReference();
        mystorage = FirebaseStorage.getInstance().getReference();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sinup();
            }
        });
    }

    private void sinup() {
        final String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        final String firstName = inputFirstName.getText().toString().trim();
        final String phone = inputPhone.getText().toString().trim();
        final String mobile = inputMobile.getText().toString().trim();
        if (!validate()) {
            return;
        }
//        progressBar.setVisibility(View.VISIBLE);

        progressDialogS = new ProgressDialog(CompanyRegisterActivity.this);
        progressDialogS.setIndeterminate(true);
        progressDialogS.setMessage(getString(R.string.creat_acount));
        progressDialogS.setCancelable(false);
        progressDialogS.show();
        //create user
        myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    completeRegister(firstName, email, phone, mobile);
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CompanyRegisterActivity.this);
                    SharedPreferences.Editor spe = sp.edit();
                    spe.putString(ConstantsLabika.USER_ID, myAuth.getCurrentUser().getUid()).apply();
                    spe.commit();

                } else {
                    progressDialogS.dismiss();
                    Toast.makeText(CompanyRegisterActivity.this, R.string.somewrong, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public boolean validate() {
        boolean valid = true;
        String firstName = inputFirstName.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String repassword = inputconfPassword.getText().toString().trim();
        String mobile = inputMobile.getText().toString().trim();
        if (firstName.isEmpty() || firstName.length() < 3) {
            inputFirstName.setError("at least 3 characters");
            Toast.makeText(CompanyRegisterActivity.this, "First Name is not right", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputFirstName.setError(null);
        }
        if (phone.isEmpty() || phone.length() < 9 || !Patterns.PHONE.matcher(phone).matches()) {
            inputPhone.setError("at least 9 characters");
            Toast.makeText(CompanyRegisterActivity.this, "phone is not right", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputPhone.setError(null);
        }
        if (mobile.isEmpty() || mobile.length() < 9 || !Patterns.PHONE.matcher(phone).matches()) {
            inputMobile.setError("at least 9 characters");
            Toast.makeText(CompanyRegisterActivity.this, "mobile is not right", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputMobile.setError(null);
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            Toast.makeText(CompanyRegisterActivity.this, "Email is not right", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 7 || password.length() > 14) {
            inputPassword.setError("between 7 and 14 alphanumeric characters");
            Toast.makeText(CompanyRegisterActivity.this, "Password is not right", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputPassword.setError(null);
        }
        if (!repassword.equals(password)) {
            inputPassword.setError("password not match!");
            Toast.makeText(CompanyRegisterActivity.this, "password not match", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputPassword.setError(null);
        }
        if (image_uri.equals("")) {
            Toast.makeText(CompanyRegisterActivity.this, "Choose Your Image First", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (ChooseLocationActivity.lat == 0.0 || ChooseLocationActivity.lang == 0.0) {
            Toast.makeText(CompanyRegisterActivity.this, R.string.add_doc_tost_lat, Toast.LENGTH_SHORT).show();
            valid = false;
        }else {
            location = getAddress(ChooseLocationActivity.lat, ChooseLocationActivity.lang);
        }
        return valid;
    }

    private void completeRegister(final String firstName, final String email, final String phone, final String mobile) {
        StorageReference filepath = mystorage.child(getString(R.string.useuproimg)).child(RandomName());

        // Get the data from an ImageView as bytes
        imgProfile.setDrawingCacheEnabled(true);
        imgProfile.buildDrawingCache();
        Bitmap bitmap = imgProfile.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        UploadTask uploadTask = filepath.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                progressDialogS.dismiss();
                Toast.makeText(CompanyRegisterActivity.this, R.string.somewrong, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                AddUserData(firstName, email, downloadUrl.toString(), phone, mobile);

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(getString(R.string.profress), String.format(getString(R.string.onProgress),
                        taskSnapshot.getBytesTransferred() / 1024.0 / 1024.0));

            }
        });


    }

    private void AddUserData(String firstName, String email, String photo, String phone, String mobile) {

        /**
         * Set raw version of date to the ServerValue.TIMESTAMP value and save into
         * timestampCreatedMap
         */
        HashMap<String, Object> timestampCreated = new HashMap<>();
        timestampCreated.put(ConstantsLabika.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        Company user = new Company(firstName, email, photo, phone, mobile, location, timestampCreated,lat,lang );
        final String user_id = myAuth.getCurrentUser().getUid();

        DatabaseReference user_database = mySUserDatabase.child(ConstantsLabika.FIREBASE_LOCATION_COMPANY).child(user_id);
        user_database.setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(CompanyRegisterActivity.this, R.string.welcome, Toast.LENGTH_SHORT).show();
                            progressDialogS.dismiss();
                            Intent mainIntent = new Intent(CompanyRegisterActivity.this, CompanyOffersActivity.class);
                            mainIntent.putExtra("company_user_id", user_id);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            progressDialogS.dismiss();
                            Toast.makeText(CompanyRegisterActivity.this, R.string.error_notadd, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALARY_REQUEST1 && resultCode == Activity.RESULT_OK) {
            image_uriget = data.getData();
            imgProfile.setImageURI(image_uriget);
            image_uri = data.getData().toString();

            Bundle extras2 = data.getExtras();
            if (extras2 != null) {

                Bitmap photo = extras2.getParcelable(getString(R.string.data));
                imgProfile.setImageBitmap(photo);
            }
        }
        if (requestCode == MAP_REQUEST) {
            location = getAddress(ChooseLocationActivity.lat, ChooseLocationActivity.lang);
            btnLocation.setText(location);
        }
    }


    private String RandomName() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        int length = random.nextInt(20);
        char temp;
        for (int i = 0; i < length; i++) {
            temp = (char) (random.nextInt(96) + 32);
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }
    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(CompanyRegisterActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
            return add;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return "f";
    }
}
