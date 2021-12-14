package com.app.ia.driver

import android.app.Activity
import android.app.Application
import android.content.Context
import com.app.ia.driver.utils.AppLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DriverApplication : Application() {

    private var mCurrentActivity: Activity? = null

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        driverApplication = this

        Firebase.database.setPersistenceEnabled(false)
        AppLogger.init()
        FirebaseApp.initializeApp(this)
        //initPlace()
    }

    companion object {

        private var driverApplication: DriverApplication? = null
        fun getInstance(): DriverApplication {
            return driverApplication!!
        }
    }

    fun getCurrentActivity(): Activity? {
        return mCurrentActivity
    }

    fun setCurrentActivity(mCurrentActivity: Activity?) {
        this.mCurrentActivity = mCurrentActivity
    }

    /*private fun initPlace() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.GoogleMapKey, Locale.US)
        }
    }*/
}