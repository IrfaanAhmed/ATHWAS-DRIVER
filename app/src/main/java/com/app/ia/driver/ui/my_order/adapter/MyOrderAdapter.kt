package com.app.ia.driver.ui.my_order.adapter

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.driver.databinding.OrderListItemBinding
import com.app.ia.driver.model.OrderListResponse
import com.app.ia.driver.ui.order_detail.OrderDetailActivity
import com.app.ia.driver.utils.startActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.order_list_item.view.*

class MyOrderAdapter :
    ListAdapter<OrderListResponse.Docs, MyOrderAdapter.MyOrderViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<OrderListResponse.Docs>() {

        override fun areItemsTheSame(oldItem: OrderListResponse.Docs, newItem: OrderListResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OrderListResponse.Docs, newItem: OrderListResponse.Docs): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderViewHolder {
        return MyOrderViewHolder(OrderListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class MyOrderViewHolder(private val mBinding: OrderListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(item: OrderListResponse.Docs, position: Int) {
            mBinding.apply {
                orderItem = item
                itemView.setOnClickListener {
                    itemView.context.startActivity<OrderDetailActivity> {
                        putExtra("order_id", item.Id)
                    }
                }

                when (item.orderStatus) {
                    3 -> {
                        val statusGradient = txtViewOrderStatus.background as GradientDrawable
                        statusGradient.setStroke(2, Color.BLUE)
                        txtViewOrderStatus.text = "Status : Returned"
                        txtViewOrderStatus.setTextColor(Color.BLUE)
                        txtViewOrderStatus.visibility = View.GONE
                    }

                    4 -> {
                        val statusGradient = txtViewOrderStatus.background as GradientDrawable
                        statusGradient.setStroke(2, Color.RED)
                        txtViewOrderStatus.text = "Status : Cancelled"
                        txtViewOrderStatus.setTextColor(Color.RED)
                        txtViewOrderStatus.visibility = View.VISIBLE
                    }

                    else -> {
                        val statusGradient = txtViewOrderStatus.background as GradientDrawable
                        statusGradient.setStroke(2, Color.parseColor("#a2c027"))
                        //txtViewOrderStatus.text = "Status : " + item.currentTrackingStatus
                        txtViewOrderStatus.setTextColor(Color.parseColor("#a2c027"))
                        txtViewOrderStatus.visibility = View.GONE
                    }
                }


                itemView.ivPhoneCall.setOnClickListener {
                    Dexter.withContext(itemView.context as Activity)
                        .withPermissions(Manifest.permission.CALL_PHONE)
                        .withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                if (report.areAllPermissionsGranted()) {
                                    val intent = Intent(Intent.ACTION_CALL)
                                    intent.data = Uri.parse("tel:${item.userDetail.phone}")
                                    itemView.context.startActivity(intent)
                                }
                            }

                            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                                token.continuePermissionRequest()
                            }

                        }).check()
                }
                executePendingBindings()
            }
        }
    }
}