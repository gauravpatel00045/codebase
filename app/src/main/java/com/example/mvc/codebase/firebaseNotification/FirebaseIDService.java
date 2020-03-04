package com.example.mvc.codebase.firebaseNotification;

import com.example.mvc.codebase.helper.PrefHelper;
import com.example.mvc.codebase.models.CustomerDetails;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.Debug;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * This class extends the FirebaseInstanceIdService.
 * <br>It runs in background and gives the fresh device token from firebase
 * in {@link FirebaseIDService#onTokenRefresh} method</br>
 * <p>
 * To use FirebaseIDService you need to add the following in your app manifest
 * </p>
 * <pre>{@code
 *     <service
 *       android:name=".firebaseNotification.FirebaseIDService">
 *         <intent-filter>
 *               <action android:name="com.google.firebase.INSTANCE_ID_EVENT"/>
 *         </intent-filter>
 *     </service>
 * }</pre>
 */
public class FirebaseIDService extends FirebaseInstanceIdService {

    //variable declaration
    private static final String TAG = FirebaseIDService.class.getName();


    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Debug.trace(Constants.DEBUG_KEY_FIREBASE_DEVICE_TOKEN, token);
        sendRegistrationToServer(token);
    }

    /**
     * This method to store the token on your server
     *
     * @param token (String) : firebase token
     */
    private void sendRegistrationToServer(String token) {
        PrefHelper.getInstance().setString(PrefHelper.KEY_DEVICE_TOKEN, token);

        DeviceTokenModel deviceTokenModel = new DeviceTokenModel();
        if (CustomerDetails.isLoggedIn()) {
            deviceTokenModel.setCustomerId(CustomerDetails.getCurrentLoginUser().getCustomerId());
        } else {
            deviceTokenModel.setCustomerId(Constants.DEFAULT_CUSTOMER_ID);
        }
        deviceTokenModel.callAddDeviceTokenAPI();
    }
}
