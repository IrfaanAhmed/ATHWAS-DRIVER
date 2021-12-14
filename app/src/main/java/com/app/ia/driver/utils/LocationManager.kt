package com.app.ia.driver.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.core.app.ActivityCompat
import com.app.ia.driver.R
import com.app.ia.driver.dialog.DriverDialog
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.GetLocationDetail
import com.example.easywaylocation.Listener
import com.example.easywaylocation.LocationData
import com.google.android.gms.location.LocationRequest
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

class LocationManager(private var currentLocationListener: CurrentLocationListener, var mContext: Context, requireLastLocation: Boolean = true) : Listener, LocationData.AddressCallBack {

    var mLocationPermissionGranted: Boolean = false
    private var easyWayLocation: EasyWayLocation? = null
    private var locationUpdateFlag = false
    private val locationRequest: LocationRequest = LocationRequest.create()

    private companion object {
        const val UPDATE_INTERVAL = (5 * 1000).toLong()  /* 5 secs */

        //private const val FASTEST_INTERVAL: Long = 0L// 2000 /* 2 sec */
        private const val SMALLEST_DISPLACEMENT: Float = 0.0f
    }

    init {
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.smallestDisplacement = SMALLEST_DISPLACEMENT
        easyWayLocation = EasyWayLocation(mContext, locationRequest, requireLastLocation, this)
    }

    fun checkPermission() {
        locationUpdateFlag = false
        getLocationPermission()
    }

    private fun getLocationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val fine = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val coarse = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val background = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (fine && coarse && background) {
                mLocationPermissionGranted = true
                getCurrentLocation()
            } else {

                Dexter.withContext(mContext).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION).withListener(object : MultiplePermissionsListener {

                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                        val deniedPermissions = report?.deniedPermissionResponses
                        when {
                            report?.areAllPermissionsGranted()!! -> {
                                mLocationPermissionGranted = true
                                getCurrentLocation()
                            }
                            deniedPermissions?.size!! < 3 -> {
                                mLocationPermissionGranted = true
                                getCurrentLocation()

                            }
                            deniedPermissions.size == 3 -> {
                                goToLocationSetting()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }

                }).onSameThread().check()
            }

        } else {

            Dexter.withContext(mContext).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    val deniedPermissions = report?.deniedPermissionResponses
                    when {
                        report?.areAllPermissionsGranted()!! -> {
                            mLocationPermissionGranted = true
                            getCurrentLocation()
                        }
                        deniedPermissions?.size!! < 2 -> {
                            mLocationPermissionGranted = true
                            getCurrentLocation()

                        }
                        deniedPermissions.size == 2 -> {
                            goToLocationSetting()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }
            }).onSameThread().check()
        }
    }

    private fun goToLocationSetting() {
        val cleverDialog = DriverDialog(mContext as Activity, "", mContext.getString(R.string.instructions_to_turn_on_location_permission), mContext.getString(R.string.settings), mContext.getString(R.string.not_now), false)
        cleverDialog.setOnItemClickListener(object : DriverDialog.OnClickListener {

            override fun onPositiveClick() {

                val settingsIntent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", mContext.packageName, null)
                settingsIntent.data = uri
                (mContext as Activity).startActivityForResult(settingsIntent,
                    AppRequestCode.LOCATION_SETTING_REQUEST
                )
            }

            override fun onNegativeClick() {

            }
        })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {

        when (requestCode) {
            EasyWayLocation.LOCATION_SETTING_REQUEST_CODE -> {
                easyWayLocation?.onActivityResult(resultCode)
            }
            AppRequestCode.LOCATION_SETTING_REQUEST -> {
                getLocationPermission()
            }
            else -> {

            }
        }
    }

    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    fun getCurrentLocation() {

        try {
            if (mLocationPermissionGranted) {
                locationUpdateFlag = false
                easyWayLocation?.startLocation()
            }
        } catch (e: SecurityException) {
            AppLogger.e("Exception: %s", e.message!!)
        }
    }

    fun startLocationUpdate() {
        locationUpdateFlag = true
        easyWayLocation?.startLocation()
    }

    fun removeLocationUpdate() {
        easyWayLocation?.endUpdates()
    }

    fun getLocationAddress(latitude: Double, longitude: Double) {
        val getLocationDetail = GetLocationDetail(this, mContext)
        getLocationDetail.getAddress(latitude, longitude, "AIzaSyC4MhJYysKpRFHe9Re--y5S0_PCtxGir9Q")
    }

    interface CurrentLocationListener {
        fun onCurrentLocation(latitude: Double, longitude: Double)
        fun onLocationUpdate(locationResult: Location?)
        fun onLocationFetchFailed()
        fun onAddressUpdate(locationData: LocationData?)
    }

    override fun locationCancelled() {
        currentLocationListener.onLocationFetchFailed()
    }

    override fun locationOn() {
    }

    override fun currentLocation(location: Location?) {
        if (locationUpdateFlag) {
            currentLocationListener.onLocationUpdate(location)
        } else {
            currentLocationListener.onCurrentLocation(location?.latitude!!, location.longitude)
            removeLocationUpdate()
        }
    }

    override fun locationData(locationData: LocationData?) {
        currentLocationListener.onAddressUpdate(locationData)
    }

}