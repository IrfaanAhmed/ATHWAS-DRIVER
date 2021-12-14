package com.app.ia.driver.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OrderStatsResponse(

    @Expose
    @SerializedName("totalOrder")
    val totalOrder: Int,
    @Expose
    @SerializedName("totalDeliveredOrder")
    val totalDeliveredOrder: Int,
    @Expose
    @SerializedName("totalNotDeliveredOrder")
    val totalNotDeliveredOrder: Int)