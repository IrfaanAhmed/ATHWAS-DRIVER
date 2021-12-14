package com.app.ia.driver.ui.order_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.driver.databinding.OrderDetailListRowBinding
import com.app.ia.driver.model.OrderDetailResponse
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailListAdapter : ListAdapter<OrderDetailResponse.Category, OrderDetailListAdapter.OrderDetailViewHolder>(OffersListDiffCallback()) {

    private var orderDate = ""

    fun setOrderDateTime(orderDate: String) {
        this.orderDate = orderDate
    }

    class OffersListDiffCallback : DiffUtil.ItemCallback<OrderDetailResponse.Category>() {

        override fun areItemsTheSame(oldItem: OrderDetailResponse.Category, newItem: OrderDetailResponse.Category): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OrderDetailResponse.Category, newItem: OrderDetailResponse.Category): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        return OrderDetailViewHolder(OrderDetailListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class OrderDetailViewHolder(private val mBinding: OrderDetailListRowBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: OrderDetailResponse.Category, position: Int) {
            mBinding.apply {
                itemCategory = productItem.name

                val adapter = OrderDetailListItemAdapter()
                recViewOrderDetailItem.adapter = adapter
                adapter.submitList(productItem.products)
                recViewOrderDetailItem.isNestedScrollingEnabled = false
                executePendingBindings()
            }
        }
    }


    fun returnVisibility(returnTime: Int): Boolean {
        val orderDate = getOrderDate()
        val finalReturnTime = addMinutesToDate(returnTime, orderDate)
        if (finalReturnTime.before(Date())) {
            return true
        }
        return false
    }

    fun cancelVisibility(cancelTime: Int): Boolean {
        val orderDate = getOrderDate()
        val finalCancelTime = addMinutesToDate(cancelTime, orderDate)
        if (finalCancelTime.before(Date())) {
            return true
        }
        return false
    }

    private fun getOrderDate(): Date {
        val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        return try {
            val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value: Date = formatter.parse(orderDate)!!
            val timeZone = TimeZone.getDefault()
            val dateFormatter = SimpleDateFormat("dd MMMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
            dateFormatter.timeZone = timeZone
            val dateTime = dateFormatter.format(value)
            dateFormatter.parse(dateTime)!!
        } catch (e: Exception) {
            Date()
        }
    }

    private fun addMinutesToDate(minutes: Int, beforeTime: Date): Date {
        val ONE_MINUTE_IN_MILLIS: Long = 60000 //millisecs
        val curTimeInMs = beforeTime.time
        return Date(curTimeInMs + minutes * ONE_MINUTE_IN_MILLIS)
    }


}