package com.apps.labikaomra.notifications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.apps.labikaomra.activities.ChoiceActivity;
import com.apps.labikaomra.activities.Home;
import com.apps.labikaomra.activities.OffersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    String title, price, starttime, endtime;
    MyNotification mNotificationManager;
    Intent intent;
    Context context;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        context = getApplicationContext();
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }

        }


    }

    private void sendPushNotification(JSONObject json) {
        FirebaseAuth.getInstance().signOut();
        mNotificationManager = new MyNotification(getApplicationContext());
        intent = new Intent(getApplicationContext(), Home.class);

        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");

            title = data.getString("title");
            price = data.getString("message");
            starttime = data.getString("starttime");
            endtime = data.getString("endtime");
            Log.e(TAG, "sendPushNotification: " + price + title);

            mNotificationManager.showSmallNotification(title, price, starttime, endtime, intent);


        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}
