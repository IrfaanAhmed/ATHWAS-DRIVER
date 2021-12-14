package com.app.ia.driver.ui.my_order

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseFragment
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.FragmentMyOrderBinding
import com.app.ia.driver.ui.my_order.adapter.MyOrderAdapter
import com.app.ia.driver.utils.AppLogger
import com.app.ia.driver.utils.EqualSpacingItemDecoration
import com.app.ia.driver.utils.RecyclerViewPaginator
import kotlinx.android.synthetic.main.fragment_my_order.*

class MyOrderFragment : BaseFragment<FragmentMyOrderBinding, MyOrderViewModel>() {

    private var mFragmentHomeBinding: FragmentMyOrderBinding? = null
    private var mMyOrderViewModel: MyOrderViewModel? = null

    lateinit var orderAdapter: MyOrderAdapter
    //private lateinit var recyclerViewPaging: RecyclerViewPaginator
    private var keyword = ""

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_my_order

    override val viewModel: MyOrderViewModel
        get() = mMyOrderViewModel!!

    companion object {
        fun newInstance(): MyOrderFragment {
            val args = Bundle()
            val fragment = MyOrderFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mMyOrderViewModel?.setActivityNavigator(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentHomeBinding = viewDataBinding!!
        mFragmentHomeBinding?.lifecycleOwner = this
        mMyOrderViewModel?.setVariable(mFragmentHomeBinding!!)

        recViewOrder.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))

        orderAdapter = MyOrderAdapter()
        recViewOrder.adapter = orderAdapter

        /*recyclerViewPaging = object : RecyclerViewPaginator(recViewOrder) {
            override val isLastPage: Boolean
                get() = mMyOrderViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mMyOrderViewModel?.currentPage?.value = start
                val requestParams = HashMap<String, String>()
                requestParams["page_no"] = start.toString()
                requestParams["keyword"] = keyword
                mMyOrderViewModel?.setupObservers(false, requestParams)
            }
        }

        recViewOrder.addOnScrollListener(recyclerViewPaging)*/

        recViewOrder?.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //super.onScrolled(recyclerView, dx, dy)
                Log.d("Scrolled", "Scrolled")
                val totalItemCount = (recViewOrder.layoutManager as LinearLayoutManager).itemCount
                val lastVisibleItem = (recViewOrder.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                Log.d("Scrolled", "Scrolled ${totalItemCount} $lastVisibleItem")
                if (!mMyOrderViewModel?.isLoading!! && mMyOrderViewModel?.isLastPage?.value == false && totalItemCount == (lastVisibleItem + 1)) {
                    mMyOrderViewModel?.isLoading = true
                    mMyOrderViewModel?.currentPage?.value = mMyOrderViewModel?.currentPage?.value!! + 1

                    val requestParams = HashMap<String, String>()
                    requestParams["page_no"] = mMyOrderViewModel?.currentPage?.value.toString()
                    requestParams["keyword"] = keyword
                    mMyOrderViewModel?.setupObservers(false, requestParams)
                }
            }
        })

        mMyOrderViewModel?.orderListData?.observe(requireActivity(), {
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

        swipeRefresh.setOnRefreshListener {
            resetOrderList(false)
        }
    }

    override fun onResume() {
        super.onResume()
        AppLogger.d("Reset","Resume Called")
        if(mMyOrderViewModel?.isLoading == true)
            return
        mMyOrderViewModel?.currentPage?.value = 1
        mMyOrderViewModel?.orderListAll?.clear()
        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = mMyOrderViewModel?.currentPage?.value.toString()
        requestParams["keyword"] = keyword
        mMyOrderViewModel?.setupObservers(false, requestParams)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(MyOrderViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mMyOrderViewModel = ViewModelProvider(this, factory).get(MyOrderViewModel::class.java)
    }

    fun resetOrderList(fromNotification: Boolean) {
        //recyclerViewPaging.reset()
        AppLogger.d("Reset","Reset Called")
        if(mMyOrderViewModel?.isLoading == true)
            return

        mMyOrderViewModel?.isLastPage!!.value = true
        mMyOrderViewModel?.currentPage?.value = 1
        mMyOrderViewModel?.orderListAll?.clear()

        mMyOrderViewModel?.selectedStartDate = ""
        mMyOrderViewModel?.selectedEndDate = ""
        mMyOrderViewModel?.selectedMinPrice = ""
        mMyOrderViewModel?.selectedMaxPrice = ""

        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = mMyOrderViewModel?.currentPage?.value.toString()
        requestParams["keyword"] = keyword
        mMyOrderViewModel?.setupObservers(!fromNotification, requestParams)
    }

}