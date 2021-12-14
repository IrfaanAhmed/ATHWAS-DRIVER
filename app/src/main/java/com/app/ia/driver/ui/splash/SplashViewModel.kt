package com.app.ia.driver.ui.splash

import android.app.Activity
import android.text.TextUtils
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.ActivitySplashBinding
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.ui.login.LoginActivity
import com.app.ia.driver.utils.Coroutines
import com.app.ia.driver.utils.startActivity
import kotlinx.coroutines.delay

@Suppress("UNUSED_PARAMETER")
class SplashViewModel(baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivitySplashBinding

    fun setVariable(mBinding: ActivitySplashBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    fun callNextActivity() {
        Coroutines.io {
            delay(3000)
            if (TextUtils.isEmpty(AppPreferencesHelper.getInstance().authToken)) {
                mActivity.startActivity<LoginActivity>()
            } else {
                mActivity.startActivity<HomeActivity>()
                //mActivity.startService(Intent(mActivity, LocationUpdaterService::class.java))

            }
            mActivity.finish()
        }
    }
}