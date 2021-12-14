package com.app.ia.driver.ui.order_detail

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseActivity
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.ActivityOrderDetailBinding
import com.app.ia.driver.ui.order_detail.adapter.OrderDetailListAdapter
import com.app.ia.driver.utils.AppConstants
import com.app.ia.driver.utils.setOnApplyWindowInset
import com.app.ia.driver.utils.visible
import com.transferwise.sequencelayout.SequenceStep
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.activity_order_status.content_container
import kotlinx.android.synthetic.main.activity_order_status.toolbar
import kotlinx.android.synthetic.main.common_header.view.*
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailActivity : BaseActivity<ActivityOrderDetailBinding, OrderDetailViewModel>() {

    private var mBinding: ActivityOrderDetailBinding? = null
    private var mViewModel: OrderDetailViewModel? = null

    var orderDetailListAdapter: OrderDetailListAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order_detail
    }

    override fun getViewModel(): OrderDetailViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!, intent)

        //makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)
        toolbar.imageViewBack.visible()

        recyclerViewOrderList.isNestedScrollingEnabled = false

        orderDetailListAdapter = OrderDetailListAdapter()
        recyclerViewOrderList.adapter = orderDetailListAdapter

        mViewModel?.orderDetailResponse?.observe(this, {
            orderDetailListAdapter?.setOrderDateTime(it?.getOrderDate()!!)
            if (it.category.isEmpty()) {
                orderDetailListAdapter?.notifyDataSetChanged()
            } else {
                if (orderDetailListAdapter?.currentList?.size!! == 0) {
                    orderDetailListAdapter?.submitList(it.category)
                } else {
                    orderDetailListAdapter?.submitList(it.category)
                }
            }

            if (trackLayout != null) {
                trackLayout.removeAllSteps()
            }
            var activePosition = 0

            if (it.trackingStatus.acknowledged.status == 1) {
                activePosition = 0
            }
            if (it.trackingStatus.packed.status == 1) {
                activePosition = 1
            }
            if (it.trackingStatus.inTransit != null && it.trackingStatus.inTransit?.status == 1) {
                activePosition = 2
            }
            if (it.trackingStatus.delivered != null && it.trackingStatus.delivered?.status == 1) {
                activePosition = 3
            }

            showTracking(it.trackingStatus.acknowledged.statusTitle, it.trackingStatus.acknowledged.time, activePosition == 0)
            showTracking(it.trackingStatus.packed.statusTitle, it.trackingStatus.packed.time, activePosition == 1)
            if(it.trackingStatus.inTransit != null) {
                showTracking(it.trackingStatus.inTransit?.statusTitle!!, it.trackingStatus.inTransit!!.time, activePosition == 2)
            }
            if(it.trackingStatus.delivered != null) {
                showTracking(it.trackingStatus.delivered?.statusTitle!!, it.trackingStatus.delivered!!.time, activePosition == 3)
            }
            trackLayout.start()
        })

        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this@OrderDetailActivity)
        val intentFilter = IntentFilter()
        intentFilter.addAction(AppConstants.ACTION_BROADCAST_ORDER_UPDATE)
        localBroadcastReceiver.registerReceiver(updateOrderListReceiver, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        mViewModel?.orderDetailObserver(mViewModel?.order_id?.value!!)
    }

    private val updateOrderListReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mViewModel?.orderDetailObserver(mViewModel?.order_id?.value!!)
        }
    }

    override fun onDestroy() {
        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this@OrderDetailActivity)
        localBroadcastReceiver.unregisterReceiver(updateOrderListReceiver)
        super.onDestroy()
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(OrderDetailViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(OrderDetailViewModel::class.java)
    }

    private fun showTracking(title: String, time: String?, isActive: Boolean) {
        val sequenceView = SequenceStep(this)
        val param = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        sequenceView.setAnchorTextAppearance(R.style.stepSubTitle)
        sequenceView.setTitleTextAppearance(R.style.stepTitle)
        sequenceView.setSubtitleTextAppearance(R.style.stepSubTitle)
        sequenceView.layoutParams = param
        if (time.isNullOrBlank()) {
            sequenceView.setAnchor(" ")
        } else {
            sequenceView.setAnchor(getTrackTime(time))
        }
        sequenceView.setSubtitle("")
        sequenceView.setTitle(title)
        sequenceView.setActive(isActive)
        trackLayout.addView(sequenceView)
    }

    @SuppressLint("NewApi", "WeekBasedYear")
    private fun getTrackTime(trackTime: String): String {
        val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        return try {
            val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value: Date = formatter.parse(trackTime)!!
            val timeZone = TimeZone.getDefault()
            val dateFormatter = SimpleDateFormat("dd MMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
            dateFormatter.timeZone = timeZone
            dateFormatter.format(value)
        } catch (e: Exception) {
            if (trackTime.isEmpty()) {
                "01 May 2020, 10:00 AM"
            } else {
                trackTime
            }
        }
    }
}