package com.app.ia.driver.ui.offers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.driver.databinding.NotificationListItemBinding
import com.app.ia.driver.databinding.OfferListItemBinding
import com.app.ia.driver.databinding.OrderListItemBinding

class OfferListAdapter : ListAdapter<String, OfferListAdapter.OfferViewHolder>(
    OffersListDiffCallback()
) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        return OfferViewHolder(OfferListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class OfferViewHolder(private val mBinding: OfferListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(profileItem: String, position: Int) {
            mBinding.apply {
                /*orderItem = profileItem
                itemView.setOnClickListener {
                    itemView.context.startActivity<OrderDetailActivity> {
                        putExtra("order_id", profileItem.Id)
                    }
                }*/
                executePendingBindings()
            }
        }
    }
}