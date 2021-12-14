package com.app.ia.driver.apiclient

import android.content.Intent
import com.app.ia.driver.DriverApplication
import com.app.ia.driver.callback.GeneralCallback
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.ui.login.LoginActivity
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

@Suppress("BlockingMethodInNonBlockingContext")
abstract class ApiRequest(private val generalCallback: GeneralCallback) {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T {

        generalCallback.showProgress()
        val response = call.invoke()
        generalCallback.hideProgress()
        if (response.isSuccessful) {
            return response.body()!!
        } else {

            val message = StringBuilder()
            val error = response.errorBody()?.string()
            error?.let {
                try {
                    val errorMsg : String? = JSONObject(it).optString("message")
                    if(!errorMsg.isNullOrEmpty()) {
                        message.append(errorMsg)
                    } else {
                        message.append("Session Expired.")
                    }

                    if (response.code() == 401 || response.code() == 403) {
                        val iaDialog = DriverDialog(DriverApplication.getInstance().getCurrentActivity()!!, if(errorMsg.isNullOrEmpty()) "Session expired" else errorMsg, true)
                        iaDialog.setOnItemClickListener(object : DriverDialog.OnClickListener {
                            override fun onPositiveClick() {
                                AppPreferencesHelper.getInstance().clearAllPreferences()
                                val intent = Intent(DriverApplication.getInstance().applicationContext, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                DriverApplication.getInstance().applicationContext.startActivity(intent)
                            }

                            override fun onNegativeClick() {
                            }
                        })
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            throw ApiException(message.toString())
        }
    }
}