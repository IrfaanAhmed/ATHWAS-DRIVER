package com.app.ia.driver.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


/**
 * Created by Hyperlink infosystem team  on 27/9/16.
 */


public class LocationManagerClient extends LocationCallback {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final int REQUEST_CHECK_SETTINGS = 111;
    public static final int REQUEST_CHECK_PERMISSION = 222;

    private FusedLocationProviderClient fusedLocationClient;
    private AppCompatActivity activity;
    private Context context;
    private LocationRequest locationRequest;
    private static LocationManagerClient locationManagerClient;
    private LocationListener locationListener;
    private boolean isFreshLocation = false;
    private boolean isRequireToUpdateLocation = false;

    public static LocationManagerClient getInstance() {
        if (locationManagerClient == null) {
            locationManagerClient = new LocationManagerClient();
        }
        return locationManagerClient;
    }

    public LocationManagerClient init(Context context, AppCompatActivity activity) {
        if (context == null)
            throw new NullPointerException("Context must not be null");
        if (activity == null)
            throw new NullPointerException("Activity must not be null");
        this.activity = activity;
        this.context = context;
        fusedLocationClient = new FusedLocationProviderClient(context);
        return this;
    }

    public LocationManagerClient setIsFreshLocation(boolean isFreshLocation) {
        this.isFreshLocation = isFreshLocation;
        return this;
    }

    public LocationManagerClient setIsRequireToUpdateLocation(boolean isRequireToUpdateLocation) {
        this.isRequireToUpdateLocation = isRequireToUpdateLocation;
        return this;
    }

    public LocationManagerClient build() {
        if (fusedLocationClient == null)
            throw new NullPointerException("must call init method before build");
        return this;
    }

    public void fetchCurrentLocation(LocationListener locationListener) {
        if (locationListener == null)
            throw new NullPointerException("Location callback must not be null");
        this.locationListener = locationListener;
        checkLocationPermission();
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        if (locationListener != null) {
            if (locationResult.getLastLocation() != null) {
                locationListener.onLocationAvailable(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));
            }
        }
    }


    public void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CHECK_PERMISSION);
            } else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_PERMISSION);
            } else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CHECK_PERMISSION);
            } else {
                checkLocationAvailability();
            }
        } else checkLocationAvailability();
    }


    private void checkLocationAvailability() {
        if (checkPlayServices()) {
            checkLocationEnable();
        } else locationListener.onFail(LocationListener.Status.NO_PLAY_SERVICE);
    }


    private void checkLocationEnable() {
        createLocationRequest();
        LocationSettingsRequest.Builder locationSettingBuilder = new LocationSettingsRequest.Builder();
        locationSettingBuilder.setAlwaysShow(true);
        locationSettingBuilder.addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(locationSettingBuilder.build());
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (locationSettingsResponse.getLocationSettingsStates().isLocationUsable()) {
                    if (locationSettingsResponse.getLocationSettingsStates().isGpsUsable())
                        getLocation();
                   /* else if (locationSettingsResponse.getLocationSettingsStates().isNetworkLocationUsable())
                        getLocation();*/
                    else locationListener.onFail(LocationListener.Status.GPS_DISABLED);
                } else locationListener.onFail(LocationListener.Status.GPS_DISABLED);
            }
        });
        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                getLocation();
            } else {
                if (locationListener != null)
                    locationListener.onFail(LocationListener.Status.DENIED_LOCATION_SETTING);
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CHECK_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocationAvailability();
        } else {
            if (locationListener != null)
                locationListener.onFail(LocationListener.Status.PERMISSION_DENIED);
        }
    }

    private void getLocation() {
        if (isFreshLocation) { /*Must check whether we can fetch updated location or not*/
            startLocationUpdates();  //Call when we update on movement
        } else fetchLastLocation();
    }

    private void startLocationUpdates() {

        fusedLocationClient.requestLocationUpdates(locationRequest,
                this,
                Looper.getMainLooper());


    }

    private void fetchLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            locationListener.onLocationAvailable(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                    }
                });
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        if (isRequireToUpdateLocation) {
            locationRequest.setInterval(30000);  /*Update after 30 seconds*/
            locationRequest.setFastestInterval(15000);  /*Update after 15 seconds*/
        }
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private boolean checkPlayServices() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(activity);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(activity, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    public interface LocationListener {
        void onLocationAvailable(LatLng latLng);

        void onFail(Status status);

        enum Status {
            PERMISSION_DENIED, NO_PLAY_SERVICE, DENIED_LOCATION_SETTING, GPS_DISABLED
        }
    }


}
