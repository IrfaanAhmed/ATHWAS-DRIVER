package com.app.ia.driver.ui.home

import android.app.Activity
import android.content.Intent
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.ActivityHomeBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.enums.Status
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.model.LoginResponse
import com.app.ia.driver.ui.home.adapter.NavigationListAdapter
import com.app.ia.driver.ui.login.LoginActivity
import com.app.ia.driver.utils.CommonUtils
import com.app.ia.driver.utils.EqualSpacingItemDecoration
import com.app.ia.driver.utils.Resource
import com.app.ia.driver.utils.startActivityWithFinish
import kotlinx.android.synthetic.main.nav_side_menu.*
import kotlinx.android.synthetic.main.toolbar_home.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class HomeMainViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityHomeBinding

    var userData = MutableLiveData<LoginResponse>()
    var userName = MutableLiveData<String>()
    var userImage = MutableLiveData<String>()
    var isOnlineStatus = MutableLiveData<Boolean>()

    fun setVariable(mBinding: ActivityHomeBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        (mActivity as HomeActivity).setTitleAndIcon(mActivity.getString(R.string.orders), true, true, false)
        userData.value = AppPreferencesHelper.getInstance().userData
        userName.value = AppPreferencesHelper.getInstance().userName
        userImage.value = AppPreferencesHelper.getInstance().userImage
        isOnlineStatus.value = AppPreferencesHelper.getInstance().onlineStatus != 0
        setNavigationAdapter()
        notificationCountObserver()
    }

    private fun setNavigationAdapter() {
        (mActivity as HomeActivity).recViewNavigation.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        (mActivity as HomeActivity).recViewNavigation.addItemDecoration(DividerItemDecoration(mActivity, LinearLayout.VERTICAL))

        val navigationAdapter = NavigationListAdapter()
        (mActivity as HomeActivity).recViewNavigation.adapter = navigationAdapter
        val menuList = ArrayList<String>()
        menuList.add(mActivity.getString(R.string.order_history))
        menuList.add(mActivity.getString(R.string.order_stats))
        menuList.add(mActivity.getString(R.string.about_us))
        menuList.add(mActivity.getString(R.string.contact_us))
        menuList.add(mActivity.getString(R.string.logout))
        navigationAdapter.submitList(menuList)
    }

    fun onSwitchOnlineStatusClick() {
        val requestParams = HashMap<String, String>()
        requestParams["status"] = if (isOnlineStatus.value == true) "0" else "1"
        setupObservers(requestParams)
    }

    fun onLogoutClick() {
        val tivoDialog = DriverDialog(mActivity, mActivity.getString(R.string.logout_alert), false)
        tivoDialog.setOnItemClickListener(object : DriverDialog.OnClickListener {
            override fun onPositiveClick() {
                logoutObserver(HashMap())
                /*AppPreferencesHelper.getInstance().clearAllPreferences()
                AppPreferencesHelper.getInstance().authToken = ""
                mActivity.startActivityWithFinish<LoginActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }*/
            }

            override fun onNegativeClick() {
            }

        })
    }
    fun logout(params: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.logout(params)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun logoutObserver(params: HashMap<String, String>) {
        logout(params).observe(mBinding.lifecycleOwner!!) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            AppPreferencesHelper.getInstance().clearAllPreferences()
                            AppPreferencesHelper.getInstance().authToken = ""
                            mActivity.startActivityWithFinish<LoginActivity> {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        }
    }

    private fun changeOnlineStatus(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.changeOnlineStatus(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun setupObservers(requestParams: HashMap<String, String>) {
        changeOnlineStatus(requestParams).observe(mBinding.lifecycleOwner!!, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            //if (users.status == "success") {
                            isOnlineStatus.value = isOnlineStatus.value != true
                            AppPreferencesHelper.getInstance().onlineStatus = if (isOnlineStatus.value == true) 1 else 0
                            (mActivity as HomeActivity).switchOnline.isChecked = isOnlineStatus.value!!
                            /*} else {
                                DriverDialog(mActivity, users.message, true)
                                (mActivity as HomeActivity).switchOnline.isChecked = isOnlineStatus.value!!
                            }*/
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        (mActivity as HomeActivity).switchOnline.isChecked = isOnlineStatus.value!!
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

    private fun getNotificationCount() = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getNotificationCount()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun notificationCountObserver() {

        getNotificationCount().observe(mBinding.lifecycleOwner!!) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            AppPreferencesHelper.getInstance().notificationCount =
                                users.data?.notificationCount!!
                            if (AppPreferencesHelper.getInstance().notificationCount > 0) {
                                (mActivity as HomeActivity).showNotificationBadge(
                                    AppPreferencesHelper.getInstance().notificationCount
                                )
                            } else {
                                (mActivity as HomeActivity).removeNotificationBadge()
                            }

                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        //baseRepository.callback.showProgress()
                    }
                }
            }
        }
    }

}