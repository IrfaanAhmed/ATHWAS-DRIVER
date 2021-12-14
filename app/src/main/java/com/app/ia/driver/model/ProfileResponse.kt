package com.app.ia.driver.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @Expose @SerializedName("_id") var Id: String = "",
    @Expose @SerializedName("username") var userName: String = "",
    @Expose @SerializedName("online_status") var onlineStatus: Int = 0,
    @Expose @SerializedName("country_code") var countryCode: String = "",
    @Expose @SerializedName("phone") var phone: String = "",
    @Expose @SerializedName("email") var email: String = "",
    @Expose @SerializedName("vehicle_no") var vehicleNumber: String = "",
    @Expose @SerializedName("user_image_url") var userImageUrl: String = "",
    @Expose @SerializedName("user_image_thumb_url") var userImageThumbUrl: String = "") {

}