package com.app.emaneraky.omrati.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.app.emaneraky.omrati.R;

public class MyNotification {
    public static final int ID_BIG_NOTIFICATION = 234;
    public static final int ID_SMALL_NOTIFICATION = 235;

    private Context mCtx;

    public MyNotification(Context mCtx) {
        this.mCtx = mCtx;
    }

    public void showSmallNotification(String title, String message,String start,String end, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.omrati).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setDefaults(Notification.DEFAULT_SOUND)
//                .setSmallIcon(R.mipmap.omrati)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.omrati))
                .setContentText("Price ="+message+" StartDay:"+start+" BackDay:"+end)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
    }

//        //The method will return Bitmap from an image URL
//        private Bitmap getBitmapFromURL(String strURL) {
//            try {
//                URL url = new URL(strURL);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream input = connection.getInputStream();
//                Bitmap myBitmap = BitmapFactory.decodeStream(input);
//                return myBitmap;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
}