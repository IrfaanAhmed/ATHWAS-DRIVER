package com.app.ia.driver.base

import com.app.ia.driver.apiclient.ApiRequest
import com.app.ia.driver.apiclient.ApiService
import com.app.ia.driver.callback.GeneralCallback
import com.app.ia.driver.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

open class BaseRepository(private val myApi: ApiService, generalCallback: GeneralCallback) : ApiRequest(generalCallback) {

    val callback = generalCallback

    suspend fun userLogin(request: Map<String, String>): BaseResponse<LoginResponse> {
        return apiRequest { myApi.userLogin(request) }
    }

    suspend fun verifyOTP(request: Map<String, String>): BaseResponse<LoginResponse> {
        return apiRequest { myApi.verifyOTP(request) }
    }

    suspend fun resendOTP(request: Map<String, String>): BaseResponse<ResendOTPResponse> {
        return apiRequest { myApi.resendOTP(request) }
    }

    suspend fun resetPassword(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.resetPassword(request) }
    }

    suspend fun updateProfile(request: Map<String, String>): BaseResponse<UpdateProfileResponse> {
        return apiRequest { myApi.updateProfile(request) }
    }

    suspend fun updateProfile(partData: Map<String, RequestBody>, file: MultipartBody.Part): BaseResponse<UpdateProfileResponse> {
        return apiRequest { myApi.updateProfile(partData, file) }
    }

    suspend fun getProfile(): BaseResponse<ProfileResponse> {
        return apiRequest { myApi.getProfile() }
    }

    suspend fun changePassword(params: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.changePassword(params) }
    }

    suspend fun changeOnlineStatus(params: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.changeOnlineStatus(params) }
    }

    suspend fun getOrderList(request: Map<String, String>): BaseResponse<OrderListResponse> {
        return apiRequest { myApi.getOrderList(request) }
    }

    suspend fun getOrderStats(): BaseResponse<OrderStatsResponse> {
        return apiRequest { myApi.getOrderStats() }
    }

    suspend fun getNotification(request: Map<String, String>): BaseResponse<NotificationResponse> {
        return apiRequest { myApi.getNotification(request) }
    }

    suspend fun deleteNotification(notification_id: String): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.deleteNotification(notification_id) }
    }

    suspend fun getContentData(request: String): BaseResponse<ContentDataResponse> {
        return apiRequest { myApi.getContentData(request) }
    }

    suspend fun deleteAllNotification(): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.deleteAllNotification() }
    }

    suspend fun orderDetail(category_id: String): BaseNewResponse<OrderDetailResponse> {
        return apiRequest { myApi.orderDetail(category_id) }
    }

    suspend fun updateOrderStatus(category_id: String, params: Map<String, String>): BaseResponse<OrderDetailResponse> {
        return apiRequest { myApi.updateOrderStatus(category_id, params) }
    }

    suspend fun updateLocation(params: Map<String, String>): BaseResponse<LocationUpdateResponse> {
        return apiRequest { myApi.updateLocation(params) }
    }

    suspend fun getNotificationCount(): BaseResponse<NotificationCountResponse> {
        return apiRequest { myApi.getNotificationCount() }
    }

}