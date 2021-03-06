package com.app.emaneraky.omrati.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.emaneraky.omrati.ConstantsLabika;
import com.app.emaneraky.omrati.R;
import com.app.emaneraky.omrati.SharedPrefManager;
import com.app.emaneraky.omrati.Utils;
import com.app.emaneraky.omrati.constans.Global;
import com.app.emaneraky.omrati.models.User;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UserLoginActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "HomeActivity";
    private String idToken;
    public SharedPrefManager sharedPrefManager;
    private final Context mContext = this;
    private DatabaseReference myDatabase;
    private String name, email, CompanyKeyId, KeyId, from;
    private String photo;
    private Uri photoUri;
    private SignInButton mSignInButton;
    private Firebase userLocation;
    int numseat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        CompanyKeyId = getIntent().getStringExtra("CompanyKeyId");
        from = getIntent().getStringExtra("mNumSeat");
        KeyId = getIntent().getStringExtra("KeyId");
        numseat = getIntent().getIntExtra("numseat", 1);


        myDatabase = FirebaseDatabase.getInstance().getReference();
        mSignInButton = (SignInButton) findViewById(R.id.signin_google_btn);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);

        mSignInButton.setOnClickListener(this);

        configureSignIn();

        mAuth = FirebaseAuth.getInstance();

        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get signedIn user
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //if user is signed in, we call a helper method to save the user details to Firebase
                if (user != null) {
                    // User is signed in
//                        createUserInFirebaseHelper();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }
    private void createUserInFirebaseHelper() {
        final String encodedEmail = Utils.encodeEmail(email.toLowerCase());
        myDatabase = FirebaseDatabase.getInstance().getReference().child(ConstantsLabika.FIREBASE_LOCATION_USERS).child(mAuth.getCurrentUser().getUid().toString());
        HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put(ConstantsLabika.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        final User newUser = new User(name, photo, encodedEmail, timestampJoined, "user");
        myDatabase.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UserLoginActivity.this, "account Create",
                        Toast.LENGTH_SHORT).show();

                Finishing(newUser);
            }
        });
    }

    private void Finishing(User newUser) {
        if (from == null) {

        } else {
            if (from.equals("0")) {
                Intent returnIntent = new Intent(UserLoginActivity.this, Home.class);
                returnIntent.putExtra("CompanyKeyId", CompanyKeyId);
                returnIntent.putExtra("KeyId", KeyId);
//                returnIntent.putExtra("mUser_Id", mAuth.getCurrentUser().getUid().toString());
                Global.set_UserId(UserLoginActivity.this,"mUser_Id",mAuth.getCurrentUser().getUid().toString());
                startActivity(returnIntent);
            }
            else if (from.equals("1")) {
                showSeatDialog();
            }
        }
        finish();
    }

    // This method configures Google SignIn
    public void configureSignIn() {
// Configure sign-in to request the user's basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(UserLoginActivity.this.getResources().getString(R.string.web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
    }

    // This method is called when the signIn button is clicked on the layout
    // It prompts the user to select a Google account.
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // This IS the method where the result of clicking the signIn button will be handled
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, save Token and a state then authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                idToken = account.getIdToken();

                name = account.getDisplayName();
                email = account.getEmail();
                photoUri = account.getPhotoUrl();
                photo = photoUri.toString();

                // Save Data to SharedPreference
                sharedPrefManager = new SharedPrefManager(mContext);
                sharedPrefManager.saveIsLoggedIn(mContext, true);

                sharedPrefManager.saveEmail(mContext, email);
                sharedPrefManager.saveName(mContext, name);
                sharedPrefManager.savePhoto(mContext, photo);

                sharedPrefManager.saveToken(mContext, idToken);
                //sharedPrefManager.saveIsLoggedIn(mContext, true);

                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuthWithGoogle(credential);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Login Unsuccessful. ");
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    //After a successful sign into Google, this method now authenticates the user with Firebase
    private void firebaseAuthWithGoogle(AuthCredential credential) {
        showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(UserLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            createUserInFirebaseHelper();
//                                Toast.makeText(UserLoginActivity.this, "Login successful",
//                                        Toast.LENGTH_SHORT).show();


                        }
                        hideProgressDialog();
                    }
                });
    }

    public void showSeatDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.seat_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText edtchair = (EditText) dialogView.findViewById(R.id.chairField);

        dialogBuilder.setTitle(getString(R.string.enterseat));
        dialogBuilder.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edtchair.getText().toString().isEmpty()) {
                    Toast.makeText(UserLoginActivity.this, "Please Enter All Data ^_^ ", Toast.LENGTH_LONG).show();
                } else {
//                    seatback = Integer.parseInt(edtchair.getText().toString());
                    Intent booking = new Intent(UserLoginActivity.this, DialogBooking.class);
                    booking.putExtra("CompanyKeyId", CompanyKeyId);
//                    booking.putExtra("mUser_Id", mAuth.getCurrentUser().getUid().toString());
                    booking.putExtra("numseat", numseat);
                    booking.putExtra("mNumSeat", edtchair.getText().toString());
                    booking.putExtra("KeyId", KeyId);
                    Global.set_UserId(UserLoginActivity.this,"mUser_Id",mAuth.getCurrentUser().getUid().toString());
                    startActivity(booking);
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
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().signOut();
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View view) {

        Utils utils = new Utils(this);
        int id = view.getId();

        if (id == R.id.signin_google_btn) {
            if (utils.isNetworkAvailable()) {
                signIn();
            } else {
                Toast.makeText(UserLoginActivity.this, "Oops! no internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}