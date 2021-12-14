package com.app.ia.driver.ui.order_status

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseActivity
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.ActivityOrderStatusBinding
import com.app.ia.driver.utils.setOnApplyWindowInset
import com.app.ia.driver.utils.visible
import kotlinx.android.synthetic.main.activity_order_status.*
import kotlinx.android.synthetic.main.common_header.view.*

class OrderStatusActivity : BaseActivity<ActivityOrderStatusBinding, OrderStatusViewModel>() {

    private var mBinding: ActivityOrderStatusBinding? = null
    private var mViewModel: OrderStatusViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order_status
    }

    override fun getViewModel(): OrderStatusViewModel {
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
        val factory = ViewModelFactory(OrderStatusViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(OrderStatusViewModel::class.java)
    }

}