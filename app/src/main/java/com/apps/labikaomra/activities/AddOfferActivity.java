package com.apps.labikaomra.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apps.labikaomra.ConstantsLabika;
import com.apps.labikaomra.R;
import com.apps.labikaomra.notifications.MyVolley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.gun0912.tedpicker.Config;

public class AddOfferActivity extends AppCompatActivity {

    private static final int RUSLET_LOAD_LOCATION = 1;
    private static final int INTENT_REQUEST_GET_IMAGES = 2;
    static int GALARY_REQUEST1 = 3;
    TextView addbtn, total;
    ImageView addImage;
    Uri uri_Post_usre_profile_image;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;
    ////////////////////////////////////////////////////////////
    String Value_one;
    String Value_two;
    private RadioGroup radio_group_one, radio_group_two;
    private RadioButton radioButtonOne, radioButtonTwo;
    private static final int Date_id = 0;
    private static final int Time_id = 1;
    public static double lat = 0, lang = 0;
    static Context mContext;
    DatabaseReference mClientsDatabase;
    StorageReference mystorage;
    Spinner spinnerDestLevel;
    Button btnStartDay, btnAttendStartTime, btnStartTime, btnBackDay, btnAttendEndTime, btnEndTime, btnLocation, btnChooseMultiImage;
    ProgressDialog progressDialog;
    String attendStartTime, startTime, attendEndTime, endTime, location;
    Long startDay, backDay;
    ArrayList<String> contentImgsArrayList;
    int dialogFlag = 0;
    Calendar c, from, to;
    String company_user_id;
    private EditText inputHotelName, inputDeals, inputPriceTotal, inputPriceBus, inputPriceplace, inputChairCount;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_OFFERS);

        c = Calendar.getInstance();
        from = Calendar.getInstance();
        to = Calendar.getInstance();
        company_user_id = getIntent().getStringExtra("company_user_id");
        mClientsDatabase = FirebaseDatabase.getInstance().getReference();
        mystorage = FirebaseStorage.getInstance().getReference();

        mContext = getBaseContext();
        inputHotelName = (EditText) findViewById(R.id.hotelNameField);
        inputDeals = (EditText) findViewById(R.id.dealsField);
        inputPriceBus = (EditText) findViewById(R.id.pricebusField);
        inputPriceplace = (EditText) findViewById(R.id.priceplaceField);
        inputPriceTotal = (EditText) findViewById(R.id.pricetotalField);
        inputChairCount = (EditText) findViewById(R.id.chairsNumField);
        spinnerDestLevel = (Spinner) findViewById(R.id.spinner_dest);
//        spinnerTransLevel = (Spinner) findViewById(R.id.spinner_trans);
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

        addImage = (ImageView) findViewById(R.id.imageView_profile);
        addbtn = (TextView) findViewById(R.id.btnAddOffer);


        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMap = new Intent(mContext, ChooseLocationActivity.class);
                startActivityForResult(intentMap, 2);
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallaryIntent = new Intent();
                gallaryIntent.setType("image/*");
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallaryIntent, "Select Image"), 1);

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
                DatePickerDialog mDate = new DatePickerDialog(AddOfferActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, datefrom, from
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
                DatePickerDialog mDate = new DatePickerDialog(AddOfferActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateto, to
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
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Validate()) {
                    Toast.makeText(mContext, R.string.error_notadd, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (startDay >= backDay) {
                    Toast.makeText(AddOfferActivity.this, "خطأ فى تاريخ الذهاب والعودة", Toast.LENGTH_SHORT).show();
                    createDialog();

                } else {
                    addOffer(view);
//                    createDialogTotal(inputChairCount.getText().toString(), inputPrice.getText().toString());

                }
            }
        });
    }

    public void addOffer(final View view) {
        final String hotelName = inputHotelName.getText().toString().trim();
        final String deals = inputDeals.getText().toString().trim();
        final String priceBus = inputPriceBus.getText().toString().trim();
        final String pricePlace = inputPriceplace.getText().toString().trim();
        final String priceTotal = inputPriceTotal.getText().toString().trim();

        final String chairCount = inputChairCount.getText().toString().trim();
        final String destLevel = spinnerDestLevel.getSelectedItem().toString().trim();

        if (uri_Post_usre_profile_image != null) {
            progressDialog.setTitle(getString(R.string.add_offer));
            progressDialog.setCancelable(false);
            progressDialog.show();
            StorageReference filepath = mStorageReference.child("OfferImgs").child(uri_Post_usre_profile_image.getLastPathSegment());
            filepath.putFile(uri_Post_usre_profile_image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(getString(R.string.timestamp), ServerValue.TIMESTAMP);
                    Toast.makeText(AddOfferActivity.this, " Upload Done", Toast.LENGTH_SHORT).show();
                    Uri uri_load = taskSnapshot.getDownloadUrl();
                    DatabaseReference offerDb = mDatabase.push();
                    offerDb.child("offerImage").setValue(uri_load.toString());
                    offerDb.child("attendStartTime").setValue(attendStartTime);
                    offerDb.child("attendEndTime").setValue(attendEndTime);
                    offerDb.child("startDay").setValue(startDay);
                    offerDb.child("backDay").setValue(backDay);
                    offerDb.child("hotelName").setValue(hotelName);
                    offerDb.child("companyKeyId").setValue(mAuth.getCurrentUser().getUid().toString());
                    offerDb.child("deals").setValue(deals);
                    offerDb.child("keyId").setValue(offerDb.getKey());
                    offerDb.child("numOfChairs").setValue(Integer.parseInt(chairCount));
                    offerDb.child("lat").setValue(ChooseLocationActivity.lat);
                    offerDb.child("lang").setValue(ChooseLocationActivity.lang);
                    offerDb.child("location").setValue(location);
                    offerDb.child("priceBus").setValue(priceBus);
                    offerDb.child("pricePlace").setValue(pricePlace);
                    offerDb.child("priceTotal").setValue(priceTotal);
                    offerDb.child("contentImagesList").setValue(contentImgsArrayList);
                    offerDb.child("startTime").setValue(startTime);
                    offerDb.child("endTime").setValue(endTime);
                    offerDb.child("destLevel").setValue(destLevel);
//                offerDb.child("transLevel").setValue(transLevel);
                    offerDb.child("value_onehouse").setValue(Value_one);
                    offerDb.child("value_twotrans").setValue(Value_two);
                    offerDb.child("timestampJoined").setValue(timestampJoined);

                    sendMultiplePush("New Offer", priceTotal, getDate(startDay, "EEE, MMM d"), getDate(backDay, "EEE, MMM d"));
                    Intent mainIntent = new Intent(AddOfferActivity.this, CompanyOffersActivity.class);
                    mainIntent.putExtra("company_user_id", company_user_id);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                    progressDialog.dismiss();

                }
            });

        } else {
            Snackbar.make(view, getString(R.string.pls) + " " + getString(R.string.Copyofreceipt), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void sendMultiplePush(final String title, final String price, final String startTime, final String endTime) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://fettered-disability.000webhostapp.com/SendAlldevices.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(AddOfferActivity.this, response, Toast.LENGTH_LONG).show();
                        Log.e("log", "onResponse: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", price);
                params.put("starttime", startTime);
                params.put("endtime", endTime);

                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void createDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(AddOfferActivity.this).create();
        alertDialog.setTitle(getString(R.string.alert));
        alertDialog.setMessage(getString(R.string.dialogoffer));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

//    public void createDialogTotal(String inputChairCount, String inputPrice) {
//        int chair = Integer.parseInt(inputChairCount);
//        int price = Integer.parseInt(inputPrice);
//        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
//                .setTitle(getString(R.string.alert))
//                .setMessage(getString(R.string.total) + " = " + (chair * price) + " riyal")
//                .setIcon(R.drawable.ic_attach_money_black_24dp)
//
//                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        //your Adding code
//                        addOffer();
//                        dialog.dismiss();
//                    }
//
//                })
//                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.dismiss();
//
//                    }
//                })
//                .create();
//        myQuittingDialogBox.show();
//    }

    private void getImages() {
        Config config = new Config();
//        config.setCameraHeight(R.dimen.app_camera_height);
//        config.setToolbarTitleRes(R.string.custom_title);
        config.setSelectionMin(2);
        config.setSelectionLimit(4);
//        config.setSelectedBottomHeight(R.dimen.bottom_height);
        ImagePickerActivity.setConfig(config);
        Intent intent = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent, 3);
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

    public boolean Validate() {
        boolean valid = true;
        String hotelName = inputHotelName.getText().toString().trim();
        String deals = inputDeals.getText().toString().trim();
        String priceBus = inputPriceBus.getText().toString().trim();
        String pricePlace = inputPriceplace.getText().toString().trim();
        String priceTotal = inputPriceTotal.getText().toString().trim();

        String chairCount = inputChairCount.getText().toString().trim();
        String destLevel = spinnerDestLevel.getSelectedItem().toString().trim();
//        String transLevel = spinnerTransLevel.getSelectedItem().toString().trim();

        radio();
        if (hotelName.isEmpty()) {
            inputHotelName.setError(getString(R.string.error_hotelname));
        } else {
            inputHotelName.setError(null);
        }
        if (deals.isEmpty()) {
            inputDeals.setText("");
//            inputDeals.setError(getString(R.string.error_deals));
//            Toast.makeText(mContext, R.string.error_deals, Toast.LENGTH_SHORT).show();
            valid = true;
        } else {
            inputDeals.setError(null);
        }
        if (priceBus.isEmpty()) {
            inputPriceBus.setError(getString(R.string.error_price));
            valid = false;
        } else {
            inputPriceBus.setError(null);
        }
        if (pricePlace.isEmpty()) {
            inputPriceplace.setError(getString(R.string.error_price));
            valid = false;
        } else {
            inputPriceplace.setError(null);
        }
        if (priceTotal.isEmpty()) {
            inputPriceTotal.setError(getString(R.string.error_price));
            valid = false;
        } else {
            inputPriceTotal.setError(null);
        }
        if (chairCount.isEmpty()) {
            inputChairCount.setError(getString(R.string.error_chairCount));
            valid = false;
        } else {
            inputChairCount.setError(null);
        }
        if (destLevel.isEmpty()) {
            Toast.makeText(mContext, R.string.error_hotelLevel, Toast.LENGTH_SHORT).show();
            valid = false;
        }
//        if (transLevel.isEmpty()) {
//            Toast.makeText(mContext, R.string.error_busLevel, Toast.LENGTH_SHORT).show();
//            valid = false;
//        }

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

    void radio() {
        // get selected radio button from radioGroup
        int selectedIdOne = radio_group_one.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        radioButtonOne = (RadioButton) findViewById(selectedIdOne);
        if (radioButtonOne.getText() == null) {

        } else {
            Value_one = radioButtonOne.getText().toString();
        }
        // get selected radio button from radioGroup
        int selectedIdTwo = radio_group_two.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        radioButtonTwo = (RadioButton) findViewById(selectedIdTwo);
        if (radioButtonTwo.getText() == null) {

        } else {
            Value_two = radioButtonTwo.getText().toString();
        }


    }

    private void AddClientData(Uri uri, int position) {
        progressDialog.setTitle(getString(R.string.upload_multi_imgs));
        progressDialog.setCancelable(false);
        progressDialog.show();
        StorageReference filepath = mystorage.child(getString(R.string.offer_imgs)).child(uri.getLastPathSegment() + getString(R.string.lists) + position);
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 2) {
                location = getAddress(ChooseLocationActivity.lat, ChooseLocationActivity.lang);
                btnLocation.setText(location);
            }
            if (requestCode == 1 && resultCode == RESULT_OK) {
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

                    Uri selectedimage = result.getUri();
                    addImage.setImageURI(selectedimage);
                    uri_Post_usre_profile_image = selectedimage;
                    addImage.setVisibility(View.VISIBLE);
                    addImage.setBackground(null);

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedimage);
                        addImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
            if (requestCode == 3 && resultCode == RESULT_OK) {
                ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                if (image_uris != null) {
                    contentImgsArrayList = new ArrayList<>();
                    for (int i = 0; i < image_uris.size(); i++) {
                        Uri imgUri = Uri.parse("file://" + image_uris.get(i));
                        AddClientData(imgUri, i);
                    }
                }
            }

        } catch (Exception e) {

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
            if (addresses.size() == 0) {
            } else {
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
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return "No Get Address";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}