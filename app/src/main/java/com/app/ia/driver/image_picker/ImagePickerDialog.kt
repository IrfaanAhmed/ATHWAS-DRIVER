package com.app.ia.driver.image_picker

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
import com.app.ia.driver.databinding.ImagePickerDialogBinding
import kotlinx.android.synthetic.main.image_picker_dialog.*

class ImagePickerDialog(private val mContext: Activity) : DialogFragment() {

    private lateinit var mBinding: ImagePickerDialogBinding
    private lateinit var imageDialogViewModel: ImageDialogViewModel
    private var imagePickerDialogNavigator: ImagePickerDialogNavigator? = null

    companion object {
        fun getInstance(mContext: Activity): ImagePickerDialog {
            return ImagePickerDialog(mContext)
        }
    }

    fun setOnClickListener(imagePickerDialogNavigator: ImagePickerDialogNavigator) {
        this.imagePickerDialogNavigator = imagePickerDialogNavigator
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.image_picker_dialog, null, false)
        setViewModel()
        mBinding.viewModel = imageDialogViewModel
        imageDialogViewModel.setActivityNavigator(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgViewClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return mBinding.root
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ImageDialogViewModel(imagePickerDialogNavigator))
        imageDialogViewModel = ViewModelProvider(this, factory).get(ImageDialogViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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