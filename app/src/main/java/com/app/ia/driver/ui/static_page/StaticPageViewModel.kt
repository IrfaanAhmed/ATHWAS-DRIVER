package com.app.ia.driver.ui.static_page

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.*
import com.app.ia.driver.utils.AppConstants.EXTRA_WEB_VIEW_TITLE
import com.app.ia.driver.utils.AppConstants.EXTRA_WEB_VIEW_URL
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_static_page.*
import kotlinx.coroutines.Dispatchers

class StaticPageViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityStaticPageBinding

    var url = ""

    fun setVariable(mBinding: ActivityStaticPageBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    fun setIntent(intent: Intent){
        title.set(intent.extras!!.getString(EXTRA_WEB_VIEW_TITLE))
        url = intent.extras!!.getString(EXTRA_WEB_VIEW_URL) as String

        (mActivity as StaticPageActivity).webview.loadUrl(url)
    }

}