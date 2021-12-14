package com.app.ia.driver.model

import com.app.ia.driver.DriverApplication
import com.app.ia.driver.R
import com.app.ia.driver.enums.PaymentMode
import com.app.ia.driver.utils.CommonUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class OrderListResponse(

    @Expose
    @SerializedName("docs")
    var docs: MutableList<Docs>,

    @Expose
    @SerializedName("totalDocs")
    var totalDocs: Int,

    @Expose
    @SerializedName("limit")
    var limit: Int,

    @Expose
    @SerializedName("totalPages")
    var totalPages: Int,

    @Expose
    @SerializedName("page")
    var page: Int,

    @Expose
    @SerializedName("pagingCounter")
    var pagingCounter: Int,

    @Expose
    @SerializedName("hasPrevPage")
    var hasPrevPage: Boolean,

    @Expose
    @SerializedName("hasNextPage")
    var hasNextPage: Boolean
) {

    data class Docs(
        @Expose
        @SerializedName("_id")
        val Id: String = "",

        @Expose
        @SerializedName("order_id")
        val orderId: String = "",

        @Expose
        @SerializedName("total_amount")
        val totalAmount: String = "",

        @Expose
        @SerializedName("net_amount")
        val netAmount: String = "",

        @Expose
        @SerializedName("order_status")
        val orderStatus: Int = 0,

        @Expose
        @SerializedName("user_id")
        val userDetail: UserDetail,

        @Expose
        @SerializedName("delivery_address")
        val addressDetail: OrderDetailResponse.DeliveryAddress,

        @Expose
        @SerializedName("payment_mode")
        val paymentMode: String = "",

        @Expose
        @SerializedName("createdAt")
        val createdAt: String = ""
    ) : Serializable {

        fun getTotalAmountValue(): String {
            return CommonUtils.convertToDecimal(totalAmount)
        }

        fun getNetAmountValue(): String {
            return CommonUtils.convertToDecimal(netAmount)
        }

        fun getOrderDate(): String {
            val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val outputDate: String
            outputDate = try {
                val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value: Date = formatter.parse(createdAt)!!
                val timeZone = TimeZone.getDefault()
                val dateFormatter = SimpleDateFormat("dd MMMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
                dateFormatter.timeZone = timeZone
                dateFormatter.format(value)
            } catch (e: Exception) {
                if (createdAt.isEmpty()) {
                    ""
                } else {
                    createdAt
                }
            }
            return outputDate
        }

        data class UserDetail(

            @Expose
            @SerializedName("_id")
            val Id: String = "",

            @Expose
            @SerializedName("username")
            val userName: String = "",

            @Expose
            @SerializedName("phone")
            val phone: String = "")

        fun getPaymentMode(value: String): String {
            return if (value == PaymentMode.CASH_ON_DELIVERY.paymentMode) {
                "COD"
            } else {
                "Pre-Paid"
            }
        }

        fun getPaymentText(value: String): String {
            return if (value == PaymentMode.CASH_ON_DELIVERY.paymentMode) {
                DriverApplication.getInstance().getString(R.string.payable_amount)
            } else {
                DriverApplication.getInstance().getString(R.string.paid_amount)
            }
        }
    }
}
