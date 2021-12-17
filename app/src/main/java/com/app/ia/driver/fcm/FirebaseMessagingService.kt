package com.app.ia.driver.fcm

import android.content.Intent
import androidx.core.app.TaskStackBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.utils.AppConstants
import com.app.ia.driver.utils.AppLogger
import com.app.ia.driver.enums.NotificationType
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import java.util.*

class FirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        AppLogger.d("From : ${remoteMessage.from}")

        AppLogger.d("Message Notification Body -------------------------------: ${remoteMessage.notification?.body}")
        AppLogger.d("Message Notification Data Body -------------------------------: ${remoteMessage.data}")

        val notificationCount = AppPreferencesHelper.getInstance().notificationCount
        AppPreferencesHelper.getInstance().notificationCount = (notificationCount + 1)

        val localBroadCast = LocalBroadcastManager.getInstance(this)
        val intent = Intent(AppConstants.ACTION_BROADCAST_UPDATE_PROFILE)
        intent.putExtra("refresh", true)
        localBroadCast.sendBroadcast(intent)

        val orderUpdateBroadCast = LocalBroadcastManager.getInstance(this)
        val orderUpdateIntent = Intent(AppConstants.ACTION_BROADCAST_ORDER_UPDATE)
        orderUpdateBroadCast.sendBroadcast(orderUpdateIntent)

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            AppLogger.d("Message Notification Body: ${it.body}")
        }

        remoteMessage.data.isNotEmpty().let {
            val redirectionId = if (remoteMessage.data["id"] != null) remoteMessage.data["id"] else ""
            val redirectionType = if (remoteMessage.data["custom_message_type"] != null) remoteMessage.data["custom_message_type"] else ""
            val title = if (remoteMessage.data["title"] != null) remoteMessage.data["title"] else ""
            val body = if (remoteMessage.data["body"] != null) remoteMessage.data["body"] else ""

            when (remoteMessage.data["custom_message_type"]) {
                NotificationType.NOTIFICATION_TYPE_ADMIN.notificationType -> redirectionNotification(title!!, body!!, redirectionId!!, redirectionType!!)
                NotificationType.NOTIFICATION_TYPE_ORDER_DETAIL.notificationType -> redirectionNotification(title!!, body!!, redirectionId!!, redirectionType!!)
                NotificationType.NOTIFICATION_TYPE_PRODUCT.notificationType -> redirectionNotification(title!!, body!!, redirectionId!!, redirectionType!!)
                NotificationType.NOTIFICATION_TYPE_ORDER_LIST.notificationType -> redirectionNotification(title!!, body!!, redirectionId!!, redirectionType!!)
                else -> {
                    onlyNotification(remoteMessage)
                }
            }
        }
        // Also, if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        AppLogger.d("Refreshed token: $token")
        AppPreferencesHelper.getInstance().deviceToken = token
    }
    // [END on_new_token]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage FCM message body received.
     */
    private fun redirectionNotification(remoteMessage: RemoteMessage) {

        try {

            val redirectionId = if (remoteMessage.data["id"] != null) remoteMessage.data["id"] else ""
            val redirection = remoteMessage.data["custom_message_type"]
            val notificationIntent = Intent(this, HomeActivity::class.java)

            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addParentStack(HomeActivity::class.java)
            stackBuilder.addNextIntent(notificationIntent)

            val bundlePayloads = ArrayList<NotificationHelper.BundlePayload>()
            bundlePayloads.add(NotificationHelper.BundlePayload(HomeActivity.KEY_REDIRECTION, redirection!!))
            bundlePayloads.add(NotificationHelper.BundlePayload(HomeActivity.KEY_REDIRECTION_ID, redirectionId!!))

            val notificationHelper = NotificationHelper(this)
            notificationHelper.createNotification(remoteMessage.notification?.title!!, remoteMessage.notification?.body!!, HomeActivity::class.java, bundlePayloads)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun redirectionNotification(title: String, message: String, redirectionId: String, redirectionType: String) {

        try {

            //val redirectionId = if (remoteMessage.data["id"] != null) remoteMessage.data["id"] else ""
            //val redirection = remoteMessage.data["custom_message_type"]
            val notificationIntent = Intent(this, HomeActivity::class.java)

            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addParentStack(HomeActivity::class.java)
            stackBuilder.addNextIntent(notificationIntent)

            val bundlePayloads = ArrayList<NotificationHelper.BundlePayload>()
            bundlePayloads.add(NotificationHelper.BundlePayload(HomeActivity.KEY_REDIRECTION, redirectionType!!))
            bundlePayloads.add(NotificationHelper.BundlePayload(HomeActivity.KEY_REDIRECTION_ID, redirectionId!!))

            val notificationHelper = NotificationHelper(this)
            notificationHelper.createNotification(title, message, HomeActivity::class.java, bundlePayloads)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun onlyNotification(remoteMessage: RemoteMessage) {

        try {

            val notificationIntent = Intent(this, HomeActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addParentStack(HomeActivity::class.java)
            stackBuilder.addNextIntent(notificationIntent)

            val notificationHelper = NotificationHelper(this)
            notificationHelper.createNotification(remoteMessage.notification?.title!!, remoteMessage.notification?.body!!, HomeActivity::class.java, null)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}