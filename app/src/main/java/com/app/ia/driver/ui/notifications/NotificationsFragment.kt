package com.app.ia.driver.ui.notifications

import android.os.Bundle
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
import com.app.ia.driver.databinding.FragmentNotificationsBinding
import com.app.ia.driver.ui.notifications.adapter.NotificationAdapter
import com.app.ia.driver.utils.EqualSpacingItemDecoration
import com.app.ia.driver.utils.RecyclerViewPaginator
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationsFragment : BaseFragment<FragmentNotificationsBinding, NotificationsViewModel>() {

    lateinit var mFragmentBinding: FragmentNotificationsBinding
    lateinit var mViewModel: NotificationsViewModel

    lateinit var mNotificationAdapter: NotificationAdapter
    private lateinit var recyclerViewPaging: RecyclerViewPaginator

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_notifications

    override val viewModel: NotificationsViewModel
        get() = mViewModel!!

    companion object {
        const val NOTIFICATION_LIST = 1
        const val DELETE_NOTIFICATION = 2
        const val DELETE_ALL_NOTIFICATION = 3

        fun newInstance(): NotificationsFragment {
            val args = Bundle()
            val fragment = NotificationsFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mViewModel?.setActivityNavigator(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentBinding = viewDataBinding!!
        mFragmentBinding?.lifecycleOwner = this
        mViewModel?.setVariable(mFragmentBinding!!)

        recViewNotification.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        mNotificationAdapter = NotificationAdapter(mViewModel)
        recViewNotification.adapter = mNotificationAdapter

        /*recyclerViewPaging = object : RecyclerViewPaginator(recViewNotification) {
            override val isLastPage: Boolean
                get() = mViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mViewModel?.currentPage?.value = start
                val requestParams = HashMap<String, String>()
                requestParams["page_no"] = start.toString()
                mViewModel?.setupObservers(null, requestParams, NOTIFICATION_LIST, 1)
            }
        }

        recViewNotification.addOnScrollListener(recyclerViewPaging)*/

        mFragmentBinding?.recViewNotification?.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //super.onScrolled(recyclerView, dx, dy)
                //Log.d("Scrolled", "Scrolled")
                val totalItemCount = (mFragmentBinding.recViewNotification.layoutManager as LinearLayoutManager).itemCount
                val lastVisibleItem = (mFragmentBinding.recViewNotification.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                //Log.d("Scrolled", "Scrolled ${totalItemCount} $lastVisibleItem")
                if (!mViewModel?.isLoading && mViewModel.isLastPage.value == false && totalItemCount == (lastVisibleItem + 1)) {
                    mViewModel.isLoading = true
                    mViewModel?.currentPage?.value = mViewModel?.currentPage?.value!! + 1
                    val requestParams = HashMap<String, String>()
                    requestParams["page_no"] = mViewModel?.currentPage?.value.toString()
                    mViewModel?.setupObservers(null, requestParams, NOTIFICATION_LIST, -1)
                }
            }
        })

        mViewModel?.notificationListData?.observe(requireActivity(), {
            mNotificationAdapter.submitList(it)
        })

        /*val requestParams = HashMap<String, String>()
        requestParams["page_no"] = mViewModel?.currentPage?.value.toString()
        mViewModel?.setupObservers(null, requestParams, NOTIFICATION_LIST, 1)*/

        swipeRefresh.setOnRefreshListener {
            resetNotification()
        }

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(NotificationsViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(NotificationsViewModel::class.java)
    }

    fun resetNotification() {
//        recyclerViewPaging.reset()
        mViewModel?.isLastPage!!.value = true
        mViewModel?.currentPage?.value = 1
        mViewModel?.notificationListData?.value = ArrayList()

        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = mViewModel?.currentPage?.value.toString()
        mViewModel?.setupObservers(null, requestParams, NOTIFICATION_LIST, 1)
    }

}