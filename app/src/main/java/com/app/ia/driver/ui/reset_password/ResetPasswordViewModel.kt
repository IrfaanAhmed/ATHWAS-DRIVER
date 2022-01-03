package com.app.ia.driver.ui.reset_password

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.ActivityForgotPasswordBinding
import com.app.ia.driver.databinding.ActivityResetPasswordBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.enums.Status
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.ui.login.LoginActivity
import com.app.ia.driver.utils.*
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import kotlinx.android.synthetic.main.common_header.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class ResetPasswordViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityResetPasswordBinding

    var countryCode = MutableLiveData<String>("")
    var mobileNumber = MutableLiveData<String>("")
    var otp = MutableLiveData<String>("")

    fun setVariable(mBinding: ActivityResetPasswordBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.reset_password))
        (mActivity as ResetPasswordActivity).appCompatTextView.setTextColor(
            ContextCompat.getColor(
                mActivity,
                R.color.black
            )
        )
    }

    fun setIntent(intent: Intent) {
        countryCode.value = intent.getStringExtra(AppConstants.EXTRA_COUNTRY_CODE)
        mobileNumber.value = intent.getStringExtra(AppConstants.EXTRA_MOBILE_NUMBER)
        otp.value = intent.getStringExtra(AppConstants.EXTRA_OTP)
    }

    /* fun onResetClick(){
         mActivity.startActivityWithFinish<LoginActivity> {
             flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
         }
     }*/

    fun onResetClick() {
        val newPassword = mBinding.edtTextNewPassword.text.toString()
        val confirmPassword = mBinding.edtTextConfirmPassword.text.toString()
        mBinding.cardEdtTextConfirmPassword.error = null
        mBinding.cardEdtTextConfirmPassword.isErrorEnabled = false
        mBinding.cardEdtTextNewPassword.error = null
        mBinding.cardEdtTextNewPassword.isErrorEnabled = false
        mBinding.pinviewError.visibility = View.GONE

        var hasOtp = false
        var hasNewPassword = false
        var hasConfirmPassword = false
        var hasMatchPassword = false
        /*if (mBinding.pinView.text!!.isEmpty() && newPassword.isEmpty() && confirmPassword.isEmpty()) {
            mBinding.pinviewError.text = mActivity.getString(R.string.please_enter_otp)
            mBinding.pinviewError.visibility = View.VISIBLE
            mBinding.cardEdtTextNewPassword.error =
                mActivity.getString(R.string.please_enter_new_password)
            mBinding.cardEdtTextConfirmPassword.error =
                mActivity.getString(R.string.enter_conform_password)
        } else {*/
            if (mBinding.pinView.text!!.isEmpty()) {
                mBinding.pinviewError.text = mActivity.getString(R.string.please_enter_otp)
                mBinding.pinviewError.visibility = View.VISIBLE
            } else if (mBinding.pinView.text!!.toString() != otp.value!!) {
                mBinding.pinviewError.text = mActivity.getString(R.string.please_enter_valid_otp)
                mBinding.pinviewError.visibility = View.VISIBLE
            } else {
                hasOtp = true
            }
            if (newPassword.isEmpty()) {
                mBinding.cardEdtTextNewPassword.error =
                    mActivity.getString(R.string.please_enter_new_password)
            } else if (newPassword.length < 6 || newPassword.length > 15) {
                mBinding.cardEdtTextNewPassword.error =
                    mActivity.getString(R.string.new_password_validation_msg)
            } else {
                hasNewPassword = true
            }


            if (confirmPassword.isEmpty()) {
                mBinding.cardEdtTextConfirmPassword.error =
                    mActivity.getString(R.string.enter_conform_password)
            } else if (confirmPassword.length < 6 || confirmPassword.length > 15) {
                mBinding.cardEdtTextConfirmPassword.error =
                    mActivity.getString(R.string.confirm_password_validation_msg)
            } else {
                hasConfirmPassword = true
            }


            if (confirmPassword != newPassword) {
                mBinding.cardEdtTextConfirmPassword.error =
                    mActivity.getString(R.string.confirm_new_matched_validation_msg)
            } else {
                hasMatchPassword = true
            }
            if (hasOtp && hasNewPassword && hasConfirmPassword && hasMatchPassword) {
                val requestParams = HashMap<String, String>()
                requestParams["country_code"] = "+${countryCode.value!!}"
                requestParams["phone"] = mobileNumber.value!!
                requestParams["otp_number"] = mBinding.pinView.text.toString()
                requestParams["new_password"] = newPassword
                setupObservers(requestParams)
            }
        //}
    }

    /*fun onResetClick() {
        val newPassword = mBinding.edtTextNewPassword.text.toString()
        val confirmPassword = mBinding.edtTextConfirmPassword.text.toString()
        mBinding.cardEdtTextConfirmPassword.error=null
        mBinding.cardEdtTextConfirmPassword.isErrorEnabled=false
        mBinding.cardEdtTextNewPassword.error=null
        mBinding.cardEdtTextNewPassword.isErrorEnabled=false
        mBinding.pinviewError.visibility= View.GONE

        if (mBinding.pinView.text!!.isEmpty()) {
//            DriverDialog(mActivity, mActivity.getString(R.string.please_enter_otp), true)
            mBinding.pinviewError.text=mActivity.getString(R.string.please_enter_otp)
            mBinding.pinviewError.visibility= View.VISIBLE

        } else if (mBinding.pinView.text!!.toString() != otp.value!!) {
//            DriverDialog(mActivity, mActivity.getString(R.string.please_enter_valid_otp), true)
            mBinding.pinviewError.text=mActivity.getString(R.string.please_enter_valid_otp)
            mBinding.pinviewError.visibility= View.VISIBLE
        } else if (newPassword.isEmpty()) {
//            DriverDialog(mActivity, mActivity.getString(R.string.please_enter_new_password), true)
            mBinding.cardEdtTextNewPassword.error=mActivity.getString(R.string.please_enter_new_password)
        } else if (newPassword.length < 6 || newPassword.length > 15) {
//            DriverDialog(mActivity, mActivity.getString(R.string.new_password_validation_msg), true)
            mBinding.cardEdtTextNewPassword.error=mActivity.getString(R.string.new_password_validation_msg)
        } else if (confirmPassword.isEmpty()) {
//            DriverDialog(mActivity, mActivity.getString(R.string.enter_conform_password), true)
            mBinding.cardEdtTextConfirmPassword.error=mActivity.getString(R.string.enter_conform_password)
        } else if (confirmPassword.length < 6 || confirmPassword.length > 15) {
//            DriverDialog(mActivity, mActivity.getString(R.string.confirm_password_validation_msg), true)
            mBinding.cardEdtTextConfirmPassword.error=mActivity.getString(R.string.confirm_password_validation_msg)
        } else if (confirmPassword != newPassword) {
//            DriverDialog(mActivity, mActivity.getString(R.string.confirm_new_matched_validation_msg), true)
            mBinding.cardEdtTextConfirmPassword.error=mActivity.getString(R.string.confirm_new_matched_validation_msg)
        } else {
            val requestParams = HashMap<String, String>()
            requestParams["country_code"] = "+${countryCode.value!!}"
            requestParams["phone"] = mobileNumber.value!!
            requestParams["otp_number"] = mBinding.pinView.text.toString()
            requestParams["new_password"] = newPassword
            setupObservers(requestParams)
        }
    }*/

    private fun resetPassword(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.resetPassword(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun setupObservers(requestParams: HashMap<String, String>) {
        resetPassword(requestParams).observe(mBinding.lifecycleOwner!!, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            //if (users.status == "success") {
                            mActivity.toast(users.message)
                            mActivity.startActivityWithFinish<LoginActivity> {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            /*} else {
                                DriverDialog(mActivity, users.message, true)
                            }*/
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (CommonUtils.isJSONValid(it.message)) {
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