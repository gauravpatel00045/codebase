package com.example.mvc.codebase.firebaseNotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.R;
import com.example.mvc.codebase.activity.LoginActivity;
import com.example.mvc.codebase.activity.MainActivity;
import com.example.mvc.codebase.activity.SplashActivity;
import com.example.mvc.codebase.models.CustomerDetails;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.Debug;
import com.example.mvc.codebase.utils.MyLifecycleHandler;
import com.google.android.gms.gcm.GcmListenerService;

/*
 * This class handle all messaging service from gcm
 * in onMessageReceived method, we receive the push notification message
 *
 * 1. How to use GcmListenerService
 *
 *   1.1) Import latest gcm gradle file in app level build.gradle
 *        @link : https://firebase.google.com/docs/android/setup#available_libraries
 *
 *        In your app-level build.gradle file, declare Google Play Services as a dependency:
 *
 *              apply plugin: 'com.android.application'
 *
 *              android {
 *                   // ...
 *                  }
 *
 *              dependencies {
 *                  // ...
 *                  compile 'com.google.android.gms:play-services-gcm:10.2.1'
 *
 *                   // Getting a "Could not find" error? Make sure you have
 *                  // the latest Google Repository in the Android SDK manager
 *              }
 *          till 23-3-2017 latest version is 10.2.1.
 *
 *                  // ADD THIS AT THE BOTTOM
 *               apply plugin: 'com.google.gms.google-services'
 *
 *   1.2) Define PushNotificationService in your app manifest
 *        To use GcmListenerService you need to add the following in your app manifest
 *         refer below link for more information
 *         @link : https://developers.google.com/android/reference/com/google/android/gms/gcm/GcmListenerService
 *
 *              <service
 *                   android:name=".PushNotificationService"
 *                   android:exported="false">
 *                <intent-filter>
 *                      <action android:name="com.google.android.c2dm.intent.RECEIVE" />
 *                </intent-filter>
 *             </service>
 *
 *             <receiver
 *                   android:name="com.google.android.gms.gcm.GcmReceiver"
 *                   android:exported="true"
 *                   android:permission="com.google.android.c2dm.permission.SEND">
 *                <intent-filter>
 *                      <action android:name="com.google.android.c2dm.intent.RECEIVE" />
 *                      <category android:name="com.example.mvc.codebase" />
 *               </intent-filter>
 *            </receiver>
 *
 */

public class PushNotificationService extends GcmListenerService {

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        super.onMessageReceived(s, bundle);

        String message = bundle.getString(Constants.KEY_NOTIFICATION_MESSAGE);

        if (!TextUtils.isEmpty(message)) {

            Debug.trace(Constants.KEY_NOTIFICATION_MESSAGE_DATA, message);
            handleNotification(message);
        }
    }

    /**
     * This method manage the flow of notification based on application lifecycle like
     * Notification should be generate or do other action (e.g. show popUp, Update data)
     *
     * @param message (String) : message or data that comes in notification
     */
    private void handleNotification(String message) {
        if (MyLifecycleHandler.isApplicationVisible()) {
            if (CustomerDetails.isLoggedIn()) {
                Intent intent = new Intent();
                intent.setAction(Constants.KEY_SHOW_POP_UP);
                intent.putExtra(Constants.KEY_FROM_NOTIFICATION, message);
                sendBroadcast(intent);
            } else {
                generateNotification(message);
            }
        } else {
            generateNotification(message);
        }

    }


    /**
     * This method creates notification and display it in notification bar.
     * In notification click action it opens relevant activity
     *
     * @param messageBody (String) : message or data that to be shown or update
     */
    private void generateNotification(final String messageBody) {

        Intent intent;

        if (MyApplication.isAppRunning) {

            if (CustomerDetails.isLoggedIn()) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
        } else {
            intent = new Intent(this, SplashActivity.class);
        }

        intent.putExtra(Constants.KEY_FROM_NOTIFICATION, messageBody);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, Constants.NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notificationBuilder.setSmallIcon(R.mipmap.ic_codebase_launcher);
        notificationBuilder.setContentTitle(getString(R.string.app_name));
        notificationBuilder.setContentText(messageBody);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Constants.NOTIFICATION_REQUEST_CODE, notificationBuilder.build());
    }
}
