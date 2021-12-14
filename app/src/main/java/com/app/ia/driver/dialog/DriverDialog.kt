package com.app.ia.driver.dialog

import android.app.Activity
import android.os.Bundle
import com.app.ia.driver.R
import com.app.ia.driver.base.BaseActivity
import com.app.ia.driver.dialog.common_dialog.CommonDialog
import com.app.ia.driver.dialog.common_dialog.CommonDialogNavigator

class DriverDialog {

    private var onClickListener: OnClickListener? = null
    private var currentActivity: Activity? = null
    private var bundle: Bundle = Bundle()

    constructor(currentActivity: Activity, message: String, isSingleAction: Boolean) {
        this.currentActivity = currentActivity

        bundle = Bundle()
        bundle.putString("title", currentActivity.getString(R.string.app_name))
        bundle.putString("message", message)
        bundle.putString("positiveText", currentActivity.getString(R.string.ok))
        bundle.putString("negativeText", if (isSingleAction) "" else currentActivity.getString(R.string.cancel))
        showDialogPopUp()
    }

    constructor(currentActivity: Activity, title: String, message: String, isSingleAction: Boolean) {
        this.currentActivity = currentActivity

        bundle = Bundle()
        bundle.putString("title", if (title.isEmpty()) currentActivity.getString(R.string.app_name) else title)
        bundle.putString("message", message)
        bundle.putString("positiveText", currentActivity.getString(R.string.ok))
        bundle.putString("negativeText", if (isSingleAction) "" else currentActivity.getString(R.string.cancel))
        showDialogPopUp()
    }

    constructor(currentActivity: Activity, title: String, message: String, Ok: String, cancel: String, isSingleAction: Boolean) {
        this.currentActivity = currentActivity

        bundle = Bundle()
        bundle.putString("title", if (title.isEmpty()) currentActivity.getString(R.string.app_name) else title)
        bundle.putString("message", message)
        bundle.putString("positiveText", Ok)
        bundle.putString("negativeText", if (isSingleAction) "" else cancel)
        showDialogPopUp()
    }

    fun setOnItemClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    private fun showDialogPopUp() {

        val dialog: CommonDialog = CommonDialog.getInstance(currentActivity!!, bundle)
        dialog.setOnClickListener(object : CommonDialogNavigator {

            override fun onPositiveClick() {
                if (onClickListener == null) {
                    dialog.dismiss()
                } else {
                    dialog.dismiss()
                    onClickListener!!.onPositiveClick()
                }
            }

            override fun onNegativeClick() {
                if (onClickListener == null) {
                    dialog.dismiss()
                } else {
                    dialog.dismiss()
                    onClickListener?.onNegativeClick()
                }
            }
        })

        if (currentActivity is BaseActivity<*, *>) {
            dialog.show((currentActivity as BaseActivity<*, *>).supportFragmentManager, "common")
        }
    }

    interface OnClickListener {
        fun onPositiveClick()

        fun onNegativeClick()
    }
}