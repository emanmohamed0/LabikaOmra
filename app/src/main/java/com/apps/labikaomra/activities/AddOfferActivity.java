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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddOfferActivity extends AppCompatActivity {


    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int Date_id = 0;
    private static final int Time_id = 1;
    public static double lat = 0, lang = 0;
    static Context mContext;
    static int GALARY_REQUEST1 = 2;
    DatabaseReference mClientsDatabase;
    StorageReference mystorage;
    Spinner spinnerHotelLevel, spinnerBusLevel;
    Button btnStartDay, btnAttendStartTime, btnStartTime, btnBackDay, btnAttendEndTime, btnEndTime, btnLocation, btnChooseMultiImage;
    ImageView imgProfile;
    Uri image_uri = null;
    ProgressDialog progressDialog;
    String profileImg = "", attendStartTime, startTime, attendEndTime, endTime, location;
    Long startDay, backDay;
    ArrayList<String> contentImgsArrayList;
    int dialogFlag = 0;
    Calendar c, b;
    Calendar from, to;
    String company_user_id;
    // Date picker dialog
//    DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {
//
//        @Override
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//            view.setMinDate(System.currentTimeMillis() - 1000);
//
////            // store the data in one string and set it to text
//////             date1 = String.valueOf(month) + "/" + String.valueOf(day)
//////                    + "/" + String.valueOf(year);
////            String myFormat = "EEE, MMM d"; //In which you need put here
////
////             c.get(year);
////             c.get(month);
////            int day = c.get(Calendar.DAY_OF_MONTH);
////            int hour = c.get(Calendar.HOUR_OF_DAY);
////            int minute = c.get(Calendar.MINUTE);
////
////            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
////            Long date1 = c.getTime().getTime();
////
////            if (dialogFlag == 0) {
////
////                startDay = date1;
////                btnStartDay.setText(sdf.format(c.getTime()));
////
////            } else if (dialogFlag == 1) {
////                backDay = date1;
////                btnBackDay.setText(sdf.format(c.getTime()));
////            }
//        }

//    DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {
//
//        @Override
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//            // store the data in one string and set it to text
//            String date1 = String.valueOf(month) + "/" + String.valueOf(day)
//                    + "/" + String.valueOf(year);
//            if (dialogFlag == 0) {
//                long diffInSec = c.getTimeInMillis();
//                Long startDayLong = c.getTime().getTime();
//
//                startDay = diffInSec;
//                btnStartDay.setText(date1);
//            } else if (dialogFlag == 1) {
//                long diffInSec = c.getTimeInMillis();
//
//                Long backDayLong = c.getTime().getTime();
//
//                backDay = diffInSec;
//                btnBackDay.setText(date1);
//            }
//        }
//
//    };
//    TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener() {
//
//        @Override
//        public void onTimeSet(TimePicker view, int hour, int minute) {
//            // store the data in one string and set it to text
//            String time1 = String.valueOf(hour) + ":" + String.valueOf(minute);
//            if (dialogFlag == 2) {
//                attendStartTime = time1;
//                btnAttendStartTime.setText(time1);
//            } else if (dialogFlag == 3) {
//                startTime = time1;
//                btnStartTime.setText(time1);
//            } else if (dialogFlag == 4) {
//                attendEndTime = time1;
//                btnAttendEndTime.setText(time1);
//            } else if (dialogFlag == 5) {
//                endTime = time1;
//                btnEndTime.setText(time1);
//            }
//        }
//    };
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

        company_user_id = getIntent().getStringExtra("company_user_id");
        mClientsDatabase = FirebaseDatabase.getInstance().getReference();
        mystorage = FirebaseStorage.getInstance().getReference();
        myAuth = FirebaseAuth.getInstance();
        mContext = getBaseContext();
        inputHotelName = (EditText) findViewById(R.id.hotelNameField);
        inputDeals = (EditText) findViewById(R.id.dealsField);
        inputPrice = (EditText) findViewById(R.id.priceField);
        inputChairCount = (EditText) findViewById(R.id.chairsNumField);
        spinnerHotelLevel = (Spinner) findViewById(R.id.spinner_hotel_level);
        spinnerBusLevel = (Spinner) findViewById(R.id.spinner_bus_level);
        btnStartDay = (Button) findViewById(R.id.btnStartDay);
        btnAttendStartTime = (Button) findViewById(R.id.btnAttendStartTime);
        btnStartTime = (Button) findViewById(R.id.btnStartTime);
        btnBackDay = (Button) findViewById(R.id.btnBackDay);
        btnAttendEndTime = (Button) findViewById(R.id.btnAttendEndTime);
        btnEndTime = (Button) findViewById(R.id.btnEndTime);
        btnLocation = (Button) findViewById(R.id.btnLocation);
        progressDialog = new ProgressDialog(com.apps.labikaomra.activities.AddOfferActivity.this);

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

                Intent galaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galaryIntent.setType(getString(R.string.image));
                galaryIntent.putExtra(getString(R.string.crop), "true");
                galaryIntent.putExtra(getString(R.string.aspc_x), 0);
                galaryIntent.putExtra(getString(R.string.aspc_y), 0);
                try {
                    galaryIntent.putExtra(getString(R.string.return_data), true);
                    startActivityForResult(Intent.createChooser(galaryIntent, getString(R.string.completeaction)), GALARY_REQUEST1);

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(com.apps.labikaomra.activities.AddOfferActivity.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button addOfferBtn = (Button) findViewById(R.id.btnAddOffer);
        addOfferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClientList();
            }
        });
    }

    private void updateLabelfrom() {

        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        checkInDate = TimeUnit.MILLISECONDS.toSeconds(from.getTime().getTime());
//         checkInDate = from.getTime().getTime();
        from.getTimeInMillis();
//        OffersActivity.checkInDate = checkInDate;
        btnStartDay.setText(sdf.format(from.getTime()));

    }

    private void updateLabelto() {

        String myFormat = "EEE, MMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

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

    public void addClientList() {
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
        final String hotelLevel = spinnerHotelLevel.getSelectedItem().toString().trim();
        final String busLevel = spinnerBusLevel.getSelectedItem().toString().trim();
        AddOffer(hotelName, busLevel, hotelLevel, deals, price, Integer.parseInt(chairCount));

    }

    public boolean Validate() {
        boolean valid = true;
        String hotelName = inputHotelName.getText().toString().trim();
        String deals = inputDeals.getText().toString().trim();
        String price = inputPrice.getText().toString().trim();
        String chairCount = inputChairCount.getText().toString().trim();
        String hotelLevel = spinnerHotelLevel.getSelectedItem().toString().trim();
        String busLevel = spinnerBusLevel.getSelectedItem().toString().trim();


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
        if (hotelLevel.isEmpty()) {
            Toast.makeText(mContext, R.string.error_hotelLevel, Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (busLevel.isEmpty()) {
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

    private void AddOffer(String hotelName, String busLevel, String hotelLevel, String deals, String price, int numOfChairs) {
        HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put(getString(R.string.timestamp), ServerValue.TIMESTAMP);

        DatabaseReference client_database = mClientsDatabase.child(ConstantsLabika.FIREBASE_LOCATION_OFFERS);
        String clientId = client_database.push().getKey();
        Offer client = new Offer(hotelName, startDay, attendStartTime, startTime, busLevel, hotelLevel, backDay, attendEndTime, endTime, deals, price, location, numOfChairs, timestampJoined, lat, lang, profileImg, contentImgsArrayList, myAuth.getCurrentUser().getUid().toString(), clientId);
        client_database.child(clientId).setValue(client)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.done, Toast.LENGTH_SHORT).show();
                            Intent mainIntent = new Intent(com.apps.labikaomra.activities.AddOfferActivity.this, CompanyOffersActivity.class);
                            mainIntent.putExtra("company_user_id",company_user_id);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                        } else {
                            Toast.makeText(mContext, R.string.error_notadd, Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, R.string.error_notadd, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
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
                progressDialog.setTitle(getString(R.string.upload_multi_imgs)+taskSnapshot.getBytesTransferred() / 1024.0 / 1024.0 + "");
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
        if (requestCode == GALARY_REQUEST1 && resultCode == Activity.RESULT_OK) {
            image_uri = data.getData();
            imgProfile.setImageURI(image_uri);
            image_uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
                imgProfile.setImageBitmap(bitmap);
                uploadProfileImg(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            Bundle extras2 = getIntent().getExtras();
//            if (extras2 != null) {
//                Bitmap photo = extras2.getParcelable(getString(R.string.data));
////                imgProfile.setImageBitmap(photo);
//                uploadProfileImg(photo);
//            }
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

    private void uploadProfileImg(final Bitmap photo) {
        progressDialog.setTitle(getString(R.string.offer_img));
        progressDialog.setCancelable(false);
        progressDialog.show();
        StorageReference filepath = mystorage.child(getString(R.string.offer_imgs)).child(getString(R.string.photos) + myAuth.getCurrentUser().getUid().toString() + getString(R.string.profile));
        // Get the data from an ImageView as bytes
//        imgProfile.setDrawingCacheEnabled(true);
//        imgProfile.buildDrawingCache();
//        Bitmap bitmap = imgProfile.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = filepath.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
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
                profileImg = downloadUrl.toString();

                imgProfile.setImageBitmap(photo);
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(getString(R.string.profress), String.format(getString(R.string.onProgress),
                        taskSnapshot.getBytesTransferred() / 1024.0 / 1024.0));
                progressDialog.setTitle(getString(R.string.offer_img)+taskSnapshot.getBytesTransferred() / 1024.0 / 1024.0 + "");
            }
        });
    }

//    protected Dialog onCreateDialog(int id) {
//
//        // Get the calander
//        c = Calendar.getInstance();
//
//        // From calander get the year, month, day, hour, minute
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//        int hour = c.get(Calendar.HOUR_OF_DAY);
//        int minute = c.get(Calendar.MINUTE);
//
//        switch (id) {
//            case Date_id:
//
//                // Open the datepicker dialog
//                return new DatePickerDialog(com.apps.labikaomra.activities.AddOfferActivity.this, date_listener, year,
//                        month, day);
//            case Time_id:
//
//                // Open the timepicker dialog
//                return new TimePickerDialog(com.apps.labikaomra.activities.AddOfferActivity.this, time_listener, hour,
//                        minute, false);
//
//        }
//        return null;
//    }

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
