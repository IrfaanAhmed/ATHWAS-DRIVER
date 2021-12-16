package com.app.ia.driver.ui.my_profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.FragmentProfileBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.enums.Status
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.model.ProfileResponse
import com.app.ia.driver.ui.change_password.ChangePasswordActivity
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.ui.otp_verify.OTPVerifyActivity
import com.app.ia.driver.utils.*
import com.app.ia.driver.utils.AppConstants.EXTRA_UPDATE_PROFILE_DATA
import com.app.ia.driver.utils.CommonUtils.isValidName
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

class MyProfileViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {
    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentProfileBinding

    var userData = MutableLiveData<ProfileResponse>()

    fun setVariable(mBinding: FragmentProfileBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        setupObservers()
    }

    fun onChangePasswordClick() {
        mActivity.startActivity<ChangePasswordActivity>()
    }

    fun updateEmailAddress() {
        val emailAddress = mBinding.edtTextEmail.text.toString()

        if (emailAddress.isEmpty()) {
            DriverDialog(mActivity, mActivity.getString(R.string.please_enter_email_address), true)
        } else if (!CommonUtils.isEmailValid(emailAddress)) {
            DriverDialog(mActivity, mActivity.getString(R.string.enter_valid_email_address), true)
        } else {
            val requestParams = HashMap<String, String>()
            requestParams["field_key"] = "new_email"
            requestParams["field_value"] = emailAddress
            setupObservers(true, true, requestParams)
            (mActivity as HomeActivity).hideKeyboard()
        }

    }

    fun updateUserName() {
        mBinding.edtTextName.setText(mBinding.edtTextName.text.toString().trim())
        val name = mBinding.edtTextName.text.toString()

        mBinding.cardEdtTextName.error=null
        mBinding.cardEdtTextName.isErrorEnabled=false

        if (name.length < 3 || name.length > 30) {
//            DriverDialog(mActivity, mActivity.getString(R.string.name_validation_msg), true)
            mBinding.cardEdtTextName.error=mActivity.getString(R.string.name_validation_msg)
        } else if (ValidationUtils.isHaveLettersOnly(name)) {
//            DriverDialog(mActivity, mActivity.getString(R.string.name_valid_msg), true)
            mBinding.cardEdtTextName.error=mActivity.getString(R.string.name_valid_msg)
        } else {
            val requestParams = HashMap<String, String>()
            requestParams["field_key"] = "username"
            requestParams["field_value"] = name
            setupObservers(false, false, requestParams)
            (mActivity as HomeActivity).hideKeyboard()
        }
    }

    fun updateVehicleNumber() {
        val vehicleNumber = mBinding.edtTextVehicleNumber.text.toString()

        mBinding.cardEdtTextVehicleNumber.error=null
        mBinding.cardEdtTextVehicleNumber.isErrorEnabled=false

        if (vehicleNumber.isEmpty()) {
//            DriverDialog(mActivity, mActivity.getString(R.string.please_enter_vehicle_number), true)
            mBinding.cardEdtTextVehicleNumber.error=mActivity.getString(R.string.please_enter_vehicle_number)
        } else if (vehicleNumber.length < 2 || vehicleNumber.length > 15) {
//            DriverDialog(mActivity, mActivity.getString(R.string.vehicle_number_validation_msg), true)
            mBinding.cardEdtTextVehicleNumber.error=mActivity.getString(R.string.vehicle_number_validation_msg)
        } else {
            val requestParams = HashMap<String, String>()
            requestParams["field_key"] = "vehicle_no"
            requestParams["field_value"] = vehicleNumber
            setupObservers(false, false, requestParams)
            (mActivity as HomeActivity).hideKeyboard()
        }
    }

    fun updateMobileNumber() {
        val mobileNumber = mBinding.edtTextMobileNumber.text.toString()

        if (mobileNumber.isEmpty()) {
            DriverDialog(mActivity, mActivity.getString(R.string.please_enter_mobile_number), true)
        } else if (mobileNumber.length < 7 || mobileNumber.length > 15) {
            DriverDialog(mActivity, mActivity.getString(R.string.mobile_number_validation_msg), true)
        } else {
            val requestParams = HashMap<String, String>()
            requestParams["field_key"] = "new_phone"
            requestParams["field_value"] = mobileNumber
            setupObservers(true, false, requestParams)
            (mActivity as HomeActivity).hideKeyboard()
        }
    }

    fun updateProfileImage(fileUri: Uri, imageFile: File) {
        val selectedImageFile = CommonUtils.prepareFilePart(mActivity, "field_value", fileUri, imageFile)
        val requestParams = HashMap<String, RequestBody>()
        requestParams["field_key"] = CommonUtils.prepareDataPart("user_image")
        setupObservers(requestParams, selectedImageFile)
    }

    private fun updateProfile(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.updateProfile(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun updateProfile(partData: Map<String, RequestBody>, file: MultipartBody.Part) =
        liveData(Dispatchers.Main) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = baseRepository.updateProfile(partData, file)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    private fun getProfile() = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getProfile()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun setupObservers(isVerify: Boolean, isEmail: Boolean, requestParams: HashMap<String, String>) {
        updateProfile(requestParams).observe(mBinding.lifecycleOwner!!, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            mActivity.toast(users.message)
                            if (isVerify) {
                                mActivity.startActivity<OTPVerifyActivity> {
                                    putExtra(EXTRA_UPDATE_PROFILE_DATA, users.data)
                                    /* putExtra(AppConstants.EXTRA_COUNTRY_CODE, users.data?.countryCode)
                                     if(isEmail)putExtra(AppConstants.EXTRA_MOBILE_NUMBER, users.data?.email) else putExtra(AppConstants.EXTRA_MOBILE_NUMBER, users.data?.phone)
                                     putExtra(AppConstants.EXTRA_OTP, users.data?.otpNumber)
                                     putExtra(AppConstants.EXTRA_OTP_FOR, users.data?.otpFor)*/
                                }
                            } else {
                                setupObservers()
                            }
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (CommonUtils.isJSONValid(it.message)) {
                            val obj = JSONObject(it.message!!)
                            if (obj.getInt("code") == 400) {
                                DriverDialog(mActivity, obj.getString("message"), true)
                            } else {
                                Toast.makeText(mActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
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

    private fun setupObservers() {
        getProfile().observe(mBinding.lifecycleOwner!!, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            userData.value = users.data
                            AppPreferencesHelper.getInstance().userName = users.data!!.userName
                            AppPreferencesHelper.getInstance().userImage = users.data!!.userImageUrl
                            AppPreferencesHelper.getInstance().onlineStatus = users.data!!.onlineStatus
                            AppPreferencesHelper.getInstance().userImageUrl = users.data!!.userImageUrl
                            val localBroadCast = LocalBroadcastManager.getInstance(mActivity)
                            val intent = Intent(AppConstants.ACTION_BROADCAST_UPDATE_PROFILE)
                            localBroadCast.sendBroadcast(intent)
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (CommonUtils.isJSONValid(it.message)) {
                            val obj = JSONObject(it.message!!)
                            if (obj.getInt("code") == 400) {
                                DriverDialog(mActivity, obj.getString("message"), true)
                            } else {
                                Toast.makeText(mActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
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

    private fun setupObservers(partData: Map<String, RequestBody>, file: MultipartBody.Part) {
        updateProfile(partData, file).observe(mBinding.lifecycleOwner!!, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            mActivity.toast(users.message)
                            setupObservers()
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (CommonUtils.isJSONValid(it.message)) {
                            val obj = JSONObject(it.message!!)
                            if (obj.getInt("code") == 400) {
                                DriverDialog(mActivity, obj.getString("message"), true)
                            } else {
                                Toast.makeText(mActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {

                    }
                }
            }
        })
    }

}