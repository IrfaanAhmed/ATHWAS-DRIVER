package com.app.ia.driver.ui.order_history

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseActivity
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.ActivityOrderHistoryBinding
import com.app.ia.driver.ui.my_order.adapter.MyOrderAdapter
import com.app.ia.driver.utils.EqualSpacingItemDecoration
import com.app.ia.driver.utils.RecyclerViewPaginator
import com.app.ia.driver.utils.setOnApplyWindowInset
import com.app.ia.driver.utils.visible
import kotlinx.android.synthetic.main.activity_order_history.*
import kotlinx.android.synthetic.main.common_header.view.*

class OrderHistoryActivity : BaseActivity<ActivityOrderHistoryBinding, OrderHistoryViewModel>() {

    private var mActivityBinding: ActivityOrderHistoryBinding? = null
    private var mViewModel: OrderHistoryViewModel? = null

    lateinit var orderAdapter: MyOrderAdapter
    private lateinit var recyclerViewPaging: RecyclerViewPaginator
    private var keyword = ""

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order_history
    }

    override fun getViewModel(): OrderHistoryViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityBinding = getViewDataBinding()
        mActivityBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mActivityBinding!!)

        //makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)

        toolbar.imageViewFilter.visible()

        recViewOrder.addItemDecoration(
            EqualSpacingItemDecoration(
                20,
                EqualSpacingItemDecoration.VERTICAL
            )
        )

        recViewOrder.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        orderAdapter = MyOrderAdapter()
        recViewOrder.adapter = orderAdapter

        recyclerViewPaging = object : RecyclerViewPaginator(recViewOrder) {
            override val isLastPage: Boolean
                get() = mViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mViewModel?.currentPage?.value = start
                val requestParams = HashMap<String, String>()
                requestParams["page_no"] = start.toString()
                requestParams["keyword"] = keyword
                requestParams["status"] = "2"
                requestParams["date_start"] = mViewModel?.selectedStartDate!!
                requestParams["date_end"] = mViewModel?.selectedEndDate!!
                requestParams["price_start"] = mViewModel?.selectedMinPrice!!
                requestParams["price_end"] = mViewModel?.selectedMaxPrice!!
                mViewModel?.setupObservers(requestParams)
            }
        }

        recViewOrder.addOnScrollListener(recyclerViewPaging)

        mViewModel?.orderListData?.observe(this@OrderHistoryActivity, {
            if (it.size <= 0) {
                orderAdapter.notifyDataSetChanged()
            } else {
                if (orderAdapter.currentList.size == 0) {
                    orderAdapter.submitList(it)
                } else {
                    orderAdapter.notifyDataSetChanged()
                }
            }
        })

        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = mViewModel?.currentPage?.value.toString()
        requestParams["keyword"] = keyword
        requestParams["status"] = "2"
        mViewModel?.setupObservers(requestParams)

        swipeRefresh.setOnRefreshListener {
            resetOrderList()
        }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(OrderHistoryViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(OrderHistoryViewModel::class.java)
    }

    fun resetOrderList() {
        recyclerViewPaging.reset()
        mViewModel?.isLastPage!!.value = true
        mViewModel?.currentPage?.value = 1
        mViewModel?.orderListAll?.clear()

        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = mViewModel?.currentPage?.value.toString()
        requestParams["keyword"] = keyword
        requestParams["status"] = "2"
        mViewModel?.setupObservers(requestParams)
    }
}