package com.app.ia.driver.ui.change_password

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.ActivityChangePasswordBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.enums.Status
import com.app.ia.driver.utils.CommonUtils
import com.app.ia.driver.utils.Resource
import com.app.ia.driver.utils.toast
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class ChangePasswordViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {
    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityChangePasswordBinding

    fun setVariable(mBinding: ActivityChangePasswordBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.change_password_wo_))
    }

    fun onChangePasswordClick() {
        val oldPassword = mBinding.edtTextOldPassword.text.toString()
        val newPassword = mBinding.edtTextNewPassword.text.toString()
        val confirmPassword = mBinding.edtTextConfirmPassword.text.toString()

        mBinding.tiledtedtTextNewPassword.error = null
        mBinding.tiledtedtTextNewPassword.isErrorEnabled = false

        mBinding.tiledtTextConfirmPassword.error = null
        mBinding.tiledtTextConfirmPassword.isErrorEnabled = false

        mBinding.tiledtedtTextOldPassword.error = null
        mBinding.tiledtedtTextOldPassword.isErrorEnabled = false

        /*  when {
  //            oldPassword.length < 6 || oldPassword.length > 20 -> {
  //                IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
  //            }
              newPassword.contains(" ") -> {
  //                DriverDialog(mActivity, mActivity.getString(R.string.invalid_password_format), true)
                  mBinding.tiledtedtTextNewPassword.error=mActivity.getString(R.string.invalid_password_format)
              }
              newPassword.length < 6 || newPassword.length > 20 -> {
  //                DriverDialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
                  mBinding.tiledtedtTextNewPassword.error=mActivity.getString(R.string.password_should_be_min_6_char)
              }
             newPassword.isEmpty() -> {
                // IADialog(mActivity, mActivity.getString(R.string.please_enter_new_password), true)
                 mBinding.tiledtedtTextNewPassword.error=mActivity.getString(R.string.please_enter_new_password)
             }
  //            newPassword.length < 6 -> {
  //                IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
  //            }
             confirmPassword.isEmpty() -> {
               //  IADialog(mActivity, mActivity.getString(R.string.please_enter_confirm_password), true)
                 mBinding.tiledtTextConfirmPassword.error=mActivity.getString(R.string.please_enter_confirm_password)
             }
             confirmPassword.length < 6 -> {
                // IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
                 mBinding.tiledtTextConfirmPassword.error=mActivity.getString(R.string.password_should_be_min_6_char)
             }
              confirmPassword != newPassword -> {
  //                DriverDialog(mActivity, mActivity.getString(R.string.confirm_password_should_be_same_as_new_password), true)
                  mBinding.tiledtTextConfirmPassword.error=mActivity.getString(R.string.confirm_password_should_be_same_as_new_password)
              }
              else -> {
                  val requestParams = HashMap<String, String>()
                  requestParams["old_password"] = oldPassword
                  requestParams["new_password"] = newPassword
                  changePasswordObserver(requestParams)
              }
          }*/

        var oldDone = false
        var newDone = false
        var confirmDone = false

        if (oldPassword.isEmpty()) {
            //  DriverDialog(mActivity, mActivity.getString(R.string.please_enter_old_password), true)
            mBinding.tiledtedtTextOldPassword.error =
                mActivity.getString(R.string.please_enter_old_password)
        } else if (oldPassword.length < 6 || oldPassword.length > 15) {
            mBinding.tiledtedtTextOldPassword.error =
                mActivity.getString(R.string.password_should_be_min_6_char)
            //DriverDialog(mActivity, mActivity.getString(R.string.old_password_validation_msg), true)
        }
        else{
            oldDone =true
        }
        if (newPassword.isEmpty()) {
            //  DriverDialog(mActivity, mActivity.getString(R.string.please_enter_new_password), true)
            mBinding.tiledtedtTextNewPassword.error =
                mActivity.getString(R.string.please_enter_new_password)
        } else if (newPassword.length < 6 || newPassword.length > 15) {
            mBinding.tiledtedtTextNewPassword.error =
                mActivity.getString(R.string.password_should_be_min_6_char)
        }else{
            newDone =true
        }
        if (confirmPassword.isEmpty()) {
            mBinding.tiledtTextConfirmPassword.error =
                mActivity.getString(R.string.please_enter_confirm_password)
        } else if (confirmPassword.length < 6 || confirmPassword.length > 15) {
            mBinding.tiledtTextConfirmPassword.error =
                mActivity.getString(R.string.password_should_be_min_6_char)
        } else if (confirmPassword != newPassword) {
            mBinding.tiledtTextConfirmPassword.error =
                mActivity.getString(R.string.confirm_password_should_be_same_as_new_password)
        }else{
            confirmDone =true
        }

        if (oldDone &&
            newDone &&
            confirmDone
        ) {
            val requestParams = HashMap<String, String>()
            requestParams["old_password"] = oldPassword
            requestParams["new_password"] = newPassword
            changePasswordObserver(requestParams)
        }
    }


    private fun changePassword(requestParams: HashMap<String, String>) =
        liveData(Dispatchers.Main) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = baseRepository.changePassword(requestParams)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    private fun changePasswordObserver(requestParams: HashMap<String, String>) {
        changePassword(requestParams).observe(mBinding.lifecycleOwner!!, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            //if (users.status == "success") {
                            mActivity.toast(users.message)
                            mActivity.finish()
                            //addressList.removeAt(deletedPosition.value!!)
                            //addressListResponse.value = addressList
                            /* } else {
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