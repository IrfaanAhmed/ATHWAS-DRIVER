package com.app.ia.driver.ui.order_history

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.ActivityOrderHistoryBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.dialog.bottom_sheet.OrderFilterDialogFragment
import com.app.ia.driver.enums.Status
import com.app.ia.driver.model.OrderListResponse
import com.app.ia.driver.utils.CommonUtils
import com.app.ia.driver.utils.Resource
import kotlinx.android.synthetic.main.common_header.view.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class OrderHistoryViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityOrderHistoryBinding

    val isItemAvailable = MutableLiveData(true)
    val currentPage = MutableLiveData(1)
    private val totalOrderCount = MutableLiveData(0)
    val isLastPage = MutableLiveData(false)
    var orderListData = MutableLiveData<MutableList<OrderListResponse.Docs>>()
    var orderListAll = ArrayList<OrderListResponse.Docs>()

    var selectedStartDate = ""
    var selectedEndDate = ""
    var selectedMinPrice = ""
    var selectedMaxPrice = ""

    fun setVariable(mBinding: ActivityOrderHistoryBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.order_history))

        this.mBinding.toolbar.imageViewFilter.setOnClickListener { onOrderFilterClick() }
    }

    private fun onOrderFilterClick() {
        val bottomSheetFragment = OrderFilterDialogFragment(selectedStartDate, selectedEndDate, selectedMinPrice, selectedMaxPrice)
        bottomSheetFragment.show((mActivity as OrderHistoryActivity).supportFragmentManager, bottomSheetFragment.tag)
        bottomSheetFragment.setOnItemClickListener(object : OrderFilterDialogFragment.OnProductFilterClickListener {
            override fun onSubmitClick(startDate: String, endDate: String, minPrice: String, maxPrice: String) {

                selectedStartDate = startDate
                selectedEndDate = endDate
                selectedMinPrice = minPrice
                selectedMaxPrice = maxPrice
                orderListAll.clear()

                val requestParams = HashMap<String, String>()
                requestParams["page_no"] = currentPage.value.toString()
                requestParams["keyword"] = ""
                requestParams["status"] = "2"
                requestParams["date_start"] = selectedStartDate
                requestParams["date_end"] = selectedEndDate
                requestParams["price_start"] = selectedMinPrice
                requestParams["price_end"] = selectedMaxPrice
                setupObservers(requestParams)
            }

            override fun onSortItemClick(filterValue: String) {
            }

        })
    }

    private fun getOrderList(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getOrderList(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setupObservers(requestParams: HashMap<String, String>) {

        getOrderList(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                mBinding.swipeRefresh.isRefreshing = false
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            val response = users.data as OrderListResponse
                            isItemAvailable.value = response.docs.size > 0
                            totalOrderCount.value = response.docs.size
                            isLastPage.value = (currentPage.value == response.totalPages)
                            orderListAll.addAll(response.docs)
                            orderListData.value = orderListAll
                            (mActivity as OrderHistoryActivity).orderAdapter.notifyDataSetChanged()
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