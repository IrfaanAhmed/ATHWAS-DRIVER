package com.app.ia.driver.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.ActivityLoginBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.enums.Status
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.ui.forgot_password.ForgotPasswordActivity
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.ui.otp.OTPActivity
import com.app.ia.driver.utils.*
import com.app.ia.driver.utils.AppConstants.EXTRA_COUNTRY_CODE
import com.app.ia.driver.utils.CommonUtils.isJSONValid
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.common_header.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class LoginViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var androidId: String

    @SuppressLint("HardwareIds")
    fun setVariable(mBinding: ActivityLoginBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.login))
        (mActivity as LoginActivity).appCompatTextView.setTextColor(
            ContextCompat.getColor(
                mActivity,
                R.color.black
            )
        )
        storeDeviceToken()
        androidId = Settings.Secure.getString(mActivity.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun onHomeRedirectClick() {
        mActivity.startActivity<HomeActivity>()
    }

    fun onForgotPasswordClick() {
        mActivity.startActivity<ForgotPasswordActivity>()
    }

    private fun storeDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                AppLogger.w("Fetching FCM registration token failed" + task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val fcmToken = task.result
            AppLogger.d("device token : $fcmToken")
            AppPreferencesHelper.getInstance().deviceToken = fcmToken
        })
    }

    fun onLoginClick() {
        (baseRepository.callback).hideKeyboard()
        val mobileNumber = mBinding.edtTextMobileNumber.text.toString()
        val password = mBinding.edtTextPassword.text.toString()
        var donePhone = false

        mBinding.cardViewMobile.error = null
        mBinding.cardViewMobile.isErrorEnabled = false
        mBinding.cardViewPassword.error = null
        mBinding.cardViewPassword.isErrorEnabled = false

        if (mobileNumber.isEmpty() && password.isEmpty()) {
            mBinding.cardViewMobile.error =
                mActivity.getString(R.string.please_enter_email_or_mobile_number)
            mBinding.cardViewPassword.error = mActivity.getString(R.string.enter_password)

        } else {
            if (isValidPhoneNumber(mobileNumber)) {
                if (mobileNumber.length < 7 || mobileNumber.length > 15) {
                    mBinding.cardViewMobile.error =
                        mActivity.getString(R.string.mobile_number_validation_msg)
                } else {
                    donePhone = true
                }
            } else {
                if (!CommonUtils.isEmailValid(mobileNumber)) {
                    mBinding.cardViewMobile.error =
                        mActivity.getString(R.string.enter_valid_email_address)
                } else {
                    donePhone = true
                }
            }

            if (password.isEmpty()) {
                mBinding.cardViewPassword.error = mActivity.getString(R.string.enter_password)
            } else if (password.length < 6 || password.length > 15) {

                mBinding.cardViewPassword.error =
                    mActivity.getString(R.string.password_validation_msg)
            } else {
                if (donePhone) {
                    submitLoginCredential(mobileNumber, password)
                }
            }
        }
    }

    /*  fun onLoginClick() {
        (baseRepository.callback).hideKeyboard()
        val mobileNumber = mBinding.edtTextMobileNumber.text.toString()
        val password = mBinding.edtTextPassword.text.toString()

        mBinding.cardViewMobile.error=null
        mBinding.cardViewMobile.isErrorEnabled=false
        mBinding.cardViewPassword.error=null
        mBinding.cardViewPassword.isErrorEnabled=false

        if (mobileNumber.isEmpty()) {
//            DriverDialog(mActivity, mActivity.getString(R.string.please_enter_email_or_mobile_number), true)
            mBinding.cardViewMobile.error=mActivity.getString(R.string.please_enter_email_or_mobile_number)

        } else if (isValidPhoneNumber(mobileNumber)) {
            if (mobileNumber.length < 7 || mobileNumber.length > 15) {
//                DriverDialog(mActivity, mActivity.getString(R.string.mobile_number_validation_msg), true)
                mBinding.cardViewMobile.error=mActivity.getString(R.string.mobile_number_validation_msg)
            } else if (password.isEmpty()) {
//                DriverDialog(mActivity, mActivity.getString(R.string.enter_password), true)
                mBinding.cardViewPassword.error=mActivity.getString(R.string.enter_password)
            } else if (password.length < 6 || password.length > 15) {
//                DriverDialog(mActivity, mActivity.getString(R.string.password_validation_msg), true)
                mBinding.cardViewPassword.error=mActivity.getString(R.string.password_validation_msg)
            } else {
                submitLoginCredential(mobileNumber, password)
            }
        } else {
            if (!CommonUtils.isEmailValid(mobileNumber)) {
//                DriverDialog(mActivity, mActivity.getString(R.string.enter_valid_email_address), true)
                mBinding.cardViewMobile.error=mActivity.getString(R.string.enter_valid_email_address)
            } else if (password.isEmpty()) {
//                DriverDialog(mActivity, mActivity.getString(R.string.enter_password), true)
                mBinding.cardViewPassword.error=mActivity.getString(R.string.enter_password)
            } else {
                submitLoginCredential(mobileNumber, password)
            }
        }
    }*/

    private fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        return if (!TextUtils.isEmpty(phoneNumber)) {
            Patterns.PHONE.matcher(phoneNumber).matches()
        } else false
    }


    private fun submitLoginCredential(mobileNumber: String, password: String) {

        val requestParams = HashMap<String, String>()
        requestParams["country_code"] = "+91"
        requestParams["phone"] = mobileNumber
        requestParams["password"] = password
        requestParams["login_through"] = "password"
        requestParams["device_token"] = AppPreferencesHelper.getInstance().deviceToken
        requestParams["latitude"] = AppPreferencesHelper.getInstance().mCurrentLat
        requestParams["longitude"] = AppPreferencesHelper.getInstance().mCurrentLng
        requestParams["device_token"] = AppPreferencesHelper.getInstance().deviceToken
        requestParams["device_type"] = "1"
        requestParams["device_id"] = androidId
        setupObservers(requestParams)
    }

    private fun getUsers(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.userLogin(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun setupObservers(requestParams: HashMap<String, String>) {
        getUsers(requestParams).observe(mBinding.lifecycleOwner!!, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            if (users.data?.isUserVerified == 0) {
                                mActivity.toast(users.message)
                                submitForgotPassword(requestParams.getValue("phone"))
                            } else {
                                AppPreferencesHelper.getInstance().userData = users.data!!
                                mActivity.startActivityWithFinish<HomeActivity> {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            }
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (isJSONValid(it.message)) {
                            val obj = JSONObject(it.message!!)
                            if (obj.getInt("code") == 400) {
                                DriverDialog(mActivity, obj.getString("message"), true)
                            } else {
                                Toast.makeText(
                                    mActivity,
                                    obj.getString("message"),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private fun submitForgotPassword(mobileNumber: String) {
        val requestParams = HashMap<String, String>()
        requestParams["country_code"] = "+91"
        requestParams["phone"] = mobileNumber
        requestParams["otp_for"] = "forgot_password"
        setupObserversSendOtp(requestParams)
    }

    private fun resendOTP(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.resendOTP(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun setupObserversSendOtp(requestParams: HashMap<String, String>) {
        resendOTP(requestParams).observe(mBinding.lifecycleOwner!!, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            //if (users.status == "success") {
                            mActivity.startActivity<OTPActivity> {
                                putExtra(EXTRA_COUNTRY_CODE, users.data?.countryCode)
                                putExtra(AppConstants.EXTRA_MOBILE_NUMBER, users.data?.phone)
                                putExtra(AppConstants.EXTRA_OTP, users.data?.otpNumber)
                            }
                            /* } else {
                                 DriverDialog(mActivity, users.message, true)
                             }*/
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (isJSONValid(it.message)) {
                            val obj = JSONObject(it.message!!)
                            if (obj.getInt("code") == 400) {
                                DriverDialog(mActivity, obj.getString("message"), true)
                            } else {
                                Toast.makeText(
                                    mActivity,
                                    obj.getString("message"),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}