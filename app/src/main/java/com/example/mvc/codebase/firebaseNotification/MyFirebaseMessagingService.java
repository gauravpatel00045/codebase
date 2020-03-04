package com.example.mvc.codebase.firebaseNotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.R;
import com.example.mvc.codebase.activity.LoginActivity;
import com.example.mvc.codebase.activity.MainActivity;
import com.example.mvc.codebase.activity.SplashActivity;
import com.example.mvc.codebase.models.CustomerDetails;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.MyLifecycleHandler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * This class handle firebase messaging service.
 * <br>{@link MyFirebaseMessagingService#onMessageReceived} method give the push notification message
 * from the server.</br>
 * <p>
 * To use <Strong>FirebaseMessagingService</Strong> you need to add the following in your app manifest
 * </p>
 * <pre>{@code
 *         <service
 *           android:name=".MyFirebaseMessagingService">
 *              <intent-filter>
 *                   <action android:name="com.google.firebase.MESSAGING_EVENT"/>
 *              </intent-filter>
 *        </service>
 *
 * }</pre>
 * refer below link for more information
 *
 * @see <a href="https://firebase.google.com/docs/notifications/android/console-audience"> FirebaseMessagingService </a>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // Variable declaration
    private static final String TAG = MyFirebaseMessagingService.class.getName();


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> messageBody;
        String message;
        // Data comes when notification will be sent from firebase console. You do not need to test
        // this condition when notification will sent through local server.
        if (remoteMessage.getNotification() != null) {
            message = remoteMessage.getNotification().getBody();
            handleNotification(message);
        } else {
            // Data comes when notification will send from local server.
            if (remoteMessage.getData() != null && !remoteMessage.getData().isEmpty()) {
                messageBody = remoteMessage.getData();

                if (messageBody.containsKey(Constants.MESSAGE)) {
                    message = messageBody.get(Constants.MESSAGE);

                    handleNotification(message);
                }
            }
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
