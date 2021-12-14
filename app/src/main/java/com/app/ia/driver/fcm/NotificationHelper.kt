package com.app.ia.driver.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.app.ia.driver.R
import com.app.ia.driver.ui.home.HomeActivity

class NotificationHelper(private val mContext: Context) {

    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null

    /**
     * Create and push the notification
     */
    fun createNotification(title: String, message: String, aClass: Class<*>, bundlePayloadList: List<BundlePayload>?) {

        /**Creates an explicit intent for an Activity in your app */
        val requestID = System.currentTimeMillis().toInt()
        val resultIntent = Intent(mContext, aClass)

        if (bundlePayloadList != null && bundlePayloadList.isNotEmpty()) {
            for (i in bundlePayloadList.indices) {
                resultIntent.putExtra(bundlePayloadList[i].bundleKey, bundlePayloadList[i].bundleValue)
            }
        }
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val stackBuilder = TaskStackBuilder.create(mContext)
        stackBuilder.addParentStack(HomeActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            requestID /* Request code */, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        //************************************

        mBuilder = NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(getNotificationIcon())
            .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, R.mipmap.ic_launcher_round))
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(mContext, R.color.colorAccent))
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(resultPendingIntent)

        mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mBuilder!!.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager!!.createNotificationChannel(notificationChannel)
        }

        val id = System.currentTimeMillis().toInt()

        mNotificationManager!!.notify(id /*Request Code*/, mBuilder!!.build())
    }


    private fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.drawable.ic_notification_icon else R.mipmap.ic_launcher_round
    }

    class BundlePayload(bundleKey: String, bundleValue: String) {

        var bundleKey: String? = bundleKey
        var bundleValue: String? = bundleValue
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "28957"
        const val NOTIFICATION_CHANNEL_NAME = "Tivo eWallet"
    }
}
