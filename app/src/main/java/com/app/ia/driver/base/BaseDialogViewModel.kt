package com.app.ia.driver.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference

abstract class BaseDialogViewModel : ViewModel() {

    private var mActivity: WeakReference<Fragment>? = null

    override fun onCleared() {
        super.onCleared()
    }

    fun setActivityNavigator(mActivity: Fragment) {
        this.mActivity = WeakReference(mActivity)
    }

    protected fun getActivityNavigator(): Fragment? {
        return mActivity!!.get()
    }
}