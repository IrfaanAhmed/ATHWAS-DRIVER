package com.app.ia.driver.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResendOTPResponse(

    @Expose
    @SerializedName("country_code")
    val countryCode: String,

    @Expose
    @SerializedName("phone")
    val phone: String,

    @Expose
    @SerializedName("otp_number")
    val otpNumber: String)