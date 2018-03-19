package com.apps.labikaomra.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.apps.labikaomra.models.Offer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddOfferActivity extends AppCompatActivity {
    String Value_one;
    String Value_two;
    String Value_three;

    private RadioGroup radio_group_one, radio_group_two, radio_group_three;
    private RadioButton radioButtonOne, radioButtonTwo, radioButtonThree;

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int Date_id = 0;
    private static final int Time_id = 1;
    public static double lat = 0, lang = 0;
    static Context mContext;
    static int GALARY_REQUEST1 = 2;
    DatabaseReference mClientsDatabase;
    StorageReference mystorage;
    Spinner spinnerHotelLevel, spinnerBusLevel, spinnerDestLevel, spinnerTransLevel;
    Button btnStartDay, btnAttendStartTime, btnStartTime, btnBackDay, btnAttendEndTime, btnEndTime, btnLocation, btnChooseMultiImage;
    ImageView imgProfile;
    Uri image_uri = null;
    ProgressDialog progressDialog;
    String profileImg = "", attendStartTime, startTime, attendEndTime, endTime, location;
    Long startDay, backDay;
    ArrayList<String> contentImgsArrayList;
    int dialogFlag = 0;
    Calendar c, b;
     Uri resultUri;
    Offer offerimage;
    Calendar from, to;
    String company_user_id;

    DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // store the data in one string and set it to text
            String date1 = String.valueOf(month) + "/" + String.valueOf(day)
                    + "/" + String.valueOf(year);
            if (dialogFlag == 0) {
                long diffInSec = c.getTimeInMillis();
                Long startDayLong = c.getTime().getTime();

                startDay = diffInSec;
                btnStartDay.setText(date1);
            } else if (dialogFlag == 1) {
                long diffInSec = c.getTimeInMillis();

                Long backDayLong = c.getTime().getTime();

                backDay = diffInSec;
                btnBackDay.setText(date1);
            }
        }

    };
    TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            // store the data in one string and set it to text
            String time1 = String.valueOf(hour) + ":" + String.valueOf(minute);
            if (dialogFlag == 2) {
                attendStartTime = time1;
                btnAttendStartTime.setText(time1);
            } else if (dialogFlag == 3) {
                startTime = time1;
                btnStartTime.setText(time1);
            } else if (dialogFlag == 4) {
                attendEndTime = time1;
                btnAttendEndTime.setText(time1);
            } else if (dialogFlag == 5) {
                endTime = time1;
                btnEndTime.setText(time1);
            }
        }
    };
    private FirebaseAuth myAuth;
    private EditText inputHotelName, inputDeals, inputPrice, inputChairCount;

    /*
     * Add new active list
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        c = Calendar.getInstance();
        from = Calendar.getInstance();
        to = Calendar.getInstance();
        offerimage = new Offer();
        company_user_id = getIntent().getStringExtra("company_user_id");
        mClientsDatabase = FirebaseDatabase.getInstance().getReference();
        mystorage = FirebaseStorage.getInstance().getReference();
        myAuth = FirebaseAuth.getInstance();
        mContext = getBaseContext();
        inputHotelName = (EditText) findViewById(R.id.hotelNameField);
        inputDeals = (EditText) findViewById(R.id.dealsField);
        inputPrice = (EditText) findViewById(R.id.priceField);
        inputChairCount = (EditText) findViewById(R.id.chairsNumField);
//        spinnerHotelLevel = (Spinner) findViewById(R.id.spinner_hotel_level);
//        spinnerBusLevel = (Spinner) findViewById(R.id.spinner_bus_level);
        spinnerDestLevel = (Spinner) findViewById(R.id.spinner_dest);
        spinnerTransLevel = (Spinner) findViewById(R.id.spinner_trans);

        btnStartDay = (Button) findViewById(R.id.btnStartDay);
        btnAttendStartTime = (Button) findViewById(R.id.btnAttendStartTime);
        btnStartTime = (Button) findViewById(R.id.btnStartTime);
        btnBackDay = (Button) findViewById(R.id.btnBackDay);
        btnAttendEndTime = (Button) findViewById(R.id.btnAttendEndTime);
        btnEndTime = (Button) findViewById(R.id.btnEndTime);
        btnLocation = (Button) findViewById(R.id.btnLocation);
        progressDialog = new ProgressDialog(com.apps.labikaomra.activities.AddOfferActivity.this);
        radio_group_one = (RadioGroup) findViewById(R.id.radio_group_one);
        radio_group_two = (RadioGroup) findViewById(R.id.radio_group_two);
        radio_group_three = (RadioGroup) findViewById(R.id.radio_group_three);

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMap = new Intent(mContext, ChooseLocationActivity.class);
                startActivityForResult(intentMap, 1);
            }
        });

        final DatePickerDialog.OnDateSetListener datefrom = new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                view.setMinDate(System.currentTimeMillis() - 1000);
                from.set(Calendar.YEAR, year);
                from.set(Calendar.MONTH, monthOfYear);
                from.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelfrom();

                startDay = from.getTimeInMillis();


            }

        };

        final DatePickerDialog.OnDateSetListener dateto = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                view.setMinDate(System.currentTimeMillis() - 1000);
                to.set(Calendar.YEAR, year);
                to.set(Calendar.MONTH, monthOfYear);
                to.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelto();
                backDay = to.getTimeInMillis();
            }

        };

        btnStartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mDate = new DatePickerDialog(com.apps.labikaomra.activities.AddOfferActivity.this, datefrom, from
                        .get(Calendar.YEAR), from.get(Calendar.MONTH),
                        from.get(Calendar.DAY_OF_MONTH));

                mDate.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDate.show();
            }
        });
        btnAttendStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(Time_id);
                dialogFlag = 2;
            }
        });
        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(Time_id);
                dialogFlag = 3;
            }
        });
        btnBackDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mDate = new DatePickerDialog(com.apps.labikaomra.activities.AddOfferActivity.this, dateto, to
                        .get(Calendar.YEAR), to.get(Calendar.MONTH),
                        to.get(Calendar.DAY_OF_MONTH));
                mDate.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDate.show();
            }
        });
        btnAttendEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(Time_id);
                dialogFlag = 4;
            }
        });
        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(Time_id);
                dialogFlag = 5;
            }
        });
        btnChooseMultiImage = (Button) findViewById(R.id.btnContentOfferImgs);
        btnChooseMultiImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImages();
            }
        });
        imgProfile = (ImageView) findViewById(R.id.imageView_profile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallaryIntent = new Intent();
                gallaryIntent.setType("image/*");
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallaryIntent, "Select Image"), GALARY_REQUEST1);

            }
        });

        Button addOfferBtn = (Button) findViewById(R.id.btnAddOffer);
        addOfferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClientList(view);
            }
        });
    }

    private void updateLabelfrom() {

        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        from.getTimeInMillis();
        btnStartDay.setText(sdf.format(from.getTime()));

    }

    private void updateLabelto() {

        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        btnBackDay.setText(sdf.format(to.getTime()));
    }

    private void getImages() {
        Config config = new Config();
//        config.setCameraHeight(R.dimen.app_camera_height);
//        config.setToolbarTitleRes(R.string.custom_title);
        config.setSelectionMin(2);
        config.setSelectionLimit(5);
//        config.setSelectedBottomHeight(R.dimen.bottom_height);

        ImagePickerActivity.setConfig(config);
        Intent intent = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }

    public void addClientList(View v) {
        if (!Validate()) {
            Toast.makeText(mContext, R.string.error_notadd, Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setTitle(getString(R.string.add_offer));
        progressDialog.setCancelable(false);
        progressDialog.show();
        final String hotelName = inputHotelName.getText().toString().trim();
        final String deals = inputDeals.getText().toString().trim();
        final String price = inputPrice.getText().toString().trim();
        final String chairCount = inputChairCount.getText().toString().trim();
//        final String hotelLevel = spinnerHotelLevel.getSelectedItem().toString().trim();
//        final String busLevel = spinnerBusLevel.getSelectedItem().toString().trim();
        final String destLevel = spinnerDestLevel.getSelectedItem().toString().trim();
        final String transLevel = spinnerTransLevel.getSelectedItem().toString().trim();

        // get selected radio button from radioGroup
        int selectedIdOne = radio_group_one.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        radioButtonOne = (RadioButton) findViewById(selectedIdOne);
        if (radioButtonOne.getText() == null) {
            Snackbar.make(v, "Please Choice One", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Value_one = radioButtonOne.getText().toString();
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
        }

        StorageReference filepath = mystorage.child(getString(R.string.offer_imgs)).child(getString(R.string.photos) + myAuth.getCurrentUser().getUid().toString() + getString(R.string.profile));


        filepath.putFile(resultUri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddOfferActivity.this, "Fail to UpLoading", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String name = taskSnapshot.getMetadata().getName();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String downLoad_Url = downloadUrl.toString();

                profileImg = downLoad_Url;
                offerimage.setOfferImage(profileImg);
                progressDialog.dismiss();
                AddOffer(hotelName, deals, price, Integer.parseInt(chairCount),profileImg,
                        Value_one, Value_three, Value_two, transLevel, destLevel);
            }
        });



    }

    public boolean Validate() {
        boolean valid = true;
        String hotelName = inputHotelName.getText().toString().trim();
        String deals = inputDeals.getText().toString().trim();
        String price = inputPrice.getText().toString().trim();
        String chairCount = inputChairCount.getText().toString().trim();
        String destLevel = spinnerDestLevel.getSelectedItem().toString().trim();
        String transLevel = spinnerTransLevel.getSelectedItem().toString().trim();


        if (hotelName.isEmpty()) {
            inputHotelName.setError(getString(R.string.error_hotelname));
            Toast.makeText(mContext, R.string.error_hotelname, Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputHotelName.setError(null);
        }
        if (deals.isEmpty()) {
            inputDeals.setError(getString(R.string.error_deals));
            Toast.makeText(mContext, R.string.error_deals, Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputDeals.setError(null);
        }
        if (price.isEmpty()) {
            inputPrice.setError(getString(R.string.error_price));
            Toast.makeText(mContext, R.string.error_price, Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputPrice.setError(null);
        }
        if (chairCount.isEmpty()) {
            inputChairCount.setError(getString(R.string.error_chairCount));
            Toast.makeText(mContext, R.string.error_chairCount, Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputChairCount.setError(null);
        }
        if (destLevel.isEmpty()) {
            Toast.makeText(mContext, R.string.error_hotelLevel, Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (transLevel.isEmpty()) {
            Toast.makeText(mContext, R.string.error_busLevel, Toast.LENGTH_SHORT).show();
            valid = false;
        }

/*        if (profileImg.isEmpty()) {
            Toast.makeText(mContext, R.string.error_profileImg, Toast.LENGTH_SHORT).show();
            valid = false;
        }*/
        if (ChooseLocationActivity.lat == 0 || ChooseLocationActivity.lang == 0) {
            Toast.makeText(mContext, R.string.add_doc_tost_lat, Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            location = getAddress(ChooseLocationActivity.lat, ChooseLocationActivity.lang);
        }
        return valid;
    }

    private void AddOffer(String hotelName, String deals, String price, int numOfChairs,String profileImg,
                          String value_onehouse, String value_threestatus, String value_twotrans, String transLevel, String destLevel) {
        HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put(getString(R.string.timestamp), ServerValue.TIMESTAMP);

        DatabaseReference client_database = mClientsDatabase.child(ConstantsLabika.FIREBASE_LOCATION_OFFERS);
        String clientId = client_database.push().getKey();
        DatabaseReference offerDb = client_database.push();
        offerDb.child("attendStartTime").setValue(attendStartTime);
        offerDb.child("attendEndTime").setValue(attendEndTime);
        offerDb.child("startDay").setValue(startDay);
        offerDb.child("backDay").setValue(backDay);
        offerDb.child("offerImage").setValue(profileImg);
        offerDb.child("hotelName").setValue(hotelName);
        offerDb.child("companyKeyId").setValue(myAuth.getCurrentUser().getUid().toString());
        offerDb.child("deals").setValue(deals);
        offerDb.child("keyId").setValue(offerDb.getKey());
        offerDb.child("numOfChairs").setValue(numOfChairs);
        offerDb.child("lat").setValue(lat);
        offerDb.child("lang").setValue(lang);
        offerDb.child("location").setValue(location);
        offerDb.child("price").setValue(price);
        offerDb.child("contentImagesList").setValue(contentImgsArrayList);
        offerDb.child("startTime").setValue(startTime);
        offerDb.child("endTime").setValue(endTime);
        offerDb.child("destLevel").setValue(destLevel);
        offerDb.child("transLevel").setValue(transLevel);
        offerDb.child("value_onehouse").setValue(value_onehouse);
        offerDb.child("value_twotrans").setValue(value_twotrans);
        offerDb.child("value_threestatus").setValue(value_threestatus);
        offerDb.child("timestampJoined").setValue(timestampJoined);

        Intent mainIntent = new Intent(com.apps.labikaomra.activities.AddOfferActivity.this, CompanyOffersActivity.class);
        mainIntent.putExtra("company_user_id", company_user_id);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        progressDialog.dismiss();

        String img = offerimage.getOfferImage();

    }

    private void AddClientData(Uri uri, int position) {
        progressDialog.setTitle(getString(R.string.upload_multi_imgs));
        progressDialog.setCancelable(false);
        progressDialog.show();
        StorageReference filepath = mystorage.child(getString(R.string.offer_imgs)).child(getString(R.string.photos) + myAuth.getCurrentUser().getUid().toString() + getString(R.string.lists) + position);
        filepath.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
//                Toast.makeText(AddOfferActivity.this, R.string.error_notadd + exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(getString(R.string.error_notadd), getString(R.string.onfailure) + exception.getMessage());
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                contentImgsArrayList.add(downloadUrl.toString());
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(getString(R.string.profress), String.format(getString(R.string.onProgress),
                        taskSnapshot.getBytesTransferred() / 1024.0 / 1024.0));
                progressDialog.setTitle(getString(R.string.upload_multi_imgs) + taskSnapshot.getBytesTransferred() / 1024.0 / 1024.0 + "");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            location = getAddress(ChooseLocationActivity.lat, ChooseLocationActivity.lang);
            btnLocation.setText(location);
        }
        if (requestCode == GALARY_REQUEST1 && resultCode == RESULT_OK) {
            Uri resultUri = data.getData();
            CropImage.activity(resultUri).
                    setAspectRatio(1, 1).
                    setMinCropResultSize(20, 200)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(AddOfferActivity.this);
                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage("Please Wait for UpLoading....");
                progressDialog.show();

                 resultUri = result.getUri();
                File thumbin_path = new File(resultUri.getPath());
                imgProfile.setImageURI(resultUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    imgProfile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK) {
            image_uri = null;
            ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            if (image_uris != null) {
                contentImgsArrayList = new ArrayList<>();
                for (int i = 0; i < image_uris.size(); i++) {
                    Uri imgUri = Uri.parse("file://" + image_uris.get(i));
                    AddClientData(imgUri, i);
                }
            }
        }
    }

    protected Dialog onCreateDialog(int id) {

        // Get the calander
        c = Calendar.getInstance();

        // From calander get the year, month, day, hour, minute
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        switch (id) {
            case Date_id:

                // Open the datepicker dialog
                return new DatePickerDialog(com.apps.labikaomra.activities.AddOfferActivity.this, date_listener, year,
                        month, day);
            case Time_id:

                // Open the timepicker dialog
                return new TimePickerDialog(com.apps.labikaomra.activities.AddOfferActivity.this, time_listener, hour,
                        minute, false);

        }
        return null;
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(com.apps.labikaomra.activities.AddOfferActivity.this, Locale.getDefault());
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


