package com.app.ia.driver.model

import com.app.ia.driver.DriverApplication
import com.app.ia.driver.R
import com.app.ia.driver.enums.PaymentMode
import com.app.ia.driver.utils.CommonUtils
import com.app.ia.driver.utils.redact
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


data class OrderDetailResponse(

    @Expose
    @SerializedName("_id")
    val Id: String,
    @Expose
    @SerializedName("category")
    var category: List<Category>,
    @Expose
    @SerializedName("current_tracking_status")
    val currentTrackingStatus: String,
    @Expose
    @SerializedName("customer_phone")
    val customerPhone: String?,
    @Expose
    @SerializedName("customer_name")
    private val customerName: String?,
    @Expose
    @SerializedName("order_id")
    var orderId: String,
    @Expose
    @SerializedName("userData")
    var userdata: Userdata,
    @Expose
    @SerializedName("warehouseData")
    var warehousedata: List<WareHouseData>,
    @Expose
    @SerializedName("driver")
    var driver: List<Driver>,
    /*@Expose
    @SerializedName("promo_code_id")
    var promoCodeIds: List<String>,*/
    @Expose
    @SerializedName("promo_code")
    var promoCode: String,
    @Expose
    @SerializedName("order_status")
    var orderStatus: Int,
    @Expose
    @SerializedName("tracking_status")
    var trackingStatus: TrackingStatus,
    @Expose
    @SerializedName("offers")
    var offers: List<String>,
    @Expose
    @SerializedName("payment_mode")
    val paymentMode: String,
    @Expose
    @SerializedName("total_amount")
    private var totalAmount: String,
    @Expose
    @SerializedName("discount")
    var discount: String,
    @Expose
    @SerializedName("net_amount")
    private var netAmount: String,
    @Expose
    @SerializedName("vat_amount")
    var vat_amount: String,
    @Expose
    @SerializedName("delivery_fee")
    var deliveryFee: String,
    @Expose
    @SerializedName("delivery_address")
    var deliveryAddress: DeliveryAddress,
    @Expose
    @SerializedName("order_date")
    private val orderDate: String,
    @Expose
    @SerializedName("order_cancel_time")
    val orderCancelTime: String,
    @Expose
    @SerializedName("order_return_time")
    val orderReturnTime: String) {

    fun getCustomerName(): String {
        if (customerName.isNullOrEmpty()) {
            return ""
        }
        return customerName
    }

    fun getCustomerPhones(): String {
        if (customerPhone.isNullOrEmpty()) {
            return ""
        }
        var number = customerPhone
        if (customerPhone.isNotEmpty()) {
            number = customerPhone.redact()
        }
        return number
    }

    fun getNetAmount(): String {
        return CommonUtils.convertToDecimal(netAmount)
    }

    fun getTotalAmount(): String {
        return CommonUtils.convertToDecimal(totalAmount)
    }

    fun getOrderDate(): String {
        val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        return try {
            val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value: Date = formatter.parse(orderDate)!!
            val timeZone = TimeZone.getDefault()
            val dateFormatter = SimpleDateFormat("dd MMMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
            dateFormatter.timeZone = timeZone
            dateFormatter.format(value)
        } catch (e: Exception) {
            if (orderDate.isEmpty()) {
                ""
            } else {
                orderDate
            }
        }
    }

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

    data class DeliveryAddress(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("full_address")
        val fullAddress: String,
        @Expose
        @SerializedName("address_type")
        val addressType: String) : Serializable

    data class TrackingStatus(
        @Expose
        @SerializedName("Acknowledged")
        var acknowledged: Acknowledged,
        @Expose
        @SerializedName("Packed")
        var packed: Packed,
        @Expose
        @SerializedName("In_Transit")
        var inTransit: InTransit?,
        @Expose
        @SerializedName("Delivered")
        var delivered: Delivered?)

    data class Delivered(
        @Expose
        @SerializedName("status")
        var status: Int,
        @Expose
        @SerializedName("status_title")
        val statusTitle: String,
        @Expose
        @SerializedName("time")
        val time: String)

    data class InTransit(
        @Expose
        @SerializedName("status")
        var status: Int,
        @Expose
        @SerializedName("status_title")
        val statusTitle: String,
        @Expose
        @SerializedName("time")
        val time: String)

    data class Packed(
        @Expose
        @SerializedName("status")
        var status: Int,
        @Expose
        @SerializedName("status_title")
        val statusTitle: String,
        @Expose
        @SerializedName("time")
        val time: String)

    data class Acknowledged(
        @Expose
        @SerializedName("status")
        var status: Int,
        @Expose
        @SerializedName("status_title")
        val statusTitle: String,
        @Expose
        @SerializedName("time")
        val time: String
    )

    data class WareHouseData(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("address")
        val address: String)

    data class Driver(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("username")
        val username: String,
        @Expose
        @SerializedName("phone")
        val phone: String)

    data class Userdata(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("username")
        val username: String,
        @Expose
        @SerializedName("phone")
        val phone: String) {

        fun getMaskedPhoneNumber(): String {
            var number = phone
            if (phone.isNotEmpty()) {
                number = phone.redact()
            }
            return number
        }
    }

    data class Category(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("cancelation_time")
        var cancellationTime: Int,
        @Expose
        @SerializedName("return_time")
        var returnTime: Int,
        @Expose
        @SerializedName("all_return")
        var allReturn: Int,
        @Expose
        @SerializedName("category_image")
        val categoryImage: String,
        @Expose
        @SerializedName("products")
        var products: List<Products>) {

        data class Products(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("images")
            var images: List<Images>,
            @Expose
            @SerializedName("is_active")
            var isActive: Int,
            @Expose
            @SerializedName("is_deleted")
            var isDeleted: Int,
            @Expose
            @SerializedName("name")
            val name: String,
            @Expose
            @SerializedName("business_category_id")
            val businessCategoryId: String,
            @Expose
            @SerializedName("brand_id")
            val brandId: String,
            @Expose
            @SerializedName("category_id")
            val categoryId: String,
            @Expose
            @SerializedName("sub_category_id")
            val subCategoryId: String,
            @Expose
            @SerializedName("description")
            val description: String,
            @Expose
            @SerializedName("createdAt")
            val createdAt: String,
            @Expose
            @SerializedName("updatedAt")
            val updatedAt: String,
            @Expose
            @SerializedName("__v")
            var V: Int,
            @Expose
            @SerializedName("quantity")
            var quantity: Int,
            @Expose
            @SerializedName("price")
            var price: String,
            @Expose
            @SerializedName("is_discount")
            var isDiscount: Int,
            @Expose
            @SerializedName("offer_price")
            val offerPrice: String,
            @Expose
            @SerializedName("order_status")
            var orderStatus: Int,
            @Expose
            @SerializedName("SubCategoryData")
            var subcategory: Subcategory,
            @Expose
            @SerializedName("CategoryData")
            var category: CategoryName): Serializable{
            @JvmName("price")
            fun getPrice(): String {
                return CommonUtils.convertToDecimal(price)
            }
        }


        data class Images(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("product_image_url")
            val productImageUrl: String,
            @Expose
            @SerializedName("product_image_thumb_url")
            val productImageThumbUrl: String)

        data class Subcategory(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("name")
            val name: String)

        data class CategoryName(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("name")
            val name: String)


    }

    @JvmName("getDiscount1")
    fun getDiscount(): String {
        if (discount == null) {
            discount = "0"
        }
        return CommonUtils.convertToDecimal(discount)
    }
}
