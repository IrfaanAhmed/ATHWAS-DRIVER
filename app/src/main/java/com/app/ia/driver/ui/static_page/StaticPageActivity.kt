package com.app.ia.driver.ui.static_page

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseActivity
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.ActivityStaticPageBinding
import com.app.ia.driver.utils.setOnApplyWindowInset
import com.app.ia.driver.utils.visible
import kotlinx.android.synthetic.main.activity_order_status.*
import kotlinx.android.synthetic.main.common_header.view.*

class StaticPageActivity : BaseActivity<ActivityStaticPageBinding, StaticPageViewModel>() {

    private var mBinding: ActivityStaticPageBinding? = null
    private var mViewModel: StaticPageViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_static_page
    }

    override fun getViewModel(): StaticPageViewModel {
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

        //makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)

        toolbar.imageViewBack.visible()
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(StaticPageViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(StaticPageViewModel::class.java)
    }

}