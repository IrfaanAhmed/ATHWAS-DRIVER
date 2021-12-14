package com.app.ia.driver.local

import com.app.ia.driver.model.LoginResponse


interface PreferencesHelper {

    var address: String

    var wallet: String

    var onlineStatus: Int

    var notificationCount: Int

    var isUserVerified: Int

    var isEmailVerified: Int

    var showNotifications: Int

    var authToken: String

    var isAvailable: Int

    var isActive: Int

    var deviceType: Int

    var isDeleted: Int

    var deviceToken: String

    var deviceId: String

    var userImage: String

    var userID: String

    var language: String

    var userName: String

    var userRole: String

    var countryCode: String

    var phone: String

    var email: String

    var userImageUrl: String

    var userImageThumbUrl: String
    var mCurrentLat: String
    var mCurrentLng: String

    var userData: LoginResponse

}