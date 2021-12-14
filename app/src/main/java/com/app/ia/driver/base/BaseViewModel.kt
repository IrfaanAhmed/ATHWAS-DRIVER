package com.app.ia.driver.base

import android.app.Activity
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference

abstract class BaseViewModel : ViewModel() {

    private val isLoading = ObservableBoolean(false)
    val title = ObservableField<String>("Login")
    private var mActivity: WeakReference<Activity>? = null

    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.set(isLoading)
    }

    fun setActivityNavigator(mActivity: Activity) {
        this.mActivity = WeakReference(mActivity)
    }

    protected fun getActivityNavigator(): Activity? {
        return mActivity!!.get()
    }

    fun onBackPressed(): Boolean {
        mActivity?.get()?.onBackPressed()
        return true
    }
}
