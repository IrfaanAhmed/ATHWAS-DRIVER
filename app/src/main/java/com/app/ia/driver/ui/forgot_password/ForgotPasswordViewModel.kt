package com.app.ia.driver.ui.forgot_password

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.ActivityForgotPasswordBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.enums.Status
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.ui.otp.OTPActivity
import com.app.ia.driver.ui.reset_password.ResetPasswordActivity
import com.app.ia.driver.utils.*
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import kotlinx.android.synthetic.main.common_header.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class ForgotPasswordViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityForgotPasswordBinding

    fun setVariable(mBinding: ActivityForgotPasswordBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.forgot_password_title))
        (mActivity as ForgotPasswordActivity).appCompatTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.black))
    }


    fun onSendClick(){
       // mActivity.startActivity<ResetPasswordActivity>()
        (baseRepository.callback).hideKeyboard()
        val mobileNumber = mBinding.edtTextMobileNumber.text.toString()

        if(mobileNumber.isEmpty()){
            DriverDialog(mActivity, mActivity.getString(R.string.please_enter_email_or_mobile_number), true)
        }else if (isValidPhoneNumber(mobileNumber)) {
            if (mobileNumber.length < 7 || mobileNumber.length > 15) {
                DriverDialog(mActivity, mActivity.getString(R.string.mobile_number_validation_msg), true)
            }else{
                if (validateNumber("91", mobileNumber)) {
                    submitForgotPassword(mobileNumber)
                } else {
                    DriverDialog(mActivity, mActivity.getString(R.string.enter_valid_mobile_no), true)
                }
            }

        } else {
            if(!CommonUtils.isEmailValid(mobileNumber)){
                DriverDialog(mActivity, mActivity.getString(R.string.enter_valid_email_address), true)
            }else{
                submitForgotPassword(mobileNumber)
            }


        }
    }

    private fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        return if (!TextUtils.isEmpty(phoneNumber)) {
            Patterns.PHONE.matcher(phoneNumber).matches()
        } else false
    }

    private fun validateNumber(countryCode: String, phNumber: String): Boolean {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode))
        var phoneNumber: Phonenumber.PhoneNumber? = null
        try {
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode)
        } catch (e: NumberParseException) {
            e.printStackTrace()
        }
        return phoneNumberUtil.isValidNumber(phoneNumber)
    }

    private fun submitForgotPassword(mobileNumber: String){
        val requestParams = HashMap<String, String>()
        requestParams["country_code"] = "+91"
        requestParams["phone"] = mobileNumber
        requestParams["otp_for"] = "forgot_password"
        setupObservers(requestParams)
    }

    private fun resendOTP(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.resendOTP(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun setupObservers(requestParams: HashMap<String, String>) {
        resendOTP(requestParams).observe(mBinding.lifecycleOwner!!, Observer{
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            //if (users.status == "success") {

                                mActivity.startActivity<ResetPasswordActivity> {
                                    putExtra(AppConstants.EXTRA_COUNTRY_CODE, users.data?.countryCode)
                                    putExtra(AppConstants.EXTRA_MOBILE_NUMBER, mBinding.edtTextMobileNumber.text.toString())
                                    putExtra(AppConstants.EXTRA_OTP, users.data?.otpNumber)
                                }
                           /* } else {
                                DriverDialog(mActivity, users.message, true)
                            }*/
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if(CommonUtils.isJSONValid(it.message)){
                            val obj = JSONObject(it.message!!)
                            if(obj.getInt("code") == 400){
                                DriverDialog(mActivity, obj.getString("message"), true)
                            }else{
                                Toast.makeText(mActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        }else{
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