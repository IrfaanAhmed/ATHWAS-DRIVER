package com.app.ia.driver.ui.splash

import android.location.Location
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseActivity
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.ActivitySplashBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.utils.AppLogger
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

    private var mBinding: ActivitySplashBinding? = null
    private var mViewModel: SplashViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun getViewModel(): SplashViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)

        storeDeviceToken()
        //currentLocationManager(false)
        mViewModel?.callNextActivity()
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(SplashViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(SplashViewModel::class.java)
    }

    private fun storeDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                AppLogger.w("Fetching FCM registration token failed" + task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val fcmToken = task.result
            AppLogger.d("device token : $fcmToken")
            AppPreferencesHelper.getInstance().deviceToken = fcmToken
        })
    }

    override fun onCurrentLocation(latitude: Double, longitude: Double) {

        if (latitude == 0.0 && longitude == 0.0) {
            DriverDialog(this, getString(R.string.unable_fetch_location), true)
        } else {
            AppPreferencesHelper.getInstance().mCurrentLat = latitude.toString()
            AppPreferencesHelper.getInstance().mCurrentLng = longitude.toString()

            mViewModel?.callNextActivity()
        }
    }

    override fun onLocationUpdate(locationResult: Location?) {
        super.onLocationUpdate(locationResult)
        if (locationResult != null) {
            if (locationResult.latitude != 0.0 && locationResult.longitude != 0.0) {
                mLocationManager?.removeLocationUpdate()
                AppPreferencesHelper.getInstance().mCurrentLat = locationResult.latitude.toString()
                AppPreferencesHelper.getInstance().mCurrentLng = locationResult.longitude.toString()
                mViewModel?.callNextActivity()
            } else {
                DriverDialog(this, getString(R.string.unable_fetch_location), true)
            }
        } else {
            DriverDialog(this, getString(R.string.unable_fetch_location), true)
        }
    }

    override fun onLocationFetchFailed() {
        super.onLocationFetchFailed()
        mLocationManager?.startLocationUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationManager != null) {
            mLocationManager?.removeLocationUpdate()
        }
    }
}