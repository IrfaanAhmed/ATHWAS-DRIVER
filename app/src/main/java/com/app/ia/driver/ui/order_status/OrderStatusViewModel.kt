package com.app.ia.driver.ui.order_status

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.ActivityOrderStatusBinding

class OrderStatusViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityOrderStatusBinding

    val isItemAvailable = MutableLiveData(true)

    fun setVariable(mBinding: ActivityOrderStatusBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.manage_order_status))
    }

}