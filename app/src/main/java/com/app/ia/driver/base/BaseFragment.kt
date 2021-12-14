package com.app.ia.driver.base

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.app.ia.driver.callback.GeneralCallback
import com.app.ia.driver.image_picker.ImagePickerManager
import com.app.ia.driver.image_picker.OnImagePickListener
import com.app.ia.driver.utils.LocationManager
import com.example.easywaylocation.LocationData

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : Fragment(), GeneralCallback,
    OnImagePickListener, LocationManager.CurrentLocationListener {

    private var baseActivity: BaseActivity<*, *>? = null
    private var mRootView: View? = null
    var viewDataBinding: T? = null
    private var mViewModel: V? = null
    var mImagePickerManager: ImagePickerManager? = null
    private var progressBar: View? = null
    var mLocationManager: LocationManager? = null
    private var isLocationSelected = false

    /**
     * Override for set binding variable
     * @return variable id
     */
    abstract val bindingVariable: Int

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract val viewModel: V

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            this.baseActivity = context
            context.onFragmentAttached()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mRootView = if (mRootView == null) viewDataBinding?.root else view
        mRootView?.setBackgroundColor(Color.TRANSPARENT)
        return mRootView
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding?.setVariable(bindingVariable, mViewModel)
        viewDataBinding?.executePendingBindings()
    }

    override fun showProgress() {
        baseActivity?.showProgress()
    }

    override fun hideProgress() {
        baseActivity?.hideProgress()
    }

    fun currentLocationManager(requireLastLocation: Boolean = true) {
        isLocationSelected = true
        mLocationManager = LocationManager(this, requireContext(), requireLastLocation)
        mLocationManager?.checkPermission()
    }

    fun createImagePicker() {
        mImagePickerManager = ImagePickerManager(this, requireActivity())
        mImagePickerManager?.mFragment = this
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mImagePickerManager?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onImageSelect(path: String) {

    }

    override fun onImageError(message: String) {

    }

    override fun hideKeyboard() {
        if (baseActivity != null) {
            baseActivity?.hideKeyboard()
        }
    }

    interface Callback {

        fun onFragmentAttached()

        fun onFragmentDetached(tag: String)
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