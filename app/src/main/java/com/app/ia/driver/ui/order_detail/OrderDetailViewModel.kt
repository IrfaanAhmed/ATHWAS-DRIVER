package com.app.ia.driver.ui.order_detail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.lifecycle.observe
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.ActivityOrderDetailBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.enums.Status
import com.app.ia.driver.model.OrderDetailResponse
import com.app.ia.driver.utils.CommonUtils
import com.app.ia.driver.utils.Resource
import kotlinx.coroutines.Dispatchers

class OrderDetailViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityOrderDetailBinding
    var orderDetailResponse = MutableLiveData<OrderDetailResponse>()
    var order_id = MutableLiveData<String>()

    val isItemAvailable = MutableLiveData(true)

    val totalAmount = MutableLiveData(0.0)
    var offerAmount = MutableLiveData(0.0)

    var isLoading = false

    fun setVariable(mBinding: ActivityOrderDetailBinding, intent: Intent) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.order_detail))
        order_id.value = intent.getStringExtra("order_id")!!
        //orderDetailObserver(order_id.value!!)
    }

    fun onPhoneCall(phoneNumber: String) {
        val callDialog = DriverDialog(getActivityNavigator()!!, "", phoneNumber, "Call", "Cancel", false)
        callDialog.setOnItemClickListener(object : DriverDialog.OnClickListener {

            override fun onPositiveClick() {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phoneNumber")
                getActivityNavigator()?.startActivity(intent)
            }

            override fun onNegativeClick() {

            }
        })
    }

    fun onManageOrderStatusClick() {
        //mActivity.startActivity<OrderStatusActivity> ()
        // Order Status (Acknowledged/Packed/In_Transit/Delivered)
        if(isLoading)
            return
        var status = ""
        when (orderDetailResponse.value!!.currentTrackingStatus) {
            "Acknowledged" -> {
                status = "Packed"
            }
            "Packed" -> {
                status = "In_Transit"
            }
            "In_Transit" -> {
                status = "Delivered"
            }
        }

        if (orderDetailResponse.value!!.orderStatus == 3 || orderDetailResponse.value!!.orderStatus == 4) {
            //mActivity.toast("This order has been return by customer. you cannot change the status.")
        } else if (status == "In_Transit" || status == "Delivered") {
            val requestParams = HashMap<String, String>()
            requestParams["status"] = status
            updateOrderStatusObserver(order_id.value!!, requestParams)
        }
    }


    private fun trackingStatus() {

        when (orderDetailResponse.value!!.orderStatus) {
            3 -> {
                mBinding.buttonInTransit.text = "Returned"
                mBinding.layoutTracking.visibility = View.GONE
            }

            4 -> {
                mBinding.buttonInTransit.text = "Cancelled"
                mBinding.layoutTracking.visibility = View.GONE
            }

            else -> {
                mBinding.layoutTracking.visibility = View.VISIBLE

                val status: String
                when (orderDetailResponse.value!!.currentTrackingStatus) {
                    "Acknowledged" -> {
                        status = "Packed"
                    }
                    "Packed" -> {
                        status = "In Transit"
                        mBinding.buttonInTransit.text = status
                    }
                    "In_Transit" -> {
                        status = "Delivered"
                        mBinding.buttonInTransit.text = status
                    }
                    else -> {
                        mBinding.buttonInTransit.text = "Delivered"
                    }
                }
            }
        }
    }

    private fun orderDetail(requestParams: String) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.orderDetail(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun orderDetailObserver(requestParams: String) {

        orderDetail(requestParams).observe(mBinding.lifecycleOwner!!, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        isLoading = false
                        resource.data?.let { users ->
                            orderDetailResponse.value = users.data!![0]

                            totalAmount.value = 0.0
                            offerAmount.value = 0.0

                            for (cartItem in orderDetailResponse.value!!.category) {
                                val products = cartItem.products
                                for (item in products) {
                                    totalAmount.value = CommonUtils.convertToDecimal(totalAmount.value!! + (item.getPrice().toDouble() * item.quantity)).toDouble()
                                }
                            }

                            offerAmount.value = CommonUtils.convertToDecimal(/*(totalAmount.value!! - orderDetailResponse.value!!.getTotalAmount().toDouble()) +*/ orderDetailResponse.value!!.getDiscount().toDouble()).toDouble()

                            trackingStatus()
                        }
                    }

                    Status.ERROR -> {
                        isLoading = false
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private fun updateOrderStatus(order_id: String, requestParams: HashMap<String, String>) =
        liveData(Dispatchers.Main) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = baseRepository.updateOrderStatus(
                            order_id,
                            requestParams
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    private fun updateOrderStatusObserver(order_id: String, requestParams: HashMap<String, String>) {

        isLoading = true

        updateOrderStatus(order_id, requestParams).observe(mBinding.lifecycleOwner!!, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            orderDetailObserver(order_id)
                        }
                    }

                    Status.ERROR -> {
                        isLoading = false
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        } else {
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}