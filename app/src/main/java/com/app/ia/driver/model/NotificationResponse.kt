package com.app.ia.driver.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class NotificationResponse(

    @Expose
    @SerializedName("docs")
    var docs: MutableList<Docs>,

    @Expose
    @SerializedName("totalDocs")
    var totalDocs: Int,

    @Expose
    @SerializedName("limit")
    var limit: Int,

    @Expose
    @SerializedName("totalPages")
    var totalPages: Int,

    @Expose
    @SerializedName("page")
    var page: Int,

    @Expose
    @SerializedName("pagingCounter")
    var pagingCounter: Int,

    @Expose
    @SerializedName("hasPrevPage")
    var hasPrevPage: Boolean,

    @Expose
    @SerializedName("hasNextPage")
    var hasNextPage: Boolean) {

    data class Docs(
        @Expose
        @SerializedName("_id")
        val Id: String = "",

        @Expose
        @SerializedName("read_status")
        val readStatus: Int = 0,

        @Expose
        @SerializedName("notification_type")
        val notificationType: Int = 1,

        @Expose
        @SerializedName("createdAt")
        private val createdAt: String = "2020-08-10T12:35:05.485Z",

        @Expose
        @SerializedName("title")
        val title: String = "Title",

        @Expose
        @SerializedName("message")
        val message: String = "message") {

        fun getCreatedAt(): String {
            val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val outputDate: String
            outputDate = try {
                val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value: Date = formatter.parse(createdAt)!!
                val timeZone = TimeZone.getDefault()
                val dateFormatter = SimpleDateFormat("dd MMMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
                dateFormatter.timeZone = timeZone
                dateFormatter.format(value)
            } catch (e: Exception) {
                createdAt
            }
            return outputDate
        }

    }
}
