package com.app.ia.driver.ui.otp

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseActivity
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.ActivityOtpBinding
import com.app.ia.driver.utils.makeStatusBarTransparent
import com.app.ia.driver.utils.setOnApplyWindowInset
import com.app.ia.driver.utils.visible
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.common_header.view.*

class OTPActivity : BaseActivity<ActivityOtpBinding, OTPViewModel>() {

    private var mBinding: ActivityOtpBinding? = null
    private var mViewModel: OTPViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_otp
    }

    override fun getViewModel(): OTPViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)
        mViewModel?.setIntent(intent)

        makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)
        toolbar.imageViewBack.visible()
        toolbar.imageViewBack.setColorFilter(ContextCompat.getColor(this@OTPActivity, R.color.black))
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(OTPViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(OTPViewModel::class.java)
    }
}