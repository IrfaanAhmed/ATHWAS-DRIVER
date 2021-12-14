package com.app.ia.driver.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.driver.R
import com.app.ia.driver.databinding.SideMenuListItemBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.ui.contact_us.ContactUsActivity
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.ui.order_history.OrderHistoryActivity
import com.app.ia.driver.ui.order_stats.OrderStatsActivity
import com.app.ia.driver.ui.static_page.StaticPageActivity
import com.app.ia.driver.ui.webview.WebViewActivity
import com.app.ia.driver.utils.AppConstants
import com.app.ia.driver.utils.startActivity

class NavigationListAdapter :
    ListAdapter<String, NavigationListAdapter.NavigationViewHolder>(
        OffersListDiffCallback()
    ) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }



    override fun onBindViewHolder(holder: NavigationViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationViewHolder {
        return NavigationViewHolder(
            SideMenuListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class NavigationViewHolder(private val mBinding: SideMenuListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(item: String) {
            mBinding.apply {
                navigationItem = item
                executePendingBindings()

                itemView.setOnClickListener {
                    when (adapterPosition) {
                        0 -> {
                            itemView.context.startActivity<OrderHistoryActivity>()
                        }
                        1 -> {
                            itemView.context.startActivity<OrderStatsActivity>()
                        }
                        2 -> {
                            itemView.context.startActivity<WebViewActivity> {
                                putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, itemView.context.getString(R.string.about_us))
                                putExtra(AppConstants.EXTRA_WEBVIEW_URL, "about_us")
                            }
                        }
                        3 -> {
                            itemView.context.startActivity<WebViewActivity> {
                                putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, itemView.context.getString(R.string.contact_us))
                                putExtra(AppConstants.EXTRA_WEBVIEW_URL, "contact_us")
                            }
                        }
                        4 -> {
                            (itemView.context as HomeActivity).mViewModel!!.onLogoutClick()
                        }
                    }
                }

            }
        }
    }

}