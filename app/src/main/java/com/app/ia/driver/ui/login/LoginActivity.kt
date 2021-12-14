package com.app.ia.driver.ui.login

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseActivity
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.ActivityLoginBinding
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.utils.invisible
import com.app.ia.driver.utils.makeStatusBarTransparent
import com.app.ia.driver.utils.setOnApplyWindowInset
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.common_header.view.*

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    private var mActivityBinding: ActivityLoginBinding? = null
    private var mViewModel: LoginViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun getViewModel(): LoginViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityBinding = getViewDataBinding()
        mActivityBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mActivityBinding!!)

        makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)
        toolbar.imageViewBack.invisible()

        currentLocationManager(false)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(LoginViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
    }

    override fun onCurrentLocation(latitude: Double, longitude: Double) {

        if (latitude == 0.0 && longitude == 0.0) {
            currentLocationManager()
        } else {
            AppPreferencesHelper.getInstance().mCurrentLat = latitude.toString()
            AppPreferencesHelper.getInstance().mCurrentLng = longitude.toString()
        }
    }
}