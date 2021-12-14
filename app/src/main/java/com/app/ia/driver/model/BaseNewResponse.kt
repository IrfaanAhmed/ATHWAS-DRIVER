package com.app.ia.driver.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class BaseNewResponse<T> {

    @SerializedName("api_name")
    @Expose
    val apiName: String = ""

    @SerializedName("message")
    @Expose
    val message: String = ""

    @SerializedName("status")
    @Expose
    val status: String = ""

    @SerializedName("data")
    @Expose
    var data: MutableList<T>? = null
}
