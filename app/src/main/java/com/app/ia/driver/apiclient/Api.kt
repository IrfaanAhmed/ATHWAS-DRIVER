package com.app.ia.driver.apiclient

import com.app.ia.driver.enums.ENVIRONMENT


object Api {

    var BASE_URL = getBaseUrl(ENVIRONMENT.DEVELOPMENT.ordinal)

    private const val COMMON_PATH = "user_service/driver/"

    const val LOGIN_URL = COMMON_PATH + "login"
    const val VERIFY_OTP_URL = COMMON_PATH + "verify_otp"
    const val RESEND_OTP_URL = COMMON_PATH + "resend_otp"
    const val RESET_PASSWORD_URL = COMMON_PATH + "update_forgot_password"

    const val GET_PROFILE_URL = COMMON_PATH + "profile/get_profile"
    const val UPDATE_PROFILE_URL = COMMON_PATH + "profile/update_profile"
    const val CHANGE_PASSWORD_URL = COMMON_PATH + "profile/change_password"
    const val CHANGE_ONLINE_URL = COMMON_PATH + "profile/change_online_status"

    const val GET_ORDER_LIST_URL = COMMON_PATH + "order"
    const val GET_ORDER_STATS = COMMON_PATH + "order/order_stats"

    const val GET_NOTIFICATION_URL = COMMON_PATH + "notifications"
    const val DELETE_NOTIFICATION_URL = COMMON_PATH + "notifications/{notification_id}"
    const val CONTENT_DATA_URL = COMMON_PATH + "contents/{content_key}"

    const val ORDER_DETAIL = COMMON_PATH + "order/order_detail/{order_id}" //Get
    const val UPDATE_ORDER_STATUS = COMMON_PATH + "order/update_order_status/{order_id}" //PUT
    const val LOCATION_UPDATE_URL = COMMON_PATH + "profile/update_location"
    const val GET_NOTIFICATION_COUNT = COMMON_PATH + "notifications/get_notification_count"

    private fun getBaseUrl(environmentType: Int): String {

        return when (environmentType) {

            ENVIRONMENT.LOCAL.ordinal -> {
                "http://192.168.1.177:3050/"
            }

            ENVIRONMENT.DEVELOPMENT.ordinal -> {
                "http://3.7.83.168:3060/"
            }

            ENVIRONMENT.TESTING.ordinal -> {
                "http://192.168.1.69:3050/"
            }

            ENVIRONMENT.LIVE.ordinal -> {
                "https://www.cleverksa.com/api/"
            }

            else -> {
                "https://www.cleverksa.com/api/"
            }
        }
    }
}