package com.app.ia.driver.ui.offers

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.FragmentMyOrderBinding
import com.app.ia.driver.databinding.FragmentNotificationsBinding
import com.app.ia.driver.databinding.FragmentOffersBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.Dispatchers

class OffersViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentOffersBinding

    val isItemAvailable = MutableLiveData(true)

    fun setVariable(mBinding: FragmentOffersBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!

    }

}