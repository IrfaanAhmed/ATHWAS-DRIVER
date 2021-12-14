package com.app.ia.driver.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.driver.R
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.model.MyLocation
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.utils.AppLogger
import com.app.ia.driver.utils.AppLogger.e
import com.app.ia.driver.utils.AppLogger.i
import com.app.ia.driver.utils.AppLogger.w
import com.app.ia.driver.utils.Utils
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 *
 *
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 *
 *
 * This sample show how to use a long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification associated with that service is removed.
 */
class LocationUpdatesService : Service() {

    private val mBinder: IBinder = LocalBinder()

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private var mChangingConfiguration = false
    private var mNotificationManager: NotificationManager? = null

    /**
     * Contains parameters used by [com.google.android.gms.location.FusedLocationProviderApi].
     */
    private var mLocationRequest: LocationRequest? = null

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Callback for changes in location.
     */
    private var mLocationCallback: LocationCallback? = null
    private var mServiceHandler: Handler? = null

    /**
     * The current location.
     */
    private var mLocation: Location? = null
    override fun onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d("Location Result: ", "Last Location: ${locationResult.lastLocation.latitude}, ${locationResult.lastLocation.longitude}")
                onNewLocation(locationResult.lastLocation)
                mCallWebService(locationResult.lastLocation)
            }
        }

        val userID: String = AppPreferencesHelper.getInstance().userID
        myRef = FirebaseDatabase.getInstance().getReference("athwas_drivers").child(userID)

        createLocationRequest()
        lastLocation()
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager!!.createNotificationChannel(mChannel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        i("$TAG Service started")
        val startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false)

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates()
            stopSelf()
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    override fun onBind(intent: Intent): IBinder {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        i("$TAG in onBind()")
        stopForeground(true)
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        i("$TAG in onRebind()")
        stopForeground(true)
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        i("$TAG Last client unbound from service")

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
            i(TAG + "Starting foreground service")
            /*
            // (developer). If targeting O, use the following code.
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                mNotificationManager.startServiceInForeground(new Intent(this,
                        LocationUpdatesService.class), NOTIFICATION_ID, getNotification());
            } else {
                startForeground(NOTIFICATION_ID, getNotification());
            }
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_LOCATION)
            }
            else{
                startForeground(NOTIFICATION_ID, notification)
            }
        }
        return true // Ensures onRebind() is called when a client re-binds.
    }

    override fun onDestroy() {
        mServiceHandler!!.removeCallbacksAndMessages(null)
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    fun requestLocationUpdates() {
        i("$TAG Requesting location updates")
        Utils.setRequestingLocationUpdates(this, true)
        startService(Intent(applicationContext, LocationUpdatesService::class.java))
        try {
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            Utils.setRequestingLocationUpdates(this, false)
            e("$TAG Lost location permission. Could not request updates. $unlikely")
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    fun removeLocationUpdates() {
        i("$TAG Removing location updates")
        try {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            Utils.setRequestingLocationUpdates(this, false)
            stopSelf()
        } catch (unlikely: SecurityException) {
            Utils.setRequestingLocationUpdates(this, true)
            e(TAG + "Lost location permission. Could not remove updates. " + unlikely)
        }
    }// Channel ID// Extra to help us figure out if we arrived in onStartCommand via the notification or not.

    // The PendingIntent that leads to a call to onStartCommand() in this service.

    // The PendingIntent to launch activity.

    // Set the Channel ID for Android O.
    /**
     * Returns the [NotificationCompat] used as part of the foreground service.
     */
    private val notification: Notification
        private get() {
            val intent = Intent(this, LocationUpdatesService::class.java)
            val text: CharSequence = Utils.getLocationText(mLocation)

            // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
            intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)

            // The PendingIntent that leads to a call to onStartCommand() in this service.
            val servicePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            // The PendingIntent to launch activity.
            val activityPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, HomeActivity::class.java), 0)
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "")
                .addAction(R.drawable.tick, getString(R.string.launch_activity), activityPendingIntent)
                .addAction(R.drawable.ic_close, getString(R.string.remove_location_updates), servicePendingIntent)
                .setContentText("")
                .setContentTitle("Online Driver")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(getNotificationIcon())
                .setTicker(text)
                .setSilent(true)
                .setWhen(System.currentTimeMillis())

            // Set the Channel ID for Android O.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID) // Channel ID
            }
            return builder.build()
        }

    private fun lastLocation() {
        try {
            mFusedLocationClient!!.lastLocation.addOnCompleteListener { task: Task<Location?> ->
                if (task.isSuccessful && task.result != null) {
                    mLocation = task.result
                } else {
                    w("$TAG Failed to get location.")
                }
            }
        } catch (unlikely: SecurityException) {
            e("$TAG  Lost location permission.$unlikely")
        }
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.drawable.ic_notification_icon else R.mipmap.ic_launcher_round
    }

    private fun onNewLocation(location: Location) {
        i("$TAG New location: $location")
        mLocation = location

        // Notify anyone listening for broadcasts about the new location.
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager!!.notify(NOTIFICATION_ID, notification)
        }
    }

    /**
     * Sets the location request parameters.
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime= FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            smallestDisplacement = SMALLEST_DISPLACEMENT
        }
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        val service: LocationUpdatesService
            get() = this@LocationUpdatesService
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The [Context].
     */
    fun serviceIsRunningInForeground(context: Context): Boolean {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (javaClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }

    private var myRef: DatabaseReference? = null
    private fun mCallWebService(location: Location) {

        try {
            AppLogger.d("latitude=" + location.latitude + " longitude=" + location.longitude)

            val userID: String = AppPreferencesHelper.getInstance().userID
            if (myRef == null) {
                myRef = FirebaseDatabase.getInstance().getReference("athwas_drivers").child(userID)
            }

            AppLogger.d("UserId: $userID")

            val requestParams = HashMap<String, String>()
            requestParams["latitude"] = location.latitude.toString()
            requestParams["longitude"] = location.longitude.toString()

            val jsonObject = MyLocation()
            jsonObject.lat = location.latitude
            jsonObject.lng = location.longitude
            myRef!!.setValue(jsonObject).addOnSuccessListener {
                AppLogger.d("Firebase update successful $userID ${jsonObject.lat}, ${jsonObject.lng}")
            }
                .addOnFailureListener {
                    AppLogger.d("Firebase update failed $userID ${jsonObject.lat}, ${jsonObject.lng}")
                    it.printStackTrace()
                }

            val service = RetrofitFactory.getInstance()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = service.updateLocation(requestParams)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                AppLogger.d("Success")
                            } else {
                                AppLogger.d("Error: ${response.code()}")
                            }
                        } catch (e: HttpException) {
                            AppLogger.d("Exception ${e.message}")
                        } catch (e: Throwable) {
                            AppLogger.d("Oops: Something else went wrong")
                        }
                        catch (e: Exception){
                            AppLogger.d("Exception ")
                        }
                    }
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val PACKAGE_NAME = "com.app.ia.driver.LocationUpdateService"
        private val TAG = LocationUpdatesService::class.java.simpleName

        /**
         * The name of the channel for notifications.
         */
        private const val CHANNEL_ID = "channel_01"
        const val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
        const val EXTRA_LOCATION = "$PACKAGE_NAME.location"
        const val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 30000

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

        /**
         * The identifier for the notification displayed for the foreground service.
         */
        private const val NOTIFICATION_ID = 12345678
        private const val SMALLEST_DISPLACEMENT = 1f
    }
}