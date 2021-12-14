package com.app.ia.driver.ui.notifications

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.FragmentMyOrderBinding
import com.app.ia.driver.databinding.FragmentNotificationsBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.enums.Status
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.model.NotificationResponse
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.ui.notifications.NotificationsFragment.Companion.DELETE_ALL_NOTIFICATION
import com.app.ia.driver.utils.AppConstants
import com.app.ia.driver.utils.CommonUtils
import com.app.ia.driver.utils.Resource
import com.app.ia.driver.utils.plusAssign
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.toolbar_home.view.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class NotificationsViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentNotificationsBinding

    val isItemAvailable = MutableLiveData(true)
    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)
    var isLoading = true
    var notificationListData = MutableLiveData<MutableList<NotificationResponse.Docs>>()

    fun setVariable(mBinding: FragmentNotificationsBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!

        (mActivity as HomeActivity).toolbar.imageViewDelete.setOnClickListener {
            val tivoDialog = DriverDialog(mActivity, mActivity.getString(R.string.delete_all_notification_msg), false)
            tivoDialog.setOnItemClickListener(object : DriverDialog.OnClickListener {

                override fun onPositiveClick() {
                    setupObservers(null, null, DELETE_ALL_NOTIFICATION, -1)
                }

                override fun onNegativeClick() {

                }
            })
        }
    }

    private fun getNotifications(notification_id: String?, requestParams: HashMap<String, String>?, notificationType: Int) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            when (notificationType) {
                NotificationsFragment.NOTIFICATION_LIST -> {
                    emit(Resource.success(data = baseRepository.getNotification(requestParams!!)))
                }
                NotificationsFragment.DELETE_NOTIFICATION -> {
                    emit(Resource.success(data = baseRepository.deleteNotification(notification_id!!)))
                }
                DELETE_ALL_NOTIFICATION -> {
                    emit(Resource.success(data = baseRepository.deleteAllNotification()))
                }
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setupObservers(notification_id: String?, requestParams: HashMap<String, String>?, notificationType: Int, deletedPosition: Int) {
        isLoading = true
        getNotifications(notification_id, requestParams, notificationType).observe(mBinding.lifecycleOwner!!, Observer {
            it?.let { resource ->
                mBinding.swipeRefresh.isRefreshing = false
                when (resource.status) {
                    Status.SUCCESS -> {
                        isLoading = false
                        resource.data?.let { users ->
                            //if (users.status == "success") {

                                when (notificationType) {
                                    NotificationsFragment.NOTIFICATION_LIST -> {
                                        val response = users.data as NotificationResponse
                                        isItemAvailable.value = response.docs.size > 0
                                        isLastPage.value = (currentPage.value == response.totalPages)
                                        notificationListData.plusAssign(response.docs)
                                        ((mActivity as HomeActivity).getCurrentFragment() as NotificationsFragment).mNotificationAdapter.notifyDataSetChanged()
                                        (mActivity as HomeActivity).removeNotificationBadge()
                                    }

                                    DELETE_ALL_NOTIFICATION -> {
                                        val notificationList = notificationListData.value
                                        notificationList?.clear()
                                        notificationListData.value = notificationList
                                        ((mActivity as HomeActivity).getCurrentFragment() as NotificationsFragment).mNotificationAdapter.notifyDataSetChanged()
                                        //DriverDialog(mActivity, users.message, true)
                                    }

                                    NotificationsFragment.DELETE_NOTIFICATION -> {
                                        val updatedList = notificationListData.value
                                        updatedList?.removeAt(deletedPosition)
                                        notificationListData.value = updatedList
                                        ((mActivity as HomeActivity).getCurrentFragment() as NotificationsFragment).mNotificationAdapter.notifyItemRemoved(deletedPosition)
                                        ((mActivity as HomeActivity).getCurrentFragment() as NotificationsFragment).mNotificationAdapter.notifyItemRangeChanged(deletedPosition, updatedList?.size!!)
                                    }

                                    else -> {
                                        DriverDialog(mActivity, users.message, true)
                                    }
                                }
                            /*} else {
                                DriverDialog(mActivity, users.message, true)
                            }*/
                        }
                    }

                    Status.ERROR -> {
                        isLoading = false
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