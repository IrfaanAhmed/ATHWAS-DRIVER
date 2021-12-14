package com.app.ia.driver.ui.my_profile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseFragment
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.FragmentProfileBinding
import com.app.ia.driver.ui.home.HomeActivity
import com.app.ia.driver.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File

class MyProfileFragment : BaseFragment<FragmentProfileBinding, MyProfileViewModel>(){

    private var mFragmentBinding: FragmentProfileBinding? = null
    private var mViewModel: MyProfileViewModel? = null


    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_profile

    override val viewModel: MyProfileViewModel
        get() = mViewModel!!

    companion object {
        fun newInstance(): MyProfileFragment {
            val args = Bundle()
            val fragment = MyProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val updateProfileImageListReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("Image Picked", "Image Picked")
            val path = intent!!.getStringExtra(AppConstants.EXTRA_SELECTED_PROFILE_IMAGE)
            mViewModel?.updateProfileImage(Uri.parse(path), File(path))
            /*val bitmap = BitmapFactory.decodeFile(path)
            ivProfile.setImageBitmap(bitmap)*/
        }
    }

    override fun onDestroy() {
        var localBroadcastReceiver = LocalBroadcastManager.getInstance(requireActivity())
        localBroadcastReceiver.unregisterReceiver(updateProfileImageListReceiver)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mViewModel?.setActivityNavigator(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentBinding = viewDataBinding!!
        mFragmentBinding?.lifecycleOwner = this
        mViewModel?.setVariable(mFragmentBinding!!)

        //(requireActivity() as HomeActivity).createImagePicker()

        relUserImage.setOnClickListener {
            pickFromCameraGallery()
        }

        val localBroadcastReceiver = LocalBroadcastManager.getInstance(requireActivity())
        val intentFilter = IntentFilter()
        intentFilter.addAction(AppConstants.ACTION_BROADCAST_UPDATE_PROFILE_IMAGE)
        localBroadcastReceiver.registerReceiver(updateProfileImageListReceiver, intentFilter)
    }


    private fun setViewModel() {
        val factory = ViewModelFactory(MyProfileViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(MyProfileViewModel::class.java)
    }

    fun pickFromCameraGallery() {
        (requireActivity() as HomeActivity).isLocationSelected = false
        (requireActivity() as HomeActivity).mImagePickerManager?.askForPermission()
    }

}