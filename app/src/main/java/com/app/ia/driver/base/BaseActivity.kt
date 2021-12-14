package com.app.ia.driver.base

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.app.ia.driver.DriverApplication
import com.app.ia.driver.R
import com.app.ia.driver.callback.GeneralCallback
import com.app.ia.driver.image_picker.ImagePickerManager
import com.app.ia.driver.image_picker.OnImagePickListener
import com.app.ia.driver.utils.CommonUtils
import com.app.ia.driver.utils.LocationManager
import com.example.easywaylocation.LocationData

abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel> : AppCompatActivity(),
    GeneralCallback,
    BaseFragment.Callback, OnImagePickListener, LocationManager.CurrentLocationListener {

    private var progressBar: View? = null
    private var mViewDataBinding: T? = null
    private var mViewModel: V? = null
    var mImagePickerManager: ImagePickerManager? = null
    var mLocationManager: LocationManager? = null
    var isLocationSelected = false

    override fun onResume() {
        super.onResume()
        DriverApplication.getInstance().setCurrentActivity(this)
    }

    override fun onPause() {
        super.onPause()
        DriverApplication.getInstance().setCurrentActivity(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        performDataBinding()
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract fun getBindingVariable(): Int

    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    fun getViewDataBinding(): T {
        return mViewDataBinding!!
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract fun getViewModel(): V

    private fun performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        this.mViewModel = if (mViewModel == null) getViewModel() else mViewModel
        mViewDataBinding?.setVariable(getBindingVariable(), mViewModel)
        mViewDataBinding?.executePendingBindings()
    }

    private fun init() {
        progressBar = layoutInflater.inflate(R.layout.layout_progress, null) as View
        val v = this.findViewById<View>(android.R.id.content).rootView
        val viewGroup = v as ViewGroup
        viewGroup.addView(progressBar)
    }

    override fun showProgress() {
        runOnUiThread {
            progressBar?.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun hideProgress() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        runOnUiThread {
            progressBar?.visibility = View.GONE
        }
    }

    fun Context.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, duration).show()
    }

    fun currentLocationManager(requireLastLocation: Boolean = true) {
        isLocationSelected = true
        mLocationManager = LocationManager(this, this, requireLastLocation)
        mLocationManager?.checkPermission()
    }

    fun createImagePicker() {
        mImagePickerManager = ImagePickerManager(this, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("Image Picked", "Image Picked Base $isLocationSelected")
        if (isLocationSelected) {
            mLocationManager?.onActivityResult(requestCode, resultCode)
        } else {
            mImagePickerManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onImageSelect(path: String) {
        Log.d("Image Picked", "Image Picked $path")
    }

    override fun onImageError(message: String) {
        Log.d("Image Picked", "Image Picked $message")
    }

    override fun hideKeyboard() {
        CommonUtils.hideSoftInput(this)
    }

    fun hideKeyboard(view: View) {
        CommonUtils.hideSoftInput(view)
    }

    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached(tag: String) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationManager != null) {
            mLocationManager?.removeLocationUpdate()
        }
    }

    override fun onBackPressed() {
        if (progressBar!!.visibility == View.GONE) {
            super.onBackPressed()
        }
    }

    override fun onCurrentLocation(latitude: Double, longitude: Double) {

    }

    override fun onLocationUpdate(locationResult: Location?) {

    }

    override fun onLocationFetchFailed() {

    }

    override fun onAddressUpdate(locationData: LocationData?) {

    }

}