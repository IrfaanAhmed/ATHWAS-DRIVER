package com.app.ia.driver.ui.order_stats

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.ActivityOrderStatsBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.enums.Status
import com.app.ia.driver.model.OrderListResponse
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.ui.my_order.MyOrderFragment
import com.app.ia.driver.utils.CommonUtils
import com.app.ia.driver.utils.Resource
import com.app.ia.driver.utils.plusAssign
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class OrderStatsViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityOrderStatsBinding

    val isItemAvailable = MutableLiveData(true)
    val currentPage = MutableLiveData(1)
    val totalOrderCount = MutableLiveData(0)
    val deliveredCount = MutableLiveData(0)
    val notDeliveredCount = MutableLiveData(0)
    val isLastPage = MutableLiveData(false)

    fun setVariable(mBinding: ActivityOrderStatsBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.order_stats))

        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = currentPage?.value.toString()
        requestParams["keyword"] = ""
        setupObservers(requestParams)
    }

    private fun getOrderStats(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getOrderStats()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setupObservers(requestParams: HashMap<String, String>) {

        getOrderStats(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            /*   val response = users.data as OrderListResponse
                               totalOrderCount.value = response.docs.size*/
                            totalOrderCount.value = users.data?.totalOrder
                            deliveredCount.value = users.data?.totalDeliveredOrder
                            notDeliveredCount.value = users.data?.totalNotDeliveredOrder
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (CommonUtils.isJSONValid(it.message)) {
                            val obj = JSONObject(it.message!!)
                            if (obj.getInt("code") == 400) {
                                DriverDialog(mActivity, obj.getString("message"), true)
                            } else {
                                Toast.makeText(mActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {

                    }
                }
            }
        })
    }
}