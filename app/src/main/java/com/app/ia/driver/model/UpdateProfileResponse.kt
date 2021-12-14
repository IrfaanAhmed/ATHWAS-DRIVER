package com.app.ia.driver.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UpdateProfileResponse(
    @Expose @SerializedName("country_code") var countryCode: String = "",
    @Expose @SerializedName("phone") var phone: String = "",
    @Expose @SerializedName("email") var email: String = "",
    @Expose @SerializedName("otp_for") var otpFor: String = "",
    @Expose @SerializedName("otp_number") var otpNumber: String = ""): Serializable {

}