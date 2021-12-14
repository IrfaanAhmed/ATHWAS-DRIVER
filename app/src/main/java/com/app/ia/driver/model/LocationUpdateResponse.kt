package com.app.ia.driver.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LocationUpdateResponse(

    @Expose
    @SerializedName("online_status")
    var onlineStatus: Int,
    @Expose
    @SerializedName("is_user_verified")
    var isUserVerified: Int,
    @Expose
    @SerializedName("is_email_verified")
    var isEmailVerified: Int,
    @Expose
    @SerializedName("is_phone_verified")
    var isPhoneVerified: Int,
    @Expose
    @SerializedName("auth_token")
    val authToken: String,
    @Expose
    @SerializedName("_id")
    val _Id: String,
    @Expose
    @SerializedName("username")
    val username: String,
    @Expose
    @SerializedName("user_role")
    var userRole: Int,
    @Expose
    @SerializedName("email")
    val email: String,
    @Expose
    @SerializedName("country_code")
    val countryCode: String,
    @Expose
    @SerializedName("phone")
    val phone: String,
    @Expose
    @SerializedName("user_image")
    val userImage: String,
    @Expose
    @SerializedName("delivery_address")
    var deliveryAddress: List<String>,
    @Expose
    @SerializedName("user_image_url")
    val userImageUrl: String,
    @Expose
    @SerializedName("user_image_thumb_url")
    val userImageThumbUrl: String,
    @Expose
    @SerializedName("id")
    val id: String
)