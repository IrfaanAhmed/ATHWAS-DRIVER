package com.app.ia.driver.ui.notifications.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.driver.R
import com.app.ia.driver.databinding.NotificationListItemBinding
import com.app.ia.driver.databinding.OrderListItemBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.model.NotificationResponse
import com.app.ia.driver.ui.notifications.NotificationsFragment
import com.app.ia.driver.ui.notifications.NotificationsViewModel
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import kotlinx.android.synthetic.main.notification_list_item.view.*

class NotificationAdapter(private val mViewModel: NotificationsViewModel?) : ListAdapter<NotificationResponse.Docs, NotificationAdapter.NotificationViewHolder>(
    NotificationDiffCallback()
) {

    private val binderHelper = ViewBinderHelper()

    init {
        binderHelper.setOpenOnlyOne(true)
    }

    class NotificationDiffCallback : DiffUtil.ItemCallback<NotificationResponse.Docs>() {

        override fun areItemsTheSame(oldItem: NotificationResponse.Docs, newItem: NotificationResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NotificationResponse.Docs, newItem: NotificationResponse.Docs): Boolean {
            return oldItem.Id == newItem.Id
        }
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.apply {
            binderHelper.bind(itemView.findViewById(R.id.swipe_layout), getItem(position).Id)
            onBind(getItem(position), position)
            holder.itemView.swipe_layout.dragEdge = SwipeRevealLayout.DRAG_EDGE_RIGHT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(NotificationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class NotificationViewHolder(private val mBinding: NotificationListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(notificationData: NotificationResponse.Docs, position: Int) {
            mBinding.apply {
                notificationItem = notificationData
                executePendingBindings()

                deleteLayout.setOnClickListener {
                    val tivoDialog = DriverDialog(itemView.context as Activity, itemView.context.getString(R.string.are_you_sure_want_to_delete_notification), false)
                    tivoDialog.setOnItemClickListener(object  : DriverDialog.OnClickListener {

                        override fun onPositiveClick() {
                            mViewModel?.setupObservers(notificationData.Id, null, NotificationsFragment.DELETE_NOTIFICATION, position)
                        }

                        override fun onNegativeClick() {

                        }
                    })
                }
            }
        }
    }
}