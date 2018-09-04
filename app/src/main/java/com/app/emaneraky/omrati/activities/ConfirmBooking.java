package com.app.emaneraky.omrati.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.emaneraky.omrati.ConstantsLabika;
import com.app.emaneraky.omrati.R;
import com.app.emaneraky.omrati.constans.Global;
import com.app.emaneraky.omrati.models.DataFacility;
import com.app.emaneraky.omrati.models.ListBookingCompany;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

public class ConfirmBooking extends AppCompatActivity {
    ImageView putImg, getImg;
    TextView bankNum;
    Button mConfirm, temconfirm;
    ProgressDialog progressDialog;
    DatabaseReference myDatabase;
    FirebaseAuth auth;
    StorageReference mystorage;

    String CompanyKeyId, mUser_Id, KeyId, bank;
    public static ListBookingCompany listBookingCompany;
    private static final int CAM_REQUEST = 2;
    private static final int GAL_REQUEST = 1;

    //    String image_uri = "";
    Uri image_uriget = null;
    byte[] thumb_byte;
    TextView lbl_name;
    ImageView img_back;

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ConfirmBooking.this, DialogBooking.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);


        lbl_name = (TextView) findViewById(R.id.lbl_name);
        lbl_name.setText(getString(R.string.titleconfirm));
        img_back = (ImageView) findViewById(R.id.img_back);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CompanyKeyId = getIntent().getStringExtra("CompanyKeyId");
        mUser_Id = Global.get_UserID(ConfirmBooking.this, "mUser_Id");
        auth = FirebaseAuth.getInstance();
        if (mUser_Id == null) {
            mUser_Id = auth.getCurrentUser().getUid();
        }
        KeyId = getIntent().getStringExtra("KeyId");

        putImg = (ImageView) findViewById(R.id.putimg);
        getImg = (ImageView) findViewById(R.id.getimg);
        bankNum = (TextView) findViewById(R.id.banknum);
        mConfirm = (Button) findViewById(R.id.btnconfirm);
        temconfirm = (Button) findViewById(R.id.temconfirm);
        myDatabase = FirebaseDatabase.getInstance().getReference();
        myDatabase.keepSynced(true);

        mystorage = FirebaseStorage.getInstance().getReference();

        getBankAccount(CompanyKeyId);

        getImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (putImg.getDrawable() == null) {
                    Snackbar.make(view, getString(R.string.pls) + " " + getString(R.string.Copyofreceipt), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    completeRegister(view);
                }
            }
        });

        temconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTemporaryBooking();
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmBooking.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), GAL_REQUEST);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAM_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAM_REQUEST && resultCode == Activity.RESULT_OK) {
            image_uriget = data.getData();
            putImg.setImageURI(image_uriget);
            Bundle extras2 = data.getExtras();
            if (extras2 != null) {
                Bitmap photo = extras2.getParcelable(getString(R.string.data));
                putImg.setImageBitmap(photo);
            }

        } else if (requestCode == GAL_REQUEST && resultCode == Activity.RESULT_OK) {
            image_uriget = data.getData();
//            image_uriget.getPath();
//
//            File thumbin_path = new File(image_uriget.getPath());
//          //this for compress image to load fast
//            Bitmap thumbin_ImageBitmap = new Compressor(this)
//                    .setMaxWidth(640)
//                    .setMaxHeight(480)
//                    .setQuality(75).compressToBitmap(thumbin_path);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            thumbin_ImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            thumb_byte = baos.toByteArray();

            putImg.setImageURI(image_uriget);

            Bundle extras2 = data.getExtras();
            if (extras2 != null) {
                Bitmap photo = extras2.getParcelable(getString(R.string.data));
                putImg.setImageBitmap(photo);
            }
        }

    }

    private void mTemporaryBooking() {
        HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put(getString(R.string.timestamp), ServerValue.TIMESTAMP);
        String bookingid = listBookingCompany.getBookingId();
        String firstname = listBookingCompany.getFirstName();
        String lastname = listBookingCompany.getLastName();
        String email = listBookingCompany.getEmail();
        String address = listBookingCompany.getAddress();
        String phone = listBookingCompany.getPhoneNum();
        String idCard = listBookingCompany.getIDcard();
        List<DataFacility> facilityList = listBookingCompany.getDataFacilities();
        Toast.makeText(ConfirmBooking.this, getString(R.string.add_book), Toast.LENGTH_SHORT).show();

        ListBookingCompany listBookingCompany = new ListBookingCompany(bookingid, CompanyKeyId, firstname,
                lastname, email, address, phone, idCard, facilityList, timestampJoined);

        Temporarybooking(listBookingCompany);
    }

    private void completeRegister(View view) {

        if (image_uriget == null) {
            Snackbar.make(view, getString(R.string.pls) + " " + getString(R.string.Copyofreceipt), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            progressDialog = new ProgressDialog(ConfirmBooking.this);
            progressDialog.setMessage(getString(R.string.adding_book));
            progressDialog.setCancelable(false);
            progressDialog.show();
            StorageReference filepath = mystorage.child("Bookimgs").child(image_uriget.getLastPathSegment());
            filepath.putFile(image_uriget).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    String firstname = listBookingCompany.getFirstName();
                    String lastname = listBookingCompany.getLastName();
                    String email = listBookingCompany.getEmail();
                    String address = listBookingCompany.getAddress();
                    String phone = listBookingCompany.getPhoneNum();
                    String idCard = listBookingCompany.getIDcard();
                    List<DataFacility> facilityList = listBookingCompany.getDataFacilities();
                    Toast.makeText(ConfirmBooking.this, getString(R.string.add_book), Toast.LENGTH_SHORT).show();

                    ListBookingCompany listBookingCompany = new ListBookingCompany(KeyId, CompanyKeyId, firstname,
                            lastname, email, address, phone, idCard, facilityList, downloadUrl.toString());

                    bookingCompany(listBookingCompany);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(getString(R.string.profress), String.format(getString(R.string.onProgress),
                            taskSnapshot.getBytesTransferred() / 1024.0 / 1024.0));
                }
            });


        }

    }

    public void bookingUser(ListBookingCompany booking) {

        DatabaseReference client_database1 = myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_BOOKING).child(auth.getCurrentUser().getUid());
        String clientId1 = KeyId;

        client_database1.child(clientId1).setValue(booking)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            finish();
                            progressDialog.dismiss();
//                            Toast.makeText(getBaseContext(), R.string.add_book, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), R.string.error_notadd, Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure: Not add to fav: " + e.getMessage());
                Toast.makeText(getBaseContext(), R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void Temporarybooking(final ListBookingCompany booking) {
        progressDialog = new ProgressDialog(ConfirmBooking.this);
        progressDialog.setMessage(getString(R.string.tempBooking));
        progressDialog.setCancelable(false);
        progressDialog.show();
        DatabaseReference client_database1 = myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_TEMBOOKING_COMPANY).child(CompanyKeyId);
        String clientId1 = KeyId;

        client_database1.child(clientId1).setValue(booking)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Global.set_UserId(ConfirmBooking.this, "mUser_Id", mUser_Id);
                            Intent intent = new Intent(ConfirmBooking.this, Home.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), R.string.error_notadd, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure: Not add to fav: " + e.getMessage());
                Toast.makeText(getBaseContext(), R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void bookingCompany(final ListBookingCompany booking) {

        DatabaseReference client_database1 = myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_BOOKING_COMPANY).child(CompanyKeyId);
        String clientId1 = KeyId;

        client_database1.child(clientId1).setValue(booking)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            bookingUser(booking);
                            Intent intent = new Intent(ConfirmBooking.this, Home.class);
                            Global.set_UserId(ConfirmBooking.this, "mUser_Id", mUser_Id);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), R.string.error_notadd, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure: Not add to fav: " + e.getMessage());
                Toast.makeText(getBaseContext(), R.string.error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void getBankAccount(String CompanyKeyId) {
        myDatabase.child(ConstantsLabika.FIREBASE_LOCATION_COMPANY).child(CompanyKeyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bank = dataSnapshot.child("bank").getValue().toString();
                bankNum.setText(bank);
//                AlertDialog.Builder popupBuilder = new AlertDialog.Builder(DialogBooking.this);
//                TextView myMsg = new TextView(DialogBooking.this);
//                popupBuilder.setTitle(getString(R.string.bank));
//                myMsg.setText(bank);
//                myMsg.setTextColor(Color.parseColor("#f5970a"));
//                myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
//                popupBuilder.setView(myMsg);
//                popupBuilder.setPositiveButton(getString(R.string.Copyofreceipt), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(intent,CAM_REQUEST);
//                        bookingCompany(booking);
//                        finish();
//                    }
//                });
//                popupBuilder.show();
//                Toast.makeText(ConfirmBooking.this, "banking " + bank, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}