package com.app.ia.driver.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NotificationCountResponse(

    @Expose
    @SerializedName("notification_count")
    val notificationCount: Int)