package com.app.ia.driver.ui.home

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseActivity
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.ActivityHomeBinding
import com.app.ia.driver.helper.CardDrawerLayout
import com.app.ia.driver.local.AppPreferencesHelper
import com.app.ia.driver.services.LocationUpdatesService
import com.app.ia.driver.ui.my_order.MyOrderFragment
import com.app.ia.driver.ui.my_profile.MyProfileFragment
import com.app.ia.driver.ui.notifications.NotificationsFragment
import com.app.ia.driver.ui.order_detail.OrderDetailActivity
import com.app.ia.driver.utils.*
import com.app.ia.driver.utils.AppConstants.EXTRA_SELECTED_PROFILE_IMAGE
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.toolbar_home.view.*

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeMainViewModel>() {

    var mBinding: ActivityHomeBinding? = null
    var mViewModel: HomeMainViewModel? = null

    private val myOrderFragment: Fragment = MyOrderFragment.newInstance()
    private val notificationFragment: Fragment = NotificationsFragment.newInstance()
    private val profileFragment: Fragment = MyProfileFragment.newInstance()
    private var active = myOrderFragment

    private var mDrawer: CardDrawerLayout? = null
    private var toggle: ActionBarDrawerToggle? = null

    lateinit var fm: FragmentManager

    companion object {
        const val KEY_REDIRECTION = "KEY_REDIRECTION"
        const val KEY_REDIRECTION_ID = "KEY_REDIRECTION_ID"
    }

    // A reference to the service used to get location updates.
    var mService: LocationUpdatesService? = null

    // Tracks the bound state of the service.
    private var mBound = false

    // Monitors the state of the connection to the service.
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: LocationUpdatesService.LocalBinder = service as LocationUpdatesService.LocalBinder
            mService = binder.service
            mBound = true
            mService!!.requestLocationUpdates()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mBound = false
        }
    }

    private val updateProfileListReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mViewModel?.userName!!.value = AppPreferencesHelper.getInstance().userName
            mViewModel?.userImage!!.value = AppPreferencesHelper.getInstance().userImage
            mViewModel?.isOnlineStatus!!.value = AppPreferencesHelper.getInstance().onlineStatus != 0
            if (AppPreferencesHelper.getInstance().notificationCount > 0) {
                showNotificationBadge(AppPreferencesHelper.getInstance().notificationCount)
            } else {
                removeNotificationBadge()
            }

            if (intent!!.getBooleanExtra("refresh", false)) {
                val currentFragment = getCurrentFragment()
                if (currentFragment != null) {
                    if (currentFragment is MyOrderFragment) {
                        currentFragment.resetOrderList(true)
                    } else if (currentFragment is NotificationsFragment) {
                        currentFragment.resetNotification()
                    }
                }
            }
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun getViewModel(): HomeMainViewModel {
        return mViewModel!!
    }

    override fun onDestroy() {
        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this@HomeActivity)
        localBroadcastReceiver.unregisterReceiver(updateProfileListReceiver)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        setSupportActionBar(toolbar)

        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)

        //makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)

        createImagePicker()

        toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle!!)

        drawer_layout.setViewScale(Gravity.START, 0.9f)
        drawer_layout.setRadius(Gravity.START, 5.0f)
        drawer_layout.setViewElevation(Gravity.START, 20.0f)

        fm = supportFragmentManager
        fm.beginTransaction().add(R.id.nav_host_fragment, profileFragment, "3").hide(profileFragment).commit()
        fm.beginTransaction().add(R.id.nav_host_fragment, notificationFragment, "2").hide(notificationFragment).commit()
        fm.beginTransaction().add(R.id.nav_host_fragment, myOrderFragment, "1").commit()

        val extras = intent!!.extras
        if (extras != null) {
            if (extras.containsKey(KEY_REDIRECTION)) {

                val where = extras.getString(KEY_REDIRECTION, "")
                val id = extras.getString(KEY_REDIRECTION_ID, "")
                val postIntent: Intent
                when (where.trim()) {

                    "2" -> {

                    }

                    "3" -> {
                        postIntent = Intent(this, OrderDetailActivity::class.java)
                        postIntent.putExtra("order_id", id)
                        startActivity(postIntent)
                    }

                    "4" -> {
                    }

                    else -> {
                    }
                }
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.home -> {
                    fm.beginTransaction().hide(active).show(myOrderFragment).commit()
                    setTitleAndIcon(getString(R.string.orders), true, true, false)
                    active = myOrderFragment
                }

                R.id.notification -> {
                    fm.beginTransaction().hide(active).show(notificationFragment).commit()
                    setTitleAndIcon(getString(R.string.notifications), false, false, true)
                    (notificationFragment as NotificationsFragment).resetNotification()
                    active = notificationFragment
                }

                R.id.profile -> {
                    fm.beginTransaction().hide(active).show(profileFragment).commit()
                    setTitleAndIcon(getString(R.string.profile), true)
                    //(notificationFragment as NotificationFragment).resetNotification()
                    active = profileFragment
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this@HomeActivity)
        val intentFilter = IntentFilter()
        intentFilter.addAction(AppConstants.ACTION_BROADCAST_UPDATE_PROFILE)
        localBroadcastReceiver.registerReceiver(updateProfileListReceiver, intentFilter)

        currentLocationManager(false)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(HomeMainViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(HomeMainViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (AppPreferencesHelper.getInstance().notificationCount > 0) {
            showNotificationBadge(AppPreferencesHelper.getInstance().notificationCount)
        } else {
            removeNotificationBadge()
        }
    }

    override fun onStart() {
        super.onStart()
        mLocationManager?.startLocationUpdate()
        bindService(Intent(this, LocationUpdatesService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer.
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection)
            mBound = false
        }
        super.onStop()
    }

    fun showNotificationBadge(number: Int) {
        val badge = bottomNavigationView.getOrCreateBadge(R.id.notification)
        badge.isVisible = true
        badge.number = number
        badge.backgroundColor = ContextCompat.getColor(this, R.color.red)
    }

    fun removeNotificationBadge() {
        AppPreferencesHelper.getInstance().notificationCount = 0
        bottomNavigationView.removeBadge(R.id.notification)
    }


    fun getCurrentFragment(): Fragment? {
        val fragmentManager = supportFragmentManager
        val fragments = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment != null && fragment.isVisible) return fragment
        }
        return null
    }

    fun setTitleAndIcon(title: String, switchVisibility: Boolean = false,
                        filterVisibility: Boolean = false, deleteVisibility: Boolean = false,
                        sortVisibility: Boolean = false) {
        toolbar.txtViewTitle.text = title
        if (switchVisibility) {
            toolbar.switchOnline.visible()
        } else {
            toolbar.switchOnline.gone()
        }

        if (filterVisibility) {
            toolbar.imageViewFilter.visible()
        } else {
            toolbar.imageViewFilter.gone()
        }

        if (deleteVisibility) {
            toolbar.imageViewDelete.visible()
        } else {
            toolbar.imageViewDelete.gone()
        }
        if (sortVisibility) {
            toolbar.imageViewSort.visible()
        } else {
            toolbar.imageViewSort.gone()
        }

        toolbar.imageViewMenu.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    override fun onImageSelect(path: String) {

        Log.d("Image Picked", "Image Picked Home $path")
        super.onImageSelect(path)
        val localBroadCast = LocalBroadcastManager.getInstance(this@HomeActivity)
        val intent = Intent(AppConstants.ACTION_BROADCAST_UPDATE_PROFILE_IMAGE)
        intent.putExtra(EXTRA_SELECTED_PROFILE_IMAGE, path)
        localBroadCast.sendBroadcast(intent)
    }
}