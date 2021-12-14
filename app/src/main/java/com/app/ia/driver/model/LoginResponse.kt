package com.app.ia.driver.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @Expose @SerializedName("address") var address: String = "",
    @Expose @SerializedName("geoLocation") var geolocation: Geolocation? = null,
    @Expose @SerializedName("wallet") var wallet: String = "",
    @Expose @SerializedName("online_status") var onlineStatus: Int = 0,
    @Expose @SerializedName("is_user_verified") var isUserVerified: Int = 0,
    @Expose @SerializedName("is_email_verified") var isEmailVerified: Int = 0,
    @Expose @SerializedName("show_notifications") var showNotifications: Int = 0,
    @Expose @SerializedName("auth_token") var authToken: String = "",
    @Expose @SerializedName("is_available") var isAvailable: Int = 0,
    @Expose @SerializedName("is_active") var isActive: Int = 0,
    @Expose @SerializedName("device_type") var deviceType: Int = 0,
    @Expose @SerializedName("is_deleted") var isDeleted: Int = 0,
    @Expose @SerializedName("device_token") var deviceToken: String = "",
    @Expose @SerializedName("device_id") var deviceId: String = "",
    @Expose @SerializedName("user_image") var userImage: String = "",
    @Expose @SerializedName("_id") var Id: String = "",
    @Expose @SerializedName("username") var userName: String = "",
    @Expose @SerializedName("user_role") var userRole: String = "",
    @Expose @SerializedName("country_code") var countryCode: String = "",
    @Expose @SerializedName("phone") var phone: String = "",
    @Expose @SerializedName("email") var email: String = "",
    @Expose @SerializedName("password") var password: String = "",
    @Expose @SerializedName("last_login_time") var lastLoginTime: String = "",
    @Expose @SerializedName("createdAt") var createdAt: String = "",
    @Expose @SerializedName("updatedAt") var updatedAt: String = "",
    @Expose @SerializedName("__v") var V: Int = 0,
    @Expose @SerializedName("user_image_url") var userImageUrl: String = "",
    @Expose @SerializedName("user_image_thumb_url") var userImageThumbUrl: String = "",
    @Expose @SerializedName("id") var _id: String = "",
    @Expose @SerializedName("otp_number") var otpNumber: String = "") {

    data class Geolocation(@Expose @SerializedName("type") var type: String, @Expose @SerializedName("coordinates") var coordinates: List<String>, @Expose @SerializedName("_id") var Id: String)
}