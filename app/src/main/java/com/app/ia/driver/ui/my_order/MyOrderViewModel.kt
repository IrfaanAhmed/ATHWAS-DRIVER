package com.app.ia.driver.ui.my_order

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.FragmentMyOrderBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.dialog.bottom_sheet.OrderFilterDialogFragment
import com.app.ia.driver.dialog.bottom_sheet.OrderSortByDialogFragment
import com.app.ia.driver.enums.Status
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.model.OrderListResponse
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.utils.AppConstants
import com.app.ia.driver.utils.CommonUtils
import com.app.ia.driver.utils.Resource
import com.app.ia.driver.utils.plusAssign
import kotlinx.android.synthetic.main.toolbar_home.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class MyOrderViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentMyOrderBinding

    val isItemAvailable = MutableLiveData(true)
    val currentPage = MutableLiveData(1)
    val totalOrderCount = MutableLiveData(0)
    val isLastPage = MutableLiveData(false)
    var orderListData = MutableLiveData<MutableList<OrderListResponse.Docs>>()
    val orderListAll = ArrayList<OrderListResponse.Docs>()

    var selectedStartDate = ""
    var selectedEndDate = ""
    var selectedMinPrice = ""
    var selectedMaxPrice = ""


    var isLoading = false

    fun setVariable(mBinding: FragmentMyOrderBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!

        (mActivity as HomeActivity).imageViewFilter.setOnClickListener { onOrderFilterClick() }
        (mActivity as HomeActivity).imageViewSort.setOnClickListener { onOrderSortByClick() }
    }

    private fun onOrderFilterClick() {
        val bottomSheetFragment = OrderFilterDialogFragment(selectedStartDate, selectedEndDate, selectedMinPrice, selectedMaxPrice)
        bottomSheetFragment.show((mActivity as HomeActivity).supportFragmentManager, bottomSheetFragment.tag)
        bottomSheetFragment.setOnItemClickListener(object : OrderFilterDialogFragment.OnProductFilterClickListener {
            override fun onSubmitClick(startDate: String, endDate: String, minPrice: String, maxPrice: String) {
                isLastPage.value = true
                currentPage.value = 1
                orderListAll.clear()

                selectedStartDate = startDate
                selectedEndDate = endDate
                selectedMinPrice = minPrice
                selectedMaxPrice = maxPrice

                val requestParams = HashMap<String, String>()
                requestParams["page_no"] = currentPage.value.toString()
                requestParams["keyword"] = ""
                requestParams["date_start"] = selectedStartDate
                requestParams["date_end"] = selectedEndDate
                requestParams["price_start"] = selectedMinPrice
                requestParams["price_end"] = selectedMaxPrice
                setupObservers(false, requestParams)
            }

            override fun onSortItemClick(filterValue: String) {
            }

        })

    }

    private fun onOrderSortByClick() {
        val bottomSheetFragment = OrderSortByDialogFragment()
        bottomSheetFragment.show((mActivity as HomeActivity).supportFragmentManager, bottomSheetFragment.tag)
    }

    private fun getOrderList(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getOrderList(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun getProfile() = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getProfile()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setupObservers(isSwipeRefresh: Boolean, requestParams: HashMap<String, String>) {
        isLoading = true
        getOrderList(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                mBinding.swipeRefresh.isRefreshing = false
                when (resource.status) {
                    Status.SUCCESS -> {
                        isLoading = false
                        resource.data?.let { users ->
                            val response = users.data as OrderListResponse
                            isItemAvailable.value = response.docs.size > 0
                            totalOrderCount.value = response.docs.size
                            isLastPage.value = (currentPage.value == response.totalPages)
                            orderListAll.addAll(response.docs)
                            orderListData.value = orderListAll
                            //((mActivity as HomeActivity).getCurrentFragment() as MyOrderFragment).orderAdapter.notifyDataSetChanged()
                            if (isSwipeRefresh) {
                                setupObservers()
                            }
                        }
                    }

                    Status.ERROR -> {
                        isLoading = false
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

    private fun setupObservers() {
        getProfile().observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            //if (users.status == "success") {
                            //userData.value = users.data
                            AppPreferencesHelper.getInstance().userName = users.data!!.userName
                            AppPreferencesHelper.getInstance().userImage = users.data!!.userImageUrl
                            AppPreferencesHelper.getInstance().onlineStatus = users.data!!.onlineStatus
                            AppPreferencesHelper.getInstance().userImageUrl = users.data!!.userImageUrl
                            val localBroadCast = LocalBroadcastManager.getInstance(mActivity)
                            val intent = Intent(AppConstants.ACTION_BROADCAST_UPDATE_PROFILE)
                            localBroadCast.sendBroadcast(intent)
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
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

}