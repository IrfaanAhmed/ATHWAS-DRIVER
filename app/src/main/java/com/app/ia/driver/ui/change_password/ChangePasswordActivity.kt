package com.app.ia.driver.ui.change_password

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseActivity
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.ActivityChangePasswordBinding
import com.app.ia.driver.utils.setOnApplyWindowInset
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : BaseActivity<ActivityChangePasswordBinding, ChangePasswordViewModel>() {

    private var mActivityChangePasswordBinding: ActivityChangePasswordBinding? = null
    private var mChangePasswordViewModel: ChangePasswordViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_change_password
    }

    override fun getViewModel(): ChangePasswordViewModel {
        return mChangePasswordViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityChangePasswordBinding = getViewDataBinding()
        mActivityChangePasswordBinding?.lifecycleOwner = this
        mChangePasswordViewModel?.setActivityNavigator(this)
        mChangePasswordViewModel?.setVariable(mActivityChangePasswordBinding!!)

        //makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ChangePasswordViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mChangePasswordViewModel = ViewModelProvider(this, factory).get(ChangePasswordViewModel::class.java)
    }
}