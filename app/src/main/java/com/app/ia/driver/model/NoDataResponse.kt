package com.app.ia.driver.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NoDataResponse(

    val noData: String = "",

    @Expose
    @SerializedName("user_id")
    val userId: String)