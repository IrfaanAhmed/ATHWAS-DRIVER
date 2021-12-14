package com.app.ia.driver.ui.order_detail.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.driver.databinding.OrderDetailListItemBinding
import com.app.ia.driver.model.OrderDetailResponse
import kotlinx.android.synthetic.main.order_detail_list_item.view.*

class OrderDetailListItemAdapter : ListAdapter<OrderDetailResponse.Category.Products, OrderDetailListItemAdapter.OrderDetailViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<OrderDetailResponse.Category.Products>() {

        override fun areItemsTheSame(oldItem: OrderDetailResponse.Category.Products, newItem: OrderDetailResponse.Category.Products): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OrderDetailResponse.Category.Products, newItem: OrderDetailResponse.Category.Products): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        return OrderDetailViewHolder(OrderDetailListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class OrderDetailViewHolder(private val mBinding: OrderDetailListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: OrderDetailResponse.Category.Products, position: Int) {
            mBinding.apply {
                product = productItem
                itemView.tvActualPrice.paintFlags = itemView.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                when (productItem.orderStatus) {
                    4 -> {
                        txtOrderStatus.text = "Cancelled"
                        txtOrderStatus.setTextColor(Color.RED)
                        txtOrderStatus.visibility = View.VISIBLE
                    }

                    2 -> {
                        txtOrderStatus.text = "Returned"
                        txtOrderStatus.setTextColor(Color.BLUE)
                        txtOrderStatus.visibility = View.VISIBLE
                    }

                    else -> {
                        txtOrderStatus.visibility = View.GONE
                    }
                }
                executePendingBindings()
            }
        }
    }

}