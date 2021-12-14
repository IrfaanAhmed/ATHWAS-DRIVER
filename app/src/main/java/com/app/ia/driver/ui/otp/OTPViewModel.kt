package com.app.ia.driver.ui.otp

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.CountDownTimer
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.ActivityOtpBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.enums.Status
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.model.LoginResponse
import com.app.ia.driver.receiver.SMSReceiver
import com.app.ia.driver.spanly.Spanly
import com.app.ia.driver.spanly.clickable
import com.app.ia.driver.spanly.color
import com.app.ia.driver.spanly.font
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.ui.login.LoginActivity
import com.app.ia.driver.utils.AppConstants.EXTRA_COUNTRY_CODE
import com.app.ia.driver.utils.AppConstants.EXTRA_MOBILE_NUMBER
import com.app.ia.driver.utils.AppConstants.EXTRA_OTP
import com.app.ia.driver.utils.CommonUtils
import com.app.ia.driver.utils.Resource
import com.app.ia.driver.utils.getColorCompat
import com.app.ia.driver.utils.startActivityWithFinish
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.common_header.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class OTPViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), SMSReceiver.OTPReceiveListener {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityOtpBinding

    private var myCountDownTimer: MyCountDownTimer? = null
    private var resendTimeInSec = 120
    var isTimeFinished = MutableLiveData<Boolean>(false)
    var expireTime = MutableLiveData<String>()

    var countryCode = MutableLiveData<String>("")
    var mobileNumber = MutableLiveData<String>("")
    var otp = MutableLiveData<String>("")

    private var smsReceiver: SMSReceiver? = null

    fun setVariable(mBinding: ActivityOtpBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.otp))
        (mActivity as OTPActivity).appCompatTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.black))
        startTimer()
        didNotReceiveOtpText()
    }

    fun setIntent(intent: Intent) {
        countryCode.value = intent.getStringExtra(EXTRA_COUNTRY_CODE)
        mobileNumber.value = intent.getStringExtra(EXTRA_MOBILE_NUMBER)
        otp.value = intent.getStringExtra(EXTRA_OTP)
    }

    fun onVerifyClick() {
        if (mBinding.pinView.text!!.isNotEmpty() && mBinding.pinView.text!!.toString() == otp.value!!) {

            val requestParams = HashMap<String, String>()
            requestParams["country_code"] = countryCode.value!!
            requestParams["phone"] = mobileNumber.value!!
            requestParams["otp_number"] = mBinding.pinView.text.toString()
            requestParams["device_token"] = AppPreferencesHelper.getInstance().deviceToken
            requestParams["device_type"] = "1"
            requestParams["device_type"] = AppPreferencesHelper.getInstance().deviceId
            setupObservers(requestParams, true)
        } else {
            DriverDialog(mActivity, mActivity.getString(R.string.please_enter_otp), true)
        }
    }

    private fun startTimer() {

        if (myCountDownTimer != null) {
            myCountDownTimer?.cancel()
        }

        isTimeFinished.value = false

        myCountDownTimer = MyCountDownTimer((resendTimeInSec * 1000).toLong(), 1000)
        myCountDownTimer?.start()
    }

    inner class MyCountDownTimer internal constructor(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {
            val progress = (millisUntilFinished / 1000).toInt()
            val time = String.format(Locale("en"), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
            expireTime.value = time
            if (progress == 0) {
                isTimeFinished.value = true
            }
        }

        override fun onFinish() {
            isTimeFinished.value = true
        }
    }

    private fun startSMSListener() {
        try {
            smsReceiver = SMSReceiver()
            smsReceiver?.setOTPListener(this)
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            mActivity.registerReceiver(smsReceiver, intentFilter)
            val client = SmsRetriever.getClient(mActivity)
            val task: Task<Void> = client.startSmsRetriever()
            task.addOnSuccessListener {
                // API successfully started
            }
            task.addOnFailureListener {
                // Fail to start API
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun didNotReceiveOtpText() {

        val fontSemiBold: Typeface = ResourcesCompat.getFont(mActivity, R.font.linotte_bold)!!
        val spanly = Spanly()

        spanly.append(mActivity.getString(R.string.didn_t_receive_the_otp)).space().append(mActivity.getString(R.string.resend_otp), color(mActivity.getColorCompat(R.color.green)), font(fontSemiBold), clickable(View.OnClickListener {
            val requestParams = HashMap<String, String>()
            requestParams["country_code"] = countryCode.value!!
            requestParams["phone"] = mobileNumber.value!!
            requestParams["otp_for"] = "register"
            setupObservers(requestParams, false)
        }))

        mBinding.txtViewResendOTP.text = spanly
        mBinding.txtViewResendOTP.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onCleared() {
        super.onCleared()
        if (myCountDownTimer != null) {
            myCountDownTimer?.cancel()
        }

        if (smsReceiver != null) {
            smsReceiver?.let { mActivity.unregisterReceiver(it) }
        }
    }

    override fun onOTPReceived(otp: String?) {
        mBinding.pinView.setText(otp)
    }

    override fun onOTPTimeOut() {

    }

    override fun onOTPReceivedError(error: String?) {

    }

    private fun apiCalling(requestParams: HashMap<String, String>, verifyOTP: Boolean) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            if (verifyOTP) {
                emit(Resource.success(data = baseRepository.verifyOTP(requestParams)))
            } else {
                emit(Resource.success(data = baseRepository.resendOTP(requestParams)))
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun setupObservers(requestParams: HashMap<String, String>, verifyOTP: Boolean) {

        apiCalling(requestParams, verifyOTP).observe(mBinding.lifecycleOwner!!, androidx.lifecycle.Observer {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {

                        resource.data?.let { users ->
                            /*if (users.status == "success") {
                                if (users.data is LoginResponse) {
                                    val response = users.data as LoginResponse
                                    AppPreferencesHelper.getInstance().userData = response
                                    mActivity.startActivityWithFinish<HomeActivity> {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                } else {
                                    val response = users.data as ResendOTPResponse
                                    otp.value = response.otpNumber
                                    startTimer()
                                }
                            } else {
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

                    }
                }
            }
        })
    }
}