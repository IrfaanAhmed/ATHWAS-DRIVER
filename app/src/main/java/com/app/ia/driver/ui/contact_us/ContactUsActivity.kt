package com.app.ia.driver.ui.contact_us

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseActivity
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.ActivityContactUsBinding
import com.app.ia.driver.utils.setOnApplyWindowInset
import com.app.ia.driver.utils.visible
import kotlinx.android.synthetic.main.activity_order_status.*
import kotlinx.android.synthetic.main.common_header.view.*

class ContactUsActivity : BaseActivity<ActivityContactUsBinding, ContactUsViewModel>() {

    private var mBinding: ActivityContactUsBinding? = null
    private var mViewModel: ContactUsViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_contact_us
    }

    override fun getViewModel(): ContactUsViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)

        //makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)

        toolbar.imageViewBack.visible()
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ContactUsViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(ContactUsViewModel::class.java)
    }

}