package com.app.ia.driver.local

import android.content.Context
import android.content.SharedPreferences
import com.app.ia.driver.DriverApplication
import com.app.ia.driver.model.LoginResponse
import com.app.ia.driver.utils.AppConstants


class AppPreferencesHelper internal constructor() : PreferencesHelper {

    private val sharedPreferences: SharedPreferences = DriverApplication.getInstance().getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE)


    override var language: String
        get() = sharedPreferences.getString(PREF_KEY_LANGUAGE, "en")!!
        set(language) = sharedPreferences.edit().putString(PREF_KEY_LANGUAGE, language).apply()


    override var address: String
        get() = sharedPreferences.getString(PREF_KEY_ADDRESS, AppConstants.EMPTY_STRING)!!
        set(value) = sharedPreferences.edit().putString(PREF_KEY_ADDRESS, value).apply()

    override var onlineStatus: Int
        get() = sharedPreferences.getInt(PREF_KEY_ONLINE_STATUS, 0)
        set(value) = sharedPreferences.edit().putInt(PREF_KEY_ONLINE_STATUS, value).apply()

    override var isUserVerified: Int
        get() = sharedPreferences.getInt(PREF_KEY_IS_USER_VERIFIED, 0)
        set(value) = sharedPreferences.edit().putInt(PREF_KEY_IS_USER_VERIFIED, value).apply()

    override var isEmailVerified: Int
        get() = sharedPreferences.getInt(PREF_KEY_IS_EMAIL_VERIFIED, 0)
        set(value) = sharedPreferences.edit().putInt(PREF_KEY_IS_EMAIL_VERIFIED, value).apply()

    override var showNotifications: Int
        get() = sharedPreferences.getInt(PREF_KEY_SHOW_NOTIFICATION, 0)
        set(value) = sharedPreferences.edit().putInt(PREF_KEY_SHOW_NOTIFICATION, value).apply()

    override var authToken: String
        get() = sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, AppConstants.EMPTY_STRING)!!
        set(accessToken) = sharedPreferences.edit().putString(PREF_KEY_AUTH_TOKEN, accessToken).apply()

    override var isAvailable: Int
        get() = sharedPreferences.getInt(PREF_KEY_IS_AVAILABLE, 0)
        set(value) = sharedPreferences.edit().putInt(PREF_KEY_IS_AVAILABLE, value).apply()

    override var isActive: Int
        get() = sharedPreferences.getInt(PREF_KEY_IS_ACTIVE, 0)
        set(value) = sharedPreferences.edit().putInt(PREF_KEY_IS_ACTIVE, value).apply()

    override var deviceType: Int
        get() = sharedPreferences.getInt(PREF_KEY_DEVICE_TYPE, 0)
        set(value) = sharedPreferences.edit().putInt(PREF_KEY_DEVICE_TYPE, value).apply()

    override var isDeleted: Int
        get() = sharedPreferences.getInt(PREF_KEY_IS_DELETED, 0)
        set(value) = sharedPreferences.edit().putInt(PREF_KEY_IS_DELETED, value).apply()

    override var deviceToken: String
        get() = sharedPreferences.getString(PREF_KEY_DEVICE_TOKEN, AppConstants.EMPTY_STRING)!!
        set(deviceToken) = sharedPreferences.edit().putString(PREF_KEY_DEVICE_TOKEN, deviceToken).apply()

    override var deviceId: String
        get() = sharedPreferences.getString(PREF_KEY_DEVICE_ID, AppConstants.EMPTY_STRING)!!
        set(deviceToken) = sharedPreferences.edit().putString(PREF_KEY_DEVICE_ID, deviceToken).apply()

    override var notificationCount: Int
        get() = sharedPreferences.getInt(PREF_KEY_NOTIFICATION_COUNT, 0)
        set(value) = sharedPreferences.edit().putInt(PREF_KEY_NOTIFICATION_COUNT, value).apply()

    override var email: String
        get() = sharedPreferences.getString(PREF_KEY_USER_EMAIL, AppConstants.EMPTY_STRING)!!
        set(email) = sharedPreferences.edit().putString(PREF_KEY_USER_EMAIL, email).apply()

    override var userID: String
        get() = sharedPreferences.getString(PREF_KEY_USER_ID, AppConstants.EMPTY_STRING)!!
        set(userId) = sharedPreferences.edit().putString(PREF_KEY_USER_ID, userId).apply()

    override var userName: String
        get() = sharedPreferences.getString(PREF_KEY_USER_NAME, AppConstants.EMPTY_STRING)!!
        set(userName) = sharedPreferences.edit().putString(PREF_KEY_USER_NAME, userName).apply()

    override var userImage: String
        get() = sharedPreferences.getString(PREF_KEY_USER_IMAGE_URL, AppConstants.EMPTY_STRING)!!
        set(profilePicUrl) = sharedPreferences.edit().putString(PREF_KEY_USER_IMAGE_URL, profilePicUrl).apply()

    override var userImageUrl: String
        get() = sharedPreferences.getString(PREF_KEY_USER_IMAGE_URL, AppConstants.EMPTY_STRING)!!
        set(value) = sharedPreferences.edit().putString(PREF_KEY_USER_IMAGE_URL, value).apply()

    override var userImageThumbUrl: String
        get() = sharedPreferences.getString(PREF_KEY_USER_IMAGE_THUMB_URL, AppConstants.EMPTY_STRING)!!
        set(value) = sharedPreferences.edit().putString(PREF_KEY_USER_IMAGE_THUMB_URL, value).apply()

    override var wallet: String
        get() = sharedPreferences.getString(PREF_KEY_WALLET, "0")!!
        set(availableWalletBalance) = sharedPreferences.edit().putString(PREF_KEY_WALLET, availableWalletBalance).apply()

    override var phone: String
        get() = sharedPreferences.getString(PREF_KEY_USER_MOBILE, AppConstants.EMPTY_STRING)!!
        set(value) = sharedPreferences.edit().putString(PREF_KEY_USER_MOBILE, value).apply()

    override var userRole: String
        get() = sharedPreferences.getString(PREF_KEY_USER_ROLE, AppConstants.EMPTY_STRING)!!
        set(value) = sharedPreferences.edit().putString(PREF_KEY_USER_ROLE, value).apply()

    override var countryCode: String
        get() = sharedPreferences.getString(PREF_KEY_USER_COUNTRY_CODE, AppConstants.EMPTY_STRING)!!
        set(value) {
            sharedPreferences.edit().putString(PREF_KEY_USER_COUNTRY_CODE, value).apply()
        }

    override var mCurrentLat: String
        get() = sharedPreferences.getString(PREF_KEY_LATITUDE, AppConstants.EMPTY_STRING)!!
        set(value) {
            sharedPreferences.edit().putString(PREF_KEY_LATITUDE, value).apply()
        }

    override var mCurrentLng: String
        get() = sharedPreferences.getString(PREF_KEY_LONGITUDE, AppConstants.EMPTY_STRING)!!
        set(value) {
            sharedPreferences.edit().putString(PREF_KEY_LONGITUDE, value).apply()
        }

    override var userData: LoginResponse
        get() = LoginResponse(address, null, wallet, onlineStatus, isUserVerified, isEmailVerified,
            showNotifications, authToken, isAvailable, isActive, deviceType, isDeleted,
            deviceToken, deviceId, userImage, userID, userName, userRole, countryCode,
            phone, email, "", "", "", "", 0,
            userImageUrl, userImageThumbUrl, userID, "")
        set(value) {
            address = value.address
            authToken = value.authToken
            phone = value.phone
            userImage = value.userImageUrl
            wallet = value.wallet
            onlineStatus = value.onlineStatus
            isUserVerified = value.isUserVerified
            isEmailVerified = value.isEmailVerified
            showNotifications = value.showNotifications
            isAvailable = value.isAvailable
            isActive = value.isActive
            deviceType = value.deviceType
            isDeleted = value.isDeleted
            deviceToken = value.deviceToken
            deviceId = value.deviceId
            userName = value.userName
            email = value.email
            userRole = value.userRole
            userID = value.Id
            countryCode = value.countryCode
            userImageUrl = value.userImageUrl
            userImageThumbUrl = value.userImageThumbUrl
        }

    fun clearAllPreferences() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {

        //user credential
        private const val PREF_NAME = "com.app.tivo.preference"
        private const val PREF_KEY_AUTH_TOKEN = "AUTH_TOKEN"
        private const val PREF_KEY_DEVICE_TOKEN = "DEVICE_TOKEN"
        private const val PREF_KEY_DEVICE_ID = "DEVICE_ID"
        private const val PREF_KEY_LANGUAGE = "LANGUAGE"
        private const val PREF_KEY_USER_EMAIL = "USER_EMAIL"
        private const val PREF_KEY_USER_ID = "USER_ID"
        private const val PREF_KEY_USER_NAME = "USER_NAME"
        private const val PREF_KEY_USER_IMAGE_URL = "USER_PROFILE_IMAGE_URL"
        private const val PREF_KEY_USER_IMAGE_THUMB_URL = "USER_PROFILE_IMAGE_THUMB_URL"
        private const val PREF_KEY_ADDRESS = "ADDRESS"
        private const val PREF_KEY_ONLINE_STATUS = "ONLINE_STATUS"
        private const val PREF_KEY_IS_USER_VERIFIED = "IS_USER_VERIFIED"
        private const val PREF_KEY_IS_EMAIL_VERIFIED = "IS_EMAIL_VERIFIED"
        private const val PREF_KEY_USER_COUNTRY_CODE = "USER_COUNTRY_CODE"
        private const val PREF_KEY_USER_MOBILE = "USER_MOBILE"
        private const val PREF_KEY_WALLET = "WALLET"
        private const val PREF_KEY_USER_ROLE = "USER_ROLE"
        private const val PREF_KEY_DEVICE_TYPE = "DEVICE_TYPE"
        private const val PREF_KEY_SHOW_NOTIFICATION = "SHOW_NOTIFICATION"
        private const val PREF_KEY_IS_AVAILABLE = "IS_AVAILABLE"
        private const val PREF_KEY_IS_ACTIVE = "IS_ACTIVE"
        private const val PREF_KEY_IS_DELETED = "IS_DELETED"
        private const val PREF_KEY_LATITUDE = "LATITUDE"
        private const val PREF_KEY_LONGITUDE = "LONGITUDE"
        private const val PREF_KEY_NOTIFICATION_COUNT = "NOTIFICATION_COUNT"

        private var appPreferencesHelper: AppPreferencesHelper? = null

        fun getInstance(): AppPreferencesHelper {
            if (appPreferencesHelper == null) {
                appPreferencesHelper = AppPreferencesHelper()
                return appPreferencesHelper!!
            }
            return appPreferencesHelper!!
        }
    }
}