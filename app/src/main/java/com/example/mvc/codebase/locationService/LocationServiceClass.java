package com.example.mvc.codebase.locationService;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.permissionUtils.PermissionClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.Collections;

/*
 * Todo: Before implement Location service you must follow below steps.
 * This class use to get current location.
 *
 * 1. Import latest Location service gradle file in app level build.gradle
 *    @link : https://developers.google.com/android/guides/setup#add_google_play_services_to_your_project
 *
 *    dependencies {
 *       compile 'com.google.android.gms:play-services-location:10.2.1'
 *     }
 *     till 23-3-2017 latest version is 10.2.1.
 *
 * 2. Import "locationService" package or required class (LocationServiceClass.java)
 *
 * 3. Import "permissionUtils" package or required class
 *
 * 4. Validate required service that need to perform action
 *
 *    4.1) check Google Play service
 *         copy below code where you want to check google play service available or not
 *
 *         Util.checkGooglePlayServices(this);
 *
 *    4.2) check run time Location permission
 *         copy below code where you want to check location permission
 *
 *         PermissionClass.checkPermission(this,PermissionClass.REQUEST_CODE_RUNTIME_PERMISSION,
 *               Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION));
 *
 * 5. Initialize GoogleApi client
 *    5.1) Initialize GoogleApi client
 *         copy below code to initialize google api client
 *
 *         LocationServiceClass.getInstance().initGoogleApiClient(this);
 *
 *    5.2) Connect mGoogleApiClient in onStart Method
 *         e.g. @Override
 *               protected void onStart() {
 *                  LocationServiceClass.connectGoogleApiClient();
 *                super.onStart();
 *                   }
 *
 *    5.3) Disconnect mGoogleApiClient in onStop Method
 *         e.g. @Override
 *               protected void onStop() {
 *                  LocationServiceClass.disConnectGoogleApiClient();
 *                 super.onStop();
 *                   }
 *
 * 6. Create Location request to get Latitude and Longitude
 *    after checking google play service and Location service now user or developer gets the
 *    location information
 *
 *    copy below code to make a request for latitude and longitude
 *
 *    LocationServiceClass.getInstance().createLocationRequest(this);
 *
 * 7. Stop Location Update service
 *    after getting required information we can stop Location service to reduce user's data
 *    and also it saves battery
 *
 *    copy below code where you get location information
 *      after getting required information we should stop Location service
 *
 *    stopLocationUpdates()
 */

public class LocationServiceClass implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    // variable declaration
    public static final String TAG = LocationServiceClass.class.getName();
    private static final int REQUEST_CODE_CHECK_SETTINGS = 102;
    private static final int REQ_PERMISSION_LOCATION = 103;
    private final static int VALUE_SET_INTERVAL = 2000;
    private final static int VALUE_SET_FASTEST_INTERVAL = 5000;

    // class object declaration
    private static GoogleApiClient mGoogleApiClient;
    private static LocationRequest mLocationRequest;
    private static LocationServiceClass locationServiceClassObj;

    /**
     * To get a class instance
     *
     * @return locationServiceClassObj (LocationServiceClass) : it return LocationServiceClass
     * class instance
     */
    public static LocationServiceClass getInstance() {
        if (locationServiceClassObj == null) {
            locationServiceClassObj = new LocationServiceClass();
        }
        return locationServiceClassObj;
    }

    /**
     * This method implement required listener, make connection and call Location service api
     *
     * @param context (Context) : application context
     * @see LocationServices#API
     */
    public synchronized void initGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    /**
     * This method connect with google api client when application state is resume
     * Connects the client to Google Play services. This method returns immediately, and
     * connects to the service in the background.
     * <p>
     * refer below link for more information
     *
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient#connect()">mGoogleApiClient.connect()</a>
     */
    public static void connectGoogleApiClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * This method disconnect google api client when application is in Background state
     * Closes the connection to Google Play services. No calls can be made using this client
     * after calling this method. Any method calls that haven't executed yet will be canceled,
     * <p>
     * refer below link for more information
     *
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient#connect()">mGoogleApiClient.disconnect()</a>
     */
    public static void disConnectGoogleApiClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * This Method request to get Location
     *
     * @param context (Context) : application context
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest">LocationRequest</a>
     * @see <a href="https://developer.android.com/training/location/change-location-settings.html#location-request">Set Up a Location Request</a>
     */
    public void createLocationRequest(Context context) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(VALUE_SET_INTERVAL);
        mLocationRequest.setFastestInterval(VALUE_SET_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        settingsRequest(context);
    }

    /**
     * This method check Location service state and perform it's relevant action.
     * <p>
     * case 1: {@link LocationSettingsStatusCodes#SUCCESS}
     * <br>All location settings are satisfied. The client can initialize location requests here</br>
     * </p>
     * <p>
     * case 2: {@link LocationSettingsStatusCodes#RESOLUTION_REQUIRED}
     * <br>Location settings are not satisfied. But could be fixed by showing dialog to user.</br>
     * </p>
     * <p>
     * case 3: {@link LocationSettingsStatusCodes#SETTINGS_CHANGE_UNAVAILABLE}
     * <br>Location settings are not satisfied. However, we have no way to fix the settings
     * so we won't show the dialog.</br>
     * </p>
     *
     * @param context (Context) : application context
     * @see <a href="https://developer.android.com/training/location/change-location-settings.html#get-settings">Get Current Location Settings</a>
     */
    private void settingsRequest(final Context context) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient. for more details refer above link : Get Current Location Settings

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        // e.g. startLocationUpdates();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult((Activity) context, REQUEST_CODE_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        //TODO auto generated method or stub
        // To get current latitude and longitude value
        location.getLatitude();
        location.getLongitude();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //TODO auto generated method or stub
    }

    @Override
    public void onConnectionSuspended(int i) {
        //TODO auto generated method or stub
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO auto generated method or stub
    }

    /**
     * This method request location update and will receive location in
     * {@link #onLocationChanged(Location)} method.
     * it call after  Location service enabled
     * <a href="https://developer.android.com/training/location/receive-location-updates.html#updates">Request Location Updates</a>
     *
     * @see LocationServices#FusedLocationApi
     * @see android.Manifest.permission#ACCESS_FINE_LOCATION
     */
    public void startLocationUpdates() {
        if (PermissionClass.checkPermission(MyApplication.getInstance(), PermissionClass.REQUEST_CODE_RUNTIME_PERMISSION,
                Collections.singletonList(Manifest.permission.ACCESS_FINE_LOCATION))) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * This method stop Location update listener
     *
     * @see com.google.android.gms.location.FusedLocationProviderApi#removeLocationUpdates(GoogleApiClient, LocationListener)
     */
    public void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

}
