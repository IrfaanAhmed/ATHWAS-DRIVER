package com.app.ia.driver.apiclient

import com.app.ia.driver.apiclient.Api
import com.app.ia.driver.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @POST(Api.LOGIN_URL)
    @FormUrlEncoded
    suspend fun userLogin(@FieldMap params: Map<String, String>): Response<BaseResponse<LoginResponse>>

    @POST(Api.VERIFY_OTP_URL)
    @FormUrlEncoded
    suspend fun verifyOTP(@FieldMap params: Map<String, String>): Response<BaseResponse<LoginResponse>>

    @POST(Api.RESEND_OTP_URL)
    @FormUrlEncoded
    suspend fun resendOTP(@FieldMap params: Map<String, String>): Response<BaseResponse<ResendOTPResponse>>

    @POST(Api.RESET_PASSWORD_URL)
    @FormUrlEncoded
    suspend fun resetPassword(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @POST(Api.UPDATE_PROFILE_URL)
    @FormUrlEncoded
    suspend fun updateProfile(@FieldMap params: Map<String, String>): Response<BaseResponse<UpdateProfileResponse>>

    @POST(Api.UPDATE_PROFILE_URL)
    @Multipart
    suspend fun updateProfile(@PartMap() partData: Map<String, @JvmSuppressWildcards RequestBody>,
                              @Part() file: MultipartBody.Part): Response<BaseResponse<UpdateProfileResponse>>

    @GET(Api.GET_PROFILE_URL)
    suspend fun getProfile(): Response<BaseResponse<ProfileResponse>>

    @POST(Api.CHANGE_PASSWORD_URL)
    @FormUrlEncoded
    suspend fun changePassword(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @PUT(Api.CHANGE_ONLINE_URL)
    @FormUrlEncoded
    suspend fun changeOnlineStatus(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @GET(Api.GET_ORDER_LIST_URL)
    suspend fun getOrderList(@QueryMap params: Map<String, String>): Response<BaseResponse<OrderListResponse>>

    @GET(Api.GET_ORDER_STATS)
    suspend fun getOrderStats(): Response<BaseResponse<OrderStatsResponse>>

    @GET(Api.GET_NOTIFICATION_URL)
    suspend fun getNotification(@QueryMap params: Map<String, String>): Response<BaseResponse<NotificationResponse>>

    @DELETE(Api.GET_NOTIFICATION_URL)
    suspend fun deleteAllNotification(): Response<BaseResponse<NoDataResponse>>

    @GET(Api.CONTENT_DATA_URL)
    suspend fun getContentData(@Path("content_key") content_key: String): Response<BaseResponse<ContentDataResponse>>

    @DELETE(Api.DELETE_NOTIFICATION_URL)
    suspend fun deleteNotification(@Path("notification_id") id: String): Response<BaseResponse<NoDataResponse>>

    @GET(Api.ORDER_DETAIL)
    suspend fun orderDetail(@Path("order_id") id: String): Response<BaseNewResponse<OrderDetailResponse>>

    @PUT(Api.UPDATE_ORDER_STATUS)
    @FormUrlEncoded
    suspend fun updateOrderStatus(@Path("order_id") id: String, @FieldMap params: Map<String, String>): Response<BaseResponse<OrderDetailResponse>>

    @PUT(Api.LOCATION_UPDATE_URL)
    @FormUrlEncoded
    suspend fun updateLocation(@FieldMap params: Map<String, String>): Response<BaseResponse<LocationUpdateResponse>>

    @GET(Api.GET_NOTIFICATION_COUNT)
    suspend fun getNotificationCount(): Response<BaseResponse<NotificationCountResponse>>
}