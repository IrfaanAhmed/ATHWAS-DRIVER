package com.app.ia.driver.dialog.common_dialog

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.R
import com.app.ia.driver.databinding.LayoutDialogBinding

class CommonDialog(private val mContext: Activity) : DialogFragment() {

    private var commonDialogNavigator: CommonDialogNavigator? = null
    private lateinit var mBinding: LayoutDialogBinding
    private lateinit var mCommonDialogViewModel: CommonDialogViewModel


    companion object {
        fun getInstance(mContext: Activity, bundle: Bundle): CommonDialog {
            val commonDialog = CommonDialog(mContext)
            val args = Bundle()
            args.putString("title", bundle.getString("title"))
            args.putString("message", bundle.getString("message"))
            args.putString("positiveText", bundle.getString("positiveText"))
            args.putString("negativeText", bundle.getString("negativeText"))
            commonDialog.arguments = args
            return commonDialog
        }
    }

    fun setOnClickListener(commonDialogNavigator: CommonDialogNavigator) {
        this.commonDialogNavigator = commonDialogNavigator
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_dialog, null, false)
        setViewModel()
        mBinding.viewModel = mCommonDialogViewModel
        mCommonDialogViewModel.setActivityNavigator(this)

        if (arguments != null) {
            mCommonDialogViewModel.heading.set(arguments?.getString("title"))
            mCommonDialogViewModel.message.set(arguments?.getString("message"))
            mCommonDialogViewModel.positiveText.set(arguments?.getString("positiveText"))
            mCommonDialogViewModel.negativeText.set(arguments?.getString("negativeText"))
        }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(CommonDialogViewModel(commonDialogNavigator!!))
        mCommonDialogViewModel = ViewModelProvider(this, factory).get(CommonDialogViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)

        val window = dialog?.window!!
        window.setBackgroundDrawableResource(R.color.transparent)

        val params = window.attributes
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT

        val displayMetrics = DisplayMetrics()
        mContext.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.8).toInt()
        window.setLayout(width, params.height)
    }
}